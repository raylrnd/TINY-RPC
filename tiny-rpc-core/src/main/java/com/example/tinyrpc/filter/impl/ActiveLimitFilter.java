package com.example.tinyrpc.filter.impl;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.filter.RPCStatus;
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
        String serviceName = invocation.getServiceName();
        String methodName = invocation.getMethodName();
        String address = invoker.getUrl().getAddress();
        Object result;
        try {
            logger.info("starting,incCount...,{}", invocation);
            RPCStatus.incCount(serviceName, methodName, address);
            result = invoker.invoke(invocation);
        } catch (Exception e) {
            logger.info("catch exception,decCount...,{}", invocation);
            RPCStatus.decCount(serviceName, methodName, address);
            throw e;
        }
        logger.info("finished,decCount...,{}", invocation);
        RPCStatus.decCount(serviceName, methodName, address);
        return result;
    }
}
