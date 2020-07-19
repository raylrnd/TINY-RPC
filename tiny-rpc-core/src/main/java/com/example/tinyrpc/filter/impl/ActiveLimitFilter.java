package com.example.tinyrpc.filter.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.filter.RpcStatus;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现了消费端的流量控制
 * @auther zhongshunchao
 * @date 28/06/2020 13:59
 */
public class ActiveLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ActiveLimitFilter.class);

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) throws Exception{
        logger.info("###ActiveLimitFilter start filtering invocation" + JSON.toJSONString(invocation));
        URL url = invocation.getUrl();
        Object result;
        try {
            if(RpcStatus.beginCount(url)) {
                result = invoker.invoke(invocation);
            } else {
                logger.warn(">>>the connect num is more than permitted actives!");
                return null;
            }
        } catch (Exception e) {
            logger.error("###ActiveLimitFiltercatch exception, decCount...,{}", JSON.toJSONString(invocation));
            RpcStatus.endCount(url);
            throw e;
        }
        logger.info("###ActiveLimitFilter finished, decCount");
        RpcStatus.endCount(url);
        return result;
    }
}
