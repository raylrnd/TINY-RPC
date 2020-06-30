package com.example.tinyrpc.protocol.impl;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.protocol.Protocol;
import com.example.tinyrpc.registry.ZkServiceRegistry;
import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 20/06/2020 15:42
 * 一个address对应一个Client，一个Client对应一个Invoker，Invoker可以理解为对Client的封装
 */
public class RegistryProtocol implements Protocol {

    private static Logger logger = LoggerFactory.getLogger(RegistryProtocol.class);

    /**
     * interfaceName -> InvokerClientWrapper
     */
    private static final Map<String, Invoker> PROXY_INVOKER_MAP = new ConcurrentHashMap<>();

    private final ZkServiceRegistry zkServiceRegistry = ZkServiceRegistry.getInstance();


    /**
     * 服务端侧只有一个server实例，不同服务可以共享同一个端口
     */
    private static final Map<String, Server> SERVER_MAP = new ConcurrentHashMap<>();

    @Override
    public Invoker refer(Invocation invocation) {
        Invoker invoker = PROXY_INVOKER_MAP.get(invocation.getServiceName());
        if (invoker != null) {
            return invoker;
        } else {
            return new InvokerClientWrapper(invocation);
        }
    }

    @Override
    public void export(URL url) {
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
