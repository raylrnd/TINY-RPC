package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import java.lang.reflect.Proxy;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:27 上午
 */
public class JdkProxyFactory implements ProxyFactory{

    @Override
    public Object getProxy(Invoker invoker, Invocation invocation) {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{invoker.getInterface()},
                new InvokerInvocationHandler(invoker, invocation)
        );
    }

}
