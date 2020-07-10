package com.example.tinyrpc.config;

import com.example.tinyrpc.common.extension.ExtensionLoader;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.protocol.impl.InvokerClientWrapper;
import com.example.tinyrpc.proxy.ProxyFactory;

/**
 * ReferenceConfig的getProxy()方法返回的是InvokerClientWrapper包装类
 * @see InvokerClientWrapper
 * @auther zhongshunchao
 * @date 23/05/2020 10:39
 */
public class ReferenceConfig {

    public Object getProxy(Invocation invocation) {
        URL url = invocation.getUrl();
        Protocol protocol = ExtensionLoader.getExtensionLoader().getExtension(Protocol.class, url.getProtocol());
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader().getExtension(ProxyFactory.class, url.getProxy());
        Invoker invoker = protocol.refer(invocation);
        Object proxy = proxyFactory.getProxy(invoker, invocation);
        if (proxy == null) {
            throw new IllegalStateException("getProxy error :proxy == null");
        }
        return proxy;
    }
}
