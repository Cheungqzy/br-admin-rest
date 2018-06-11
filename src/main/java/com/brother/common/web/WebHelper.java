package com.brother.common.web;

import java.util.Date;

/**
 * @authi: cheungqzy
 * @create: 2018-06-12 00:37
 **/
public class WebHelper {
    public WebHelper() {
    }

    public static String toUnicode(String str) {
        char[] arChar = str.toCharArray();
        String uStr = "";
        for(int i = 0; i < arChar.length; ++i) {
            int iValue = str.charAt(i);
            if (iValue <= 256) {
                uStr = uStr + "\\" + Integer.toHexString(iValue);
            } else {
                uStr = uStr + "\\u" + Integer.toHexString(iValue);
            }
        }
        return uStr;
    }

    public static WebResult emptyResult() {
        WebResult result = new WebResult();
        result.setCode(404);
        result.setMessage("No data");
        result.setNow(new Date());
        return result;
    }

    public static <T> WebResult<T> emptyResultT() {
        WebResult<T> tWebResult = new WebResult();
        tWebResult.setCode(404);
        tWebResult.setMessage("No data");
        tWebResult.setNow(new Date());
        return tWebResult;
    }

    public static WebResult<String> messageResult(String message) {
        return messageResult(message, 200);
    }

    public static WebResult<String> messageResult(String message, int code) {
        WebResult<String> result = new WebResult();
        result.setCode(code);
        result.setMessage(message);
        result.setNow(new Date());
        return result;
    }

    public static <T> WebResult<T> dataResult(T data) {
        WebResult<T> result = new WebResult();
        result.setCode(200);
        result.setData(data);
        result.setNow(new Date());
        return result;
    }

    public static WebResult handleException(Exception e) {
        return handleException(e, false);
    }

    public static WebResult handleException(Exception e, boolean showCode) {
        WebResult result = new WebResult();
        result.setNow(new Date());
        result.setCode(500);
        result.setMessage(e.getMessage());
        return result;
    }
}

