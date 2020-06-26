package com.example.tinyrpc.protocol;

import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.registry.ZkServiceRegistry;
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
public class RegistryProtocol implements Protocol{

    private static Logger log = LoggerFactory.getLogger(RegistryProtocol.class);

    /**
     * interfaceName -> ProxyInvoker
     */
    private static final Map<String, Invoker> PROXY_INVOKER_MAP = new ConcurrentHashMap<>();

    private final ZkServiceRegistry zkServiceRegistry = ZkServiceRegistry.getInstance();


    /**
     * 服务端侧只有一个server实例，不同服务可以共享同一个端口
     */
    private static final Map<String, Server> SERVER_MAP = new ConcurrentHashMap<>();

    @Override
    public Invoker refer(Class<?> interfaceClass) {
        Invoker invoker = PROXY_INVOKER_MAP.get(interfaceClass);
        if (invoker != null) {
            return invoker;
        } else {
            return new ProxyInvoker(interfaceClass);
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
                    SERVER_MAP.put(address, new NettyServer(address));
                }
            }
        }
        zkServiceRegistry.register(url);
    }

}
