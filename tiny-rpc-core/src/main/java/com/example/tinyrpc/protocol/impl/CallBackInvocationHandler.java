package com.example.tinyrpc.protocol.impl;

import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.transport.Endpoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CallBackInvocationHandler implements InvocationHandler {
    public CallBackInvocationHandler(String callbackMethod, URL url, Endpoint endpoint) {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
