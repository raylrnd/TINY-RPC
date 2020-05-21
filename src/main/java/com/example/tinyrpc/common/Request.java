package com.example.tinyrpc.common;

import java.io.Serializable;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:35 下午
 */
public class Request implements Serializable {

    private long requestId;
    private boolean isEvent = false;
    private Object data;

    public Request(long requestId) {
        this.requestId = requestId;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
