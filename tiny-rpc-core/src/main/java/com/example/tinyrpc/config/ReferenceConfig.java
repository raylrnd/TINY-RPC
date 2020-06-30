package com.example.tinyrpc.config;

import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.proxy.ProxyFactory;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 10:39
 */
//@Reference里的内容
public class ReferenceConfig {

    public Object getProxy(Invocation invocation) {
        URL url = invocation.getUrl();
        Protocol protocol = ExtensionLoader.getExtensionLoader().getExtension(Protocol.class, url.getProtocol());
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader().getExtension(ProxyFactory.class, url.getProxy());
        Invoker invoker = protocol.refer(invocation);
        return proxyFactory.getProxy(invoker, invocation);
    }
}
