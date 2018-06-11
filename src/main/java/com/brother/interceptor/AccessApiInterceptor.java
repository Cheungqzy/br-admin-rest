package com.brother.interceptor;

import com.brother.membercenter.model.entity.YhMemberOperation;
import com.brother.security.TokenRecognizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Coldmoon on 2015/11/20.
 */
public class AccessApiInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TokenRecognizer tokenRecognizer;

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        AccessRequired annotation1 = method.getAnnotation(AccessRequired.class);
        if (annotation1 != null) {
            String token = request.getParameter("access_token");
            Long memberId = tokenRecognizer.getMemberId(token, false);
            request.setAttribute("memberId", memberId);
        }

        MemberRequest annotation2 = method.getAnnotation(MemberRequest.class);
        if (annotation2 != null) {
            YhMemberOperation memberOperation = new YhMemberOperation();
            String deviceId = request.getParameter("deviceid");
            Date timestamp = getTimeStamp(request);
            memberOperation.setIp(getIpAddr(request));
            memberOperation.setTimestamp(timestamp);
            memberOperation.setDeviceId(deviceId);
            memberOperation.setPlatform(request.getParameter("platform"));
            memberOperation.setVersion(request.getParameter("v"));
            memberOperation.setChannel(request.getParameter("channel"));
            memberOperation.setRequestId(getRequestId(deviceId, timestamp));
            request.setAttribute("memberOperation", memberOperation);
        }

        return true;
    }

    private static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index].trim();
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    private static Date getTimeStamp(HttpServletRequest request) {
        String timestamp = request.getParameter("timestamp");
        if (StringUtils.isNotBlank(timestamp)) {
            return new Date(Long.valueOf(timestamp));
        }
        return null;
    }

    private static String getRequestId(String deviceId, Date date) {
        deviceId = StringUtils.isBlank(deviceId) ? StringUtils.EMPTY : deviceId;
        if (date == null) {
            return UUID.randomUUID().toString();
        }

        return MessageFormat.format("{0}-{1}", deviceId, Long.toString(date.getTime()));
    }

}
