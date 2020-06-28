package com.example.tinyrpc.filter;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zhongshunchao
 * @date 28/06/2020 13:56
 */
public class LogFilter implements Filter{

    private static Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) {
        log.info("LogFilter start invoking invocation{}", invocation);
        Object result = invoker.invoke(invocation);
        log.info("LogFilter end!");
        return result;
    }
}
