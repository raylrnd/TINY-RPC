package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.protocol.Invoker;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:25 上午
 */
//可以选择JDK、CJLIB等多种代理模式，因此用工厂模式
public abstract class AbstractProxyFactory implements ProxyFactory {


    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type) {
        return null;
    }


}
