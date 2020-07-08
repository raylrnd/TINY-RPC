package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.Request;
import com.example.tinyrpc.common.domain.RpcContext;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.utils.CodecSupport;
import com.example.tinyrpc.transport.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @auther zhongshunchao
 * @date 08/07/2020 08:46
 */
public class CallBackInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallBackInvocationHandler.class);

    private String callbackMethod;
    private URL url;
    private Endpoint endpoint;

    public CallBackInvocationHandler(String callbackMethod, URL url, Endpoint endpoint) {
        this.callbackMethod = callbackMethod;
        this.url = url;
        this.endpoint = endpoint;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals(callbackMethod)) {
            // 创建并初始化 RPC 请求
            logger.info("server start sending callback：{} {}", method.getDeclaringClass().getName(), method.getName());
            Request callbackRequest = new Request(RpcContext.getContext().getRequestId(), CodecSupport.getIDByName(url.getSerialization()));
            Invocation callbackInvocation = new Invocation();
            callbackInvocation.setMethodName(method.getName());
            callbackInvocation.setParameterTypes(method.getParameterTypes());
            callbackInvocation.setArguments(args);
            callbackRequest.setData(callbackInvocation);
            endpoint.sendCallBack(callbackRequest);
//        } else {
//            return method.invoke(this, args);
//        }
        }
        return null;
    }
}
