package com.example.tinyrpc.common;

import java.io.Serializable;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:35 下午
 */
public class Request implements Serializable {

    private long requestId;
    private boolean isEvent = false;
    private boolean is2way = false;
    private int serializationId;
    private Invocation data;

    public Request(long requestId) {
        this.requestId = requestId;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
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

    public boolean isIs2way() {
        return is2way;
    }

    public void setIs2way(boolean is2way) {
        this.is2way = is2way;
    }

    public int getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(int serializationId) {
        this.serializationId = serializationId;
    }


}
