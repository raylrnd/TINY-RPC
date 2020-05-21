package com.example.tinyrpc.proxy;

import com.example.tinyrpc.protocol.Invoker;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:24 上午
 */
public interface ProxyFactory {

    <T> T createProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type);
}
