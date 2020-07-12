package com.example.tinyrpc.filter.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.RpcContext;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import static com.example.tinyrpc.common.domain.Constants.SERVER_SIDE;

/**
 * 服务端
 * @auther zhongshunchao
 * @date 03/07/2020 23:07
 */
public class ServerContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ServerContextFilter.class);

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) throws Exception {
        logger.info("###ServerContextFilter start filtering invocation" + JSON.toJSONString(invocation));
        invocation.setSide(SERVER_SIDE);
        Map<String, Object> attachments = invocation.getAttachments();
        if (attachments != null) {
            if (RpcContext.getServerContext().getAttachments() != null) {
                RpcContext.getServerContext().getAttachments().putAll(attachments);
            } else {
                RpcContext.getServerContext().setAttachments(attachments);
            }
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            RpcContext.removeServerContext();
        }
    }
}
