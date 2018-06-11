package com.brother.common.constants;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yonghui.common.util.HttpStatus;

/**
 * @authi: cheungqzy
 * @create: 2018-06-12 00:05
 **/
public class Constant {
    static {
        HTTPCODE_OK = HttpStatus.OK.value();
        HTTPCODE_INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
        HTTPCODE_USER_ERROR = HttpStatus.BAD_REQUEST.value();
        COMMON_JSON_FEATURE = new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty};
    }
    //System error code 0 ~ 10000
    public static final int ERRORCODE_OK = 0;
    public static final int ERRORCODE_INTERNAL_SERVER_ERROR = 500;
    public static final String ERRORMESSAGE_INTERNAL_ERROR = "当前服务繁忙，请稍后再试。";
    public static final int ERRORCODE_SERVER_BUSY = 501;
    public static final String ERRORMESSAGE_SERVER_BUSY = "服务器繁忙，请稍后重试。";
    public static final int ERRORCODE_ID_GENERATOR_WORKER_ID_FULLED = 1001;
    public static final int INTERNAL_SERVER_CODE = 500;
    public static final String INTERNAL_SERVER_MESSAGE = "服务器内部错误";
    public static final int HTTPCODE_OK;
    public static final int HTTPCODE_INTERNAL_SERVER_ERROR;
    public static final int HTTPCODE_USER_ERROR;
    public static final SerializerFeature[] COMMON_JSON_FEATURE;
}
