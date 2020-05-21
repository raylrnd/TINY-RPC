package com.example.tinyrpc.proxy;

import com.example.tinyrpc.protocol.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:27 上午
 */
public class JdkProxyFactory extends AbstractProxyFactory {


    @Override
    public <T> T createProxy(Invoker<T> invoker) {
        return (T) Proxy.newProxyInstance(
                invoker.getInterface().getClassLoader(),
                new Class<?>[]{invoker.getInterface()},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return invokeProxy((Invoker)proxy, method, args);
                    }
                }
        );
    }
}
