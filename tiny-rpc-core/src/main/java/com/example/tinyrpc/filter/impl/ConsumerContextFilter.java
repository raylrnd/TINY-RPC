package com.example.tinyrpc.filter.impl;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.RpcContext;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;

/**
 * 消费端
 * @auther zhongshunchao
 * @date 03/07/2020 22:57
 */
public class ConsumerContextFilter implements Filter {

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) {
        RpcContext.getContext().setInvocation(invocation);
        try {
            RpcContext.removeServerContext();
            return invoker.invoke(invocation);
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }
}
