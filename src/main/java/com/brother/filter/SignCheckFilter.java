package com.brother.filter;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.brother.common.constants.Constant;
import com.brother.common.web.RequestContext;
import com.brother.common.web.WebResult;
import com.brother.common.web.WebResultRender;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by shp on 2015/10/28.
 */
public class SignCheckFilter extends OncePerRequestFilter {

    private static Logger logger = LoggerFactory.getLogger(SignCheckFilter.class);

    private static boolean isLogContext = false;
    private final String SECRET = "YONGHUI601933";
    private final int interval = 5; // MINUTE
    private final boolean isTestEnv = isTestEnv();

    public SignCheckFilter() {
        super();
        Resource resource = null;
        Properties props = null;
        try {
            resource = new ClassPathResource("/config.properties");
            props = PropertiesLoaderUtils.loadProperties(resource);
            String logContext = (String) props.get("isLogContext");
            isLogContext = Boolean.valueOf(logContext);


        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = httpServletRequest.getRequestURI();

        if (uri.contains("/api/image/multipart")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        try {
            BodyRequestWrapper requestWrapper = new BodyRequestWrapper(
                    httpServletRequest);
            Map<String, String[]> params = requestWrapper.getParameterMap();

            // 设置请求requestId，uri到threadLocal变量中
            RequestContext reqContext = (RequestContext) RequestContext
                    .getCurr().get();
            if (reqContext == null) {
                reqContext = new RequestContext();
                reqContext.setReqId(UUID.randomUUID().toString());
                reqContext.setTimestampStr(requestWrapper
                        .getParameter("timestamp"));
                reqContext.setUri(uri);
                RequestContext.getCurr().set(reqContext);
            }

            if (uri.contains("/static/") || uri.contains("/h5/")
                    || uri.contains("/api/backend-cache")
                    || uri.contains("/api/image/upload")
                    || uri.contains("/api/logistics/third-party/dada/callback")
                    || uri.contains("/api/search/indexAll")
                    || uri.contains("/api/search/indexSpatial")
                    || uri.contains("/api/search/reIndexShopSku")
                    || uri.contains("/api/pay/sPayResult")
                    || uri.contains("/api/pay/aPayResult")
                    || uri.contains("/api/pay/wPayResult")
                    || uri.contains("/api/pay/uPayResult")
                    || uri.contains("/api/search/indexProduct")
                    || uri.contains("/api/search/productslist")
                    || uri.contains("/api/yunqing/outstock/feedback")
                    || uri.contains("/api/yunqing/instock/feedback")
                    || uri.contains("/api/logistics/cwb/sendTrackingInfo")
                    || uri.contains("/api/search/importkeywords") || isTestEnv) {
                filterChain.doFilter(requestWrapper, httpServletResponse);
                return;
            }

            String access_token = requestWrapper.getParameter("access_token");
            String platform = requestWrapper.getParameter("platform");
            String channel = requestWrapper.getParameter("channel");
            String v = requestWrapper.getParameter("v");
            String timestampStr = requestWrapper.getParameter("timestamp");
            Long serverTime = System.currentTimeMillis();
            String clientSign = requestWrapper.getParameter("sign");

            BufferedReader reader = requestWrapper.getReader();
            StringBuilder sb = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
            String json = sb.toString();
            if (json.endsWith("\n")) {
                json = json.substring(0, json.length() - 1);
            }
            // System.out.println("------request json is ------"+json);
            // logger.info("------request context------"+json);
            TreeMap<String, String[]> treeMap = new TreeMap();
            treeMap.putAll(params);
            treeMap.remove("sign");

            String serverSign = toMD5Sign(treeMap, json, SECRET);

            // 校验放在这里，以便于获取请求上下文
            if (StringUtils.isBlank(platform)) {
            }

            if (StringUtils.isBlank(channel)) {

            }
            if (StringUtils.isBlank(v)) {
            }

            if (StringUtils.isBlank(timestampStr)) {
            }

            Long clientTime = NumberUtils.toLong(timestampStr, 0L);

            //Check sign only if client time is greater than 0. because some time client didn't pass correct timestamp
            if (clientTime > 0L && !uri.contains("/api/commonconfig") && !uri.contains("/api/patch") && Math.abs(
                    serverTime - clientTime) > interval * 60 * 1000) {
            }

            if (!serverSign.equalsIgnoreCase(clientSign)) {
            }
            filterChain.doFilter(requestWrapper, httpServletResponse);
        } catch (Exception e) {
            WebResult result = new WebResult(Constant.INTERNAL_SERVER_CODE, Constant.INTERNAL_SERVER_MESSAGE, (Object) null);
            logger.error("{} {}", e.getMessage(), JSON.toJSONString(result));
            WebResultRender.render(result, Constant.HTTPCODE_USER_ERROR, httpServletResponse);
        } finally {
            // 清楚当前线程的数据
            RequestContext.getCurr().set(null);
            RpcContext.removeContext();
        }

    }

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
                default:
                    return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    private String toMD5Sign(TreeMap<String, String[]> params, String json,
                             String secret) {
        if (MapUtils.isEmpty(params)) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(secret);
        StringBuilder input = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = "";
            for (String s : entry.getValue()) {
                value += s;
            }
            sb.append(key).append(value);
            input.append(key).append(":").append(value).append(",");
        }
        sb.append(json);
        RequestContext reqContext = (RequestContext) RequestContext.getCurr()
                .get();
        String inputStr = input.toString();
        if (StringUtils.isNotBlank(inputStr)) {
            inputStr = inputStr.substring(0, inputStr.length() - 1);
        }
        inputStr = "{\"" + "params\":\"" + inputStr + "\"" + "," + "\"body\":" + (StringUtils.isBlank(json) ? null : json) + "}";
        reqContext.setRequestCtx(inputStr);
        //System.out.println(sb);
        // System.out.println(json);
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }

}
