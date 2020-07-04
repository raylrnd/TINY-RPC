package com.example.tinyrpc.filter.impl;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.RpcContext;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;
import java.util.Map;
import static com.example.tinyrpc.common.Constants.SERVER_SIDE;

/**
 * 服务端
 * @auther zhongshunchao
 * @date 03/07/2020 23:07
 */
public class ContextFilter implements Filter {

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) {
        invocation.setSide(SERVER_SIDE);
        Map<String, Object> attachments = invocation.getAttachments();
        if (attachments != null) {
            if (RpcContext.getContext().getAttachments() != null) {
                RpcContext.getContext().getAttachments().putAll(attachments);
            } else {
                RpcContext.getContext().setAttachments(attachments);
            }
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            RpcContext.removeContext();
            RpcContext.removeServerContext();
        }
    }
}
