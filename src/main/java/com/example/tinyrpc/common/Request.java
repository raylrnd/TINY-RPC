package com.example.tinyrpc.common;

/**
 * @auther zhongshunchao
 * @date 2020/5/20 8:35 下午
 */
public class Request {

    private int requestId;
    private boolean isEvent = false;

    public Request(int requestId) {
        this.requestId = requestId;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }
}
