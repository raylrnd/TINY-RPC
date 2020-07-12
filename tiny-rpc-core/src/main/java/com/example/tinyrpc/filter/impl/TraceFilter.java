package com.example.tinyrpc.filter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
        Map<String, Object> attachments = RpcContext.getServerContext().getAttachments();
        // 这里默认的序列化方式为fastjson
        Span parentSpan;
        Object obj = attachments.get(SPAN_KEY);
        if (obj instanceof Span) {
            parentSpan = (Span) obj;
        } else {
            JSONObject parentSpanObject = (JSONObject) attachments.get(SPAN_KEY);
            parentSpan = JSONObject.toJavaObject(parentSpanObject, Span.class);
        }
        URL url = invocation.getUrl();
        String spanName = invocation.getServiceName() + "#" + invocation.getMethodName();
        Span curSpan;
        if (parentSpan == null) {
            curSpan = new Span(UUIDUtils.getUUID(), url.getAddress(), spanName, invocation.getSide());
            curSpan.setSpanId("0");
        } else {
            String currentSpanNum = parentSpan.incAndGetSpanid();
            curSpan = new Span(parentSpan.getTraceId(), url.getAddress(), spanName, invocation.getSide());
            curSpan.setSpanId(currentSpanNum);
            logger.info("###Tracing Result, parentSpan == {}, thread id == {}", JSON.toJSONString(parentSpan), Thread.currentThread().getId());
        }
        attachments.put(SPAN_KEY, curSpan);
        logger.info("###Tracing Result, curSpan == {}, thread id == {}", JSON.toJSONString(curSpan), Thread.currentThread().getId());
        // start invoke
        long startTime = System.currentTimeMillis();
        Object result = invoker.invoke(invocation);
        long endTime = System.currentTimeMillis();
        logger.info("###Tracing Result, invoke costs time: " + (endTime - startTime));
        // end invoke
        // 恢复parent span
        attachments.put(SPAN_KEY, parentSpan);
        return result;
    }
}
