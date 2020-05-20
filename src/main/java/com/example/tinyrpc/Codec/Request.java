package com.example.tinyrpc.Codec;

/**
 * @auther zhongshunchao
 * @date 2020/5/19 6:30 下午
 */
// 封装对方发送过来的request请求
public class Request {

    private boolean isEvent = false;
    private Object data;
    private int responseId;


    public Request(int responseId) {
        this.responseId = responseId;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public boolean getEvent() {
        return isEvent;
    }

}
