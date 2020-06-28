package com.example.tinyrpc.config;

import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.protocol.impl.ProtocolFilterWrapper;
import com.example.tinyrpc.proxy.JdkProxyFactory;
import com.example.tinyrpc.proxy.ProxyFactory;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 10:39
 */
//@Reference里的内容
public class ReferenceConfig {

//    private transient volatile boolean destroyed;

    private Protocol protocol = new ProtocolFilterWrapper();

    private ProxyFactory proxyFactory = new JdkProxyFactory();

    public Object getProxy(Invocation invocation) {
        Invocation.Attachments attachments = invocation.getAttachments();
        protocol = (Protocol) ExtensionLoader.getExtension(attachments.getProtocol());
        proxyFactory = (ProxyFactory) ExtensionLoader.getExtension(attachments.getProxy());
        Invoker invoker = protocol.refer(invocation);
        return proxyFactory.getProxy(invoker, invocation);
    }
}
