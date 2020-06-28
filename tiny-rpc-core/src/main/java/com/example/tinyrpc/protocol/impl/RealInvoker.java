package com.example.tinyrpc.protocol.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.transport.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @auther zhongshunchao
 * @date 26/06/2020 21:43
 */
public class RealInvoker implements Invoker {

    private static Logger log = LoggerFactory.getLogger(RealInvoker.class);

    private Client client;

    private int weight;

    private Class<?> interfaceClass;

    private URL url;

    public RealInvoker(Class<?> interfaceClass, int weight, URL url) {
        this.interfaceClass = interfaceClass;
        this.weight = weight;
        this.url = url;
    }

    @Override
    public Object invoke(Invocation invocation) {
        Invocation.Attachments attachments = invocation.getAttachments();
        Request request = new Request(123456789);
        request.setData(invocation);
        request.setIs2way(!attachments.isOneWay());
        request.setSerializationId(attachments.getSerializer());
        Future<Object> future = client.send(request);
        Object response = null;
        try {
            if (future != null) {
                response = future.get(900, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new BusinessException("Fail to get result from Server when invoking invocation:" + JSON.toJSONString(invocation));
        }
        return response;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Class<?> getInterface() {
        return interfaceClass;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void destroy() {
        if (this.client != null) {
            client.close();
            client = null;
        }
    }
}
