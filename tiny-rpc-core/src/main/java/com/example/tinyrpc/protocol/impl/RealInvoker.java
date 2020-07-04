package com.example.tinyrpc.protocol.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.RpcContext;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.common.utils.UUIDUtils;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.transport.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.example.tinyrpc.common.Constants.CLIENT_SIDE;

/**
 * 原生的Invoker
 * @auther zhongshunchao
 * @date 26/06/2020 21:43
 */
public class RealInvoker implements Invoker {

    private static final Logger logger = LoggerFactory.getLogger(RealInvoker.class);

    private Client client;

    private int weight;

    private Class<?> interfaceClass;

    private URL url;

    private Object ref;

    public RealInvoker(Class<?> interfaceClass, int weight, URL url) {
        this.interfaceClass = interfaceClass;
        this.weight = weight;
        this.url = url;
    }

    @Override
    public Object invoke(final Invocation invocation) {
        invocation.getAttachments().putAll(RpcContext.getContext().getAttachments());
        if (invocation.getSide() == CLIENT_SIDE) {
            URL url = invocation.getUrl();
            Request request = new Request(UUIDUtils.getUUID());
            request.setData(invocation);
            request.setIs2way(!url.isOneWay());
            request.setSerializationId(url.getSerializer());
            Future<Object> future = client.send(request);
            Object response = null;
            if (url.isAync()) {
                RpcContext.getContext().setFuture(future);
            } else {
                try {
                    if (future != null) {
                        response = future.get(invocation.getTimeout(), TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    throw new BusinessException("Fail to get result from Server when invoking invocation:" + JSON.toJSONString(invocation));
                }
            }
            return response;
        } else {
            try {
                Method method = ref.getClass().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                return method.invoke(ref, invocation.getArguments());
            } catch (Exception e) {
                throw new BusinessException("HandleRequest error, exception:" + e.getMessage());
            }
        }

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

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
