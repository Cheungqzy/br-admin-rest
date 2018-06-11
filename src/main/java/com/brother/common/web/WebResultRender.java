package com.brother.common.web;

/**
 * @authi: cheungqzy
 * @create: 2018-06-12 00:30
 **/

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.brother.common.constants.Constant;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebResultRender {
    public static final SerializerFeature[] JSON_FEATURE;
    private static final Pattern CHINESE_PATTERN;

    public WebResultRender() {
    }

    public static void Ok(Object data, HttpServletResponse response) {
        WebResult ret = new WebResult(0, "", data);
        render(ret, Constant.HTTPCODE_OK, response);
    }

    public static void render(WebResult obj, int httpCode, HttpServletResponse response) {
        renderRaw(JSON.toJSONString(obj, JSON_FEATURE), httpCode, response);
    }

    public static void renderObject(Object obj, int httpCode, HttpServletResponse response) {
        renderRaw(JSON.toJSONString(obj, JSON_FEATURE), httpCode, response);
    }

    public static void renderRaw(String json, HttpServletResponse response) {
        renderRaw(json, Constant.HTTPCODE_OK, response);
    }

    public static void renderRaw(String json, int httpCode, HttpServletResponse response) {
        PrintWriter out = null;

        try {
            response.setStatus(httpCode);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Matcher m = CHINESE_PATTERN.matcher(json);
            StringBuffer res = new StringBuffer();

            while(m.find()) {
                m.appendReplacement(res, "\\" + WebHelper.toUnicode(m.group()));
            }

            m.appendTail(res);
            out = response.getWriter();
            out.write(res.toString());
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }

        }

    }

    static {
        JSON_FEATURE = new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect};
        CHINESE_PATTERN = Pattern.compile("[一-龥]");
    }
}

