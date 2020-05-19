package com.example.tinyrpc.Codec;

/**
 * @auther zhongshunchao
 * @date 2020/5/19 6:30 下午
 */
//封装对方发送过来的request请求
public class Request {
    private boolean mEvent = false;
    private Object mData;

    public Request(long responseId) {
        this.responseId = responseId;
    }

    private long responseId;

}
