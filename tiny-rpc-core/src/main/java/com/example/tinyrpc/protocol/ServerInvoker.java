package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.transport.Server;

/**
 * @auther zhongshunchao
 * @date 24/06/2020 16:37
**/
@Deprecated
public class ServerInvoker<T> implements Invoker{

    private Class<?> interfaceClass;

    private Server server;

    private T ref;

    @Override
    public Class<?> getInterface() {
        return interfaceClass;
    }

    @Override
    public Object invoke(Invocation invocation) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
