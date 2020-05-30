package com.example.tinyrpc.config;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 10:39
 */
//@Reference里的内容
public class ReferenceConfig {
    boolean async;
    boolean callback;
    boolean oneway;
    long timeout;
    int serializer;
    String proxy;

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isCallback() {
        return callback;
    }

    public void setCallback(boolean callback) {
        this.callback = callback;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    public int getSerializer() {
        return serializer;
    }

    public void setSerializer(int serializer) {
        this.serializer = serializer;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
}
