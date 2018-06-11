package com.brother.filter;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by shp on 2015/11/17.
 */
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        httpServletRequest.setAttribute("_starttime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        long startTime = (Long) httpServletRequest.getAttribute("_starttime");
        long endTime = System.currentTimeMillis();
        String uri = httpServletRequest.getRequestURI();
        StringBuilder sb = new StringBuilder();
        Map<String, String[]> map = httpServletRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = "";
            for (String s : entry.getValue()) {
                value += s;
            }
            if ((uri.contains("/api/member/login") || uri.contains("/api/member/register") || uri.contains("/api/member/forgetpwd")) && key.equals("pwd")) {
                value = "***";
            }
            sb.append(key).append("=").append(value).append(",");
        }
    }
}
