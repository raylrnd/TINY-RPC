package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.transport.Client;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:14 上午
 */
class InjvmInvoker<T> extends AbstractInvoker<T> {


    public InjvmInvoker(Class interfaceClass, Client client) {
        super(interfaceClass, client);
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return null;
    }
}
