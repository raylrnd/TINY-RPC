package com.example.tinyrpc.protocol.impl;

import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.registry.Registry;
import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个address对应一个Client，一个Client对应一个Invoker，Invoker可以理解为对Client的封装
 * @auther zhongshunchao
 * @date 20/06/2020 15:42
 */
public class RegistryProtocol implements Protocol {

    private static Logger logger = LoggerFactory.getLogger(RegistryProtocol.class);

    /**
     * InvokerClientWrapper缓存
     */
    private static final Map<Invocation, Invoker> WRAPPED_INVOKER_CACHE = new ConcurrentHashMap<>();

    private Registry zkServiceRegistry;


    /**
     * 服务端侧只有一个server实例，不同服务可以共享同一个端口
     */
    private static final Map<String, Server> SERVER_MAP = new ConcurrentHashMap<>();

    @Override
    public Invoker refer(Invocation invocation) {
        zkServiceRegistry = ExtensionLoader.getExtensionLoader().getExtension(Registry.class, invocation.getUrl().getRegistry());
        Invoker invoker = WRAPPED_INVOKER_CACHE.get(invocation);
        if (invoker != null) {
            return invoker;
        } else {
            invoker = new InvokerClientWrapper(invocation);
            WRAPPED_INVOKER_CACHE.put(invocation, invoker);
            return invoker;
        }
    }

    @Override
    public void export(URL url) {
        zkServiceRegistry = ExtensionLoader.getExtensionLoader().getExtension(Registry.class, url.getRegistry());
        String address = url.getAddress();
        Server server = SERVER_MAP.get(address);
        if (server == null) {
            synchronized (this) {
                server = SERVER_MAP.get(address);
                if (server == null) {
                    server = new NettyServer(address);
                    server.start();
                    SERVER_MAP.put(address, server);
                }
            }
        }
        zkServiceRegistry.register(url);
    }

}
