package com.brother.common.web;

import java.io.Serializable;

/**
 * @authi: cheungqzy
 * @create: 2018-06-12 00:21
 **/
public class RequestContext implements Serializable {
    private static final ThreadLocal curr = new ThreadLocal();
    private static final long serialVersionUID = 1L;
    private String reqId;
    private Long traceId;
    private long timestamp;
    private String timestampStr;
    private String requestCtx;
    private String uri;

    public RequestContext() {
    }

    public static ThreadLocal getCurr() {
        return curr;
    }

    public Long getTraceId() {
        return this.traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public String getTimestampStr() {
        return this.timestampStr;
    }

    public void setTimestampStr(String timestampStr) {
        this.timestampStr = timestampStr;
    }

    public String getReqId() {
        return this.reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRequestCtx() {
        return this.requestCtx;
    }

    public void setRequestCtx(String requestCtx) {
        this.requestCtx = requestCtx;
    }
}
