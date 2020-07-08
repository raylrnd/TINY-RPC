package com.example.tinyrpc.protocol.impl;

import com.alibaba.fastjson.JSON;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.Request;
import com.example.tinyrpc.common.domain.RpcContext;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.common.utils.CodecSupport;
import com.example.tinyrpc.common.utils.UUIDUtils;
import com.example.tinyrpc.config.GlobalConfig;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.transport.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.example.tinyrpc.common.domain.Constants.CLIENT_SIDE;

/**
 * 原生的Invoker
 * @auther zhongshunchao
 * @date 26/06/2020 21:43
 */
public class RealInvoker implements Invoker {

    private static final Logger logger = LoggerFactory.getLogger(RealInvoker.class);

    private Endpoint endpoint;

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
    public Object invoke(Invocation invocation) throws Exception {
        invocation.getAttachments().putAll(RpcContext.getContext().getAttachments());
        if (invocation.getSide() == CLIENT_SIDE) {
            URL url = invocation.getUrl();
            if (url == null) {
                throw new IllegalStateException("###RealInvoker: url is null while invoking:" + JSON.toJSONString(invocation));
            }
            if (endpoint == null) {
                throw new IllegalStateException("###RealInvoker:endpoint is null while invoking:" + JSON.toJSONString(invocation));
            }
            Request request = new Request(UUIDUtils.getUUID(), CodecSupport.getIDByName(url.getSerialization()));
            request.setOneway(!url.isOneWay());
            request.setSerializationId(CodecSupport.getIDByName(url.getSerialization()));
            if (invocation.isCallback()) {
                Object[] arguments = invocation.getArguments();
                int callbackParamIndex = invocation.getCallbackParamIndex();
                GlobalConfig.saveCallBack(request.getRequestId(), arguments[callbackParamIndex]);
            }
            request.setData(invocation);
            Future<Object> future = endpoint.send(request);
            Object response = null;
            if (url.isAync()) {
                RpcContext.getContext().setFuture(future);
            } else {
                try {
                    if (future != null) {
                        response = future.get(invocation.getTimeout(), TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    throw new BusinessException("Fail to get result from Server when invoking invocation:" + JSON.toJSONString(invocation), e);
                }
            }
            return response;
        } else {
            Method method = ref.getClass().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            if (invocation.isCallback()) {
                Object[] arguments = invocation.getArguments();
                Class<?>[] parameterTypes = invocation.getParameterTypes();
                int callbackParamIndex = invocation.getCallbackParamIndex();
                String callbackMethod = invocation.getCallbackMethod();
                Class<?> interfaceClass = parameterTypes[callbackParamIndex];
                Object proxy = Proxy.newProxyInstance(
                        interfaceClass.getClassLoader(),
                        new Class<?>[]{interfaceClass},
                        new CallBackInvocationHandler(callbackMethod, invocation.getUrl(), endpoint)
                );
                arguments[callbackParamIndex] = proxy;
            }
            method.setAccessible(true);
            return method.invoke(ref, invocation.getArguments());
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

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void destroy() {
        if (this.endpoint != null) {
            endpoint.close();
            endpoint = null;
        }
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
