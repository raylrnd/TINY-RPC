package com.example.tinyrpc.filter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.tinyrpc.common.domain.Constants;
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
        Map<String, Object> attachments = RpcContext.getContext().getAttachments();
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
            curSpan = new Span(UUIDUtils.getUUID(), url.getAddress(), spanName, invocation.getSide(), "0");
            parentSpan = curSpan;
            parentSpan.incCurrentSpanNum();
            logger.warn("###Tracing Result, curSpan == {}, thread id == {}", JSON.toJSONString(curSpan), Thread.currentThread().getId());
        } else {
            curSpan = new Span(parentSpan.getTraceId(), url.getAddress(), spanName, invocation.getSide(), parentSpan.getSpanId() + "." + parentSpan.getCurrentSpanNum());
            if (invocation.getSide() == Constants.SERVER_SIDE) {
                logger.warn("###Tracing Result, curSpan == {}, thread id == {}", JSON.toJSONString(curSpan), Thread.currentThread().getId());
            } else {
                parentSpan.incCurrentSpanNum();
                curSpan = parentSpan;
            }
        }
        attachments.put(SPAN_KEY, curSpan);
        // start invoke
        Object result = invoker.invoke(invocation);
        // end invoke
        // 恢复parent span
        attachments.put(SPAN_KEY, parentSpan);
        return result;
    }
}
