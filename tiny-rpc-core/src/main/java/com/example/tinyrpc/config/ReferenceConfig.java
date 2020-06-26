package com.example.tinyrpc.config;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.protocol.RegistryProtocol;
import com.example.tinyrpc.proxy.JdkProxyFactory;
import com.example.tinyrpc.proxy.ProxyFactory;

/**
 * @auther zhongshunchao
 * @date 23/05/2020 10:39
 */
//@Reference里的内容
public class ReferenceConfig {

//    private transient volatile boolean destroyed;

    private static final Protocol REF_PROTOCOL = new RegistryProtocol();

    private static final ProxyFactory PROXY_FACTORY = new JdkProxyFactory();

    public Object getProxy(Invocation invocation) {
//        if (destroyed) {
//            throw new IllegalStateException("The invoker of ReferenceConfig has already destroyed!");
//        }
        Invoker invoker = REF_PROTOCOL.refer(invocation.getInterfaceClass());
        return PROXY_FACTORY.getProxy(invoker, invocation);
    }
}
