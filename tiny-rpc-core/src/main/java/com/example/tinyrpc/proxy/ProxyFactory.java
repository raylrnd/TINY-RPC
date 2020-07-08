package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.extension.SPI;
import com.example.tinyrpc.protocol.Invoker;

/**
 * @auther zhongshunchao
 * @date 19/06/2020 23:54
 */
@SPI("jdk")
public interface ProxyFactory {

    Object getProxy(Invoker invoker, Invocation invocation);

}
