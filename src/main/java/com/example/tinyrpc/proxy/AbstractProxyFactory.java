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

    public Object invokeProxy(Invoker invoker, Method method, Object[] args) {
//        if ("toString".equals(methodName) && parameterTypes.length == 0) {
//            return invoker.toString();
//        }
//        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
//            return invoker.hashCode();
//        }
//        if ("equals".equals(methodName) && parameterTypes.length == 1) {
//            return invoker.equals(args[0]);
//        }
        Request request = buildRequest(method, args);
        return request;
    }

    private Request buildRequest(Method method, Object[] args) {
        Invocation invocation = new Invocation();
        invocation.setClassName(method.getDeclaringClass().getName());
        invocation.setMethodName(method.getName());
        invocation.setParameterTypes(method.getParameterTypes());
        invocation.setParameters(args);
        Request request = new Request(UUID.randomUUID().getLeastSignificantBits());
        request.setData(invocation);
        return request;
    }
    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type) {
        return null;
    }


}
