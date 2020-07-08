package com.example.tinyrpc.filter.impl;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.RpcContext;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.utils.UUIDUtils;
import com.example.tinyrpc.filter.Filter;
import com.example.tinyrpc.filter.Span;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.example.tinyrpc.common.domain.Constants.SPAN_KEY;

/**
 * 链路跟踪，简单理解就是跨方法调用的日志打印和查询。TraceFilter是可以用注解上的字段配置开启还是关闭的，默认关闭
 * @auther zhongshunchao
 * @date 03/07/2020 08:48
 */
public class TraceFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    public Object invoke(Invoker invoker, Invocation invocation) throws Exception{
        logger.info("###TraceFilter Start tracing...");
        long startTime = System.currentTimeMillis();
        Object result = invoker.invoke(invocation);
        long endTime = System.currentTimeMillis();
        URL url = invocation.getUrl();
        Map<String, Object> attachments = RpcContext.getContext().getAttachments();
        Span span = (Span) attachments.get(SPAN_KEY);
        if (span == null) {
            String spanName = invocation.getServiceName() + "." + invocation.getMethodName();
            span = new Span(UUIDUtils.getUUID(), url.getAddress(), startTime, endTime, spanName, invocation.getSide());
        } else {
            span.incSpanId();
        }
        attachments.put(SPAN_KEY, span);
        logger.info(">>>Tracing Result:{}", span);
        return result;
    }
}
