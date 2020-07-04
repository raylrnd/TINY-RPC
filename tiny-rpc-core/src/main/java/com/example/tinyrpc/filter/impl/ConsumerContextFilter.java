package com.example.tinyrpc.filter.impl;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.RpcContext;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;
import static com.example.tinyrpc.common.Constants.CLIENT_SIDE;

/**
 * 消费端。分两种情况，1、属于第一个调用端 2、injvm，这两种情况都不用更新RpcContext中的Attachments
 * @auther zhongshunchao
 * @date 03/07/2020 22:57
 */
public class ConsumerContextFilter implements Filter {

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) {
        invocation.setSide(CLIENT_SIDE);
        RpcContext.getContext().setInvocation(invocation);
        try {
            RpcContext.removeServerContext();
            return invoker.invoke(invocation);
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }
}
