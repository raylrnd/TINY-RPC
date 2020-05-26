package com.example.tinyrpc.config;

import com.example.tinyrpc.proxy.JavassistProxyFactory;
import com.example.tinyrpc.proxy.JdkProxyFactory;
import com.example.tinyrpc.proxy.ProxyFactory;
import com.example.tinyrpc.serialization.Serializer;
import com.example.tinyrpc.serialization.serializer.HessianSerializer;
import com.example.tinyrpc.serialization.serializer.ProtostuffSerializer;

import java.util.Objects;

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
    String serializer;
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


    public ProxyFactory getProxyFactory() {
        switch (proxy) {
            case "jdk":
                return new JdkProxyFactory();
            case "javassist":
                return new JavassistProxyFactory();
        }
        return null;
    }

    public Serializer getSerializer() {
        switch (serializer) {
            case "hessian":
                return new HessianSerializer();
            case "protobuff":
                return new ProtostuffSerializer();
        }
        return null;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
}
