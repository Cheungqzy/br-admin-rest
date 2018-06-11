package com.brother.filter;

import java.io.IOException;
import java.util.Properties;

import javax.com.brother.common.exception.log.ExceptionLogger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.dubbo.rpc.RpcContext;
import com.brother.common.log.Logger;
import com.brother.common.log.LoggerFactory;
import com.brother.trace.BinaryAnnotation;
import com.brother.trace.Endpoint;
import com.brother.trace.Span;
import com.brother.trace.agent.Tracer;
import com.brother.trace.agent.support.TracerUtils;
import com.brother.trace.dubbo.HydraFilter;

/**
 * 服务端记录
 * 
 * @author yangbutao
 * 
 */
public class YHTraceFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger("YHTraceFilter");

    // 自动注入
	private Tracer tracer = null;
	private static boolean isTraceStart;
	private final boolean isTestEnv = isTestEnv();

	private boolean isTestEnv() {
	        try {
	            ClassPathResource resource = new ClassPathResource(
	                    "/config.properties");
	            Properties props = PropertiesLoaderUtils.loadProperties(resource);
	            String ENV = (String) props.get("env");
	            switch (ENV) {
	                case "dev":
	                    return true;
	                case "prod":
	                    return true;
	                case "stage":
	                    return true;
	                case "integration":
	                    return true;
	                default:
	                    return false;
	            }
	        } catch (IOException e) {
	            ExceptionLogger.log(e, null);
	            return false;
	        }
	    }
	  
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//如果是测试环境，则不运行trace
		/*if(isTestEnv){
			filterChain.doFilter(request, response);
			return;
		}*/
		
		if (tracer == null) {
			tracer=HydraFilter.getTracer();
			logger.info("##########tracer is null");
			filterChain.doFilter(request, response);
		}
		// 取得uri
		String serviceId = request.getRequestURI().substring(1).replace('/', '.');
		//serviceId=tracer.getServiceId(serviceId);
		serviceId=Tracer.getTracer().getServiceId(serviceId);
		if (!isTraceStart) {
			Tracer.startTraceWork();
			isTraceStart = true;
		}

		long start = System.currentTimeMillis();
		RpcContext context = RpcContext.getContext();
		boolean isConsumerSide = context.isConsumerSide();
		Span span = null;
		Endpoint endpoint = null;
		try {
			endpoint = tracer.newEndPoint();
			endpoint.setIp(context.getLocalAddressString());
			endpoint.setPort(context.getLocalPort());
			Long traceId = null;
			Long parentId = null;
			Long spanId = tracer.genSpanId();
			span = tracer.genSpan(traceId, parentId, spanId, serviceId, true,
					serviceId);
			invokerBefore(span, endpoint, start);// 记录annotation
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			catchException(e, endpoint);
			throw e;
		} finally {
			if (span != null) {
				long end = System.currentTimeMillis();
				invokerAfter(endpoint, span, end, isConsumerSide);// 调用后记录annotation
			}
		}

		// after

	}

	
	private void invokerBefore(Span span, Endpoint endpoint, long start) {
		//:no logger
		//logger.info("--------------before invoke-----------" + span.toString());
		
		tracer.clientSendRecord(span, endpoint, start);
		 
		tracer.serverReceiveRecord(span, endpoint, start+2);
		// request调用进入前，设置parent span
		tracer.setParentSpan(span);
	}

	private void invokerAfter(Endpoint endpoint, Span span, long end,
			boolean isConsumerSide) {
		//:no logger
		//		logger.info("--------------after invoke-----------" + span.toString());
//记录服务端的ss
		tracer.serverSendRecord(span, endpoint, end);
		tracer.removeParentSpan();
		//：记录客户端的cr
	     tracer.clientReceiveRecord(span, endpoint, end+3);
		
	}

	private void catchException(Throwable e, Endpoint endpoint) {
		BinaryAnnotation exAnnotation = new BinaryAnnotation();
		exAnnotation.setKey(TracerUtils.EXCEPTION);
		exAnnotation.setValue(e.getMessage());
		exAnnotation.setType("ex");
		exAnnotation.setHost(endpoint);
		tracer.addBinaryAnntation(exAnnotation);
	    //:no logger
		//	logger.error("--------------exception--------" + e.getMessage());

	}

	// setter
	public void setTracer(Tracer tracer) {
		this.tracer = tracer;
	}

}
