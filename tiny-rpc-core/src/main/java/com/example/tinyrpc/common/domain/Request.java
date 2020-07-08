package com.example.tinyrpc.common.domain;

import java.io.Serializable;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:35 下午
 */
public class Request implements Serializable {

    private long requestId;
    private boolean event = false;
    private boolean oneway = false;
    private byte serializationId = 0x00;
    private Invocation data;

    public Request(long requestId, byte serializationId) {
        this.requestId = requestId;
        this.serializationId = serializationId;
    }


    public Invocation getData() {
        return data;
    }

    public void setData(Invocation data) {
        this.data = data;
    }

    public long getRequestId() {
        return requestId;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public byte getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(byte serializationId) {
        this.serializationId = serializationId;
    }
}
