package com.example.tinyrpc.proxy.impl;


import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.proxy.InvokerInvocationHandler;
import com.example.tinyrpc.proxy.JavassistProxy;
import com.example.tinyrpc.proxy.ProxyFactory;

/**
 * @auther zhongshunchao
 * @date 05/07/2020 17:05
 */
/*
 * 这种方式不需要事先创建
 * 要代理的对象
 *
 * */
public class JavassistProxyFactory implements ProxyFactory {

    @Override
    public Object getProxy(Invoker invoker, Invocation invocation) {
        try {
            return JavassistProxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), invoker.getInterface(), new InvokerInvocationHandler(invoker, invocation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

