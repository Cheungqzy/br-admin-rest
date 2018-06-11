package com.brother.common.web;

import com.brother.common.constants.Constant;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @authi: cheungqzy
 * @create: 2018-06-12 00:03
 **/
public class WebResult<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    private Date now;

    public WebResult() {
        super();
    }

    public WebResult(int code, String message, T data) {
        super();
        this.code = code;
        this.data = data;
        this.message = message;
        this.now = new Date();
    }

    public WebResult(T data) {
        super();
        this.code = Constant.ERRORCODE_OK;
        this.message = StringUtils.EMPTY;
        this.data = data;
        this.now = new Date();
    }

    public static <T extends Serializable> WebResult<T> ok(T data) {
        WebResult<T> ret = new WebResult<>(Constant.ERRORCODE_OK, "", data);
        return ret;
    }

    public static WebResult ok(Object data) {
        WebResult<Object> ret = new WebResult<>(Constant.ERRORCODE_OK, "", data);
        return ret;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T data() {
        return (T) data;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}