package com.example.tinyrpc.cluster;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:10 上午
 */
public abstract class AbstractClusterInvoker<T> implements Invoker {
    @Override
    public Class getInterface() {
        return null;
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return null;
    }
}
