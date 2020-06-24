package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;

/**
 * @auther zhongshunchao
 * @date 19/06/2020 23:54
 */
public interface ProxyFactory {

    Object getProxy(Invoker invoker, Invocation invocation);

}
