package com.example.tinyrpc.filter;

import java.io.Serializable;

/**
 * @auther zhongshunchao
 * @date 03/07/2020 23:26
 */
public class Span implements Serializable {
    private long traceId;                                   // 一次请求的全局唯一id
    private String spanId = "0";                                    // 调用关系id, 标识一次trace中的某一次rpc调用, 签名方式命名, EG : 0, 0.1, 0.2, 01.1
    private transient int currentSpanNum;            // 标识分配到了第几个span, 用于生成调用下游的spanId
    private String spanName;                                   // 调用接口的Class Name + "." + Method Name
//    private transient Span parentSpan;                               // 上游的span
    /**
     * 远端的appKey, ip, port 信息
     */
    private String remoteAddress;
    private int side;                                // 是client span 还是 server span, 0 client, 1 server
    /**
     * 服务返回状态
     */
    private String status;
    private boolean async = false;                                  // 异步标识

    public Span(long traceId, String remoteAddress, String spanName, int side) {
        this.traceId = traceId;
        this.remoteAddress = remoteAddress;
        this.spanName = spanName;
        this.side = side;
    }

    public String getSpanId() {
        return spanId;
    }

    public String incAndGetSpanid() {
        return spanId + "." + currentSpanNum++;
    }

    public long getTraceId() {
        return traceId;
    }

    public void setTraceId(long traceId) {
        this.traceId = traceId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getSpanName() {
        return spanName;
    }

    public void setSpanName(String spanName) {
        this.spanName = spanName;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
