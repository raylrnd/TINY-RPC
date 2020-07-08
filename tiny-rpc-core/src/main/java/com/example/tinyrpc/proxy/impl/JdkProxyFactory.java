package com.example.tinyrpc.proxy.impl;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.proxy.InvokerInvocationHandler;
import com.example.tinyrpc.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:27 上午
 */
public class JdkProxyFactory implements ProxyFactory {

    @Override
    public Object getProxy(Invoker invoker, Invocation invocation) {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{invoker.getInterface()},
                new InvokerInvocationHandler(invoker, invocation)
        );
    }

}
