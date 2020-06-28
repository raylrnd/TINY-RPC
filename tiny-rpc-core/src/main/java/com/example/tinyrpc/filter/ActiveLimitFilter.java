package com.example.tinyrpc.filter;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zhongshunchao
 * @date 28/06/2020 13:59
 */
public class ActiveLimitFilter implements Filter{

    private static Logger log = LoggerFactory.getLogger(ActiveLimitFilter.class);
    @Override
    public Object invoke(Invoker invoker, Invocation invocation) {
        String serviceName = invocation.getServiceName();
        String methodName = invocation.getMethodName();
        String address = invoker.getUrl().getAddress();
        Object result;
        try {
            log.info("starting,incCount...,{}", invocation);
            RPCStatus.incCount(serviceName, methodName, address);
            result = invoker.invoke(invocation);
        } catch (Exception e) {
            log.info("catch exception,decCount...,{}", invocation);
            RPCStatus.decCount(serviceName, methodName, address);
            throw e;
        }
        log.info("finished,decCount...,{}", invocation);
        RPCStatus.decCount(serviceName, methodName, address);
        return result;
    }
}
