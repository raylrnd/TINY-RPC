package com.example.tinyrpc.filter.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.RpcContext;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.example.tinyrpc.common.domain.Constants.CLIENT_SIDE;

/**
 * 消费端。分两种情况，1、属于第一个调用端 2、injvm，这两种情况都不用更新RpcContext中的Attachments
 * 消费端的filter执行顺序：ConsumerContextFilter -> ActiveLimitFilter -> TraceFilter
 * @auther zhongshunchao
 * @date 03/07/2020 22:57
 */
public class ConsumerContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerContextFilter.class);

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) throws Exception {
        logger.info("###ConsumerContextFilter start filtering invocation" + JSON.toJSONString(invocation));
        invocation.setSide(CLIENT_SIDE);
        RpcContext.getContext().setInvocation(invocation);
        return invoker.invoke(invocation);
    }
}
