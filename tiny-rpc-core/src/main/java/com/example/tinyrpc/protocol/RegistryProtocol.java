package com.example.tinyrpc.protocol;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.cluster.RandomLoadBalancer;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.registry.ZkServiceRegistry;
import com.example.tinyrpc.transport.Client;
import com.example.tinyrpc.transport.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther zhongshunchao
 * @date 20/06/2020 15:42
 */
public class RegistryProtocol implements Protocol{

    private static Logger log = LoggerFactory.getLogger(RegistryProtocol.class);

    private static final ZkServiceRegistry ZK_SERVICE_REGISTRY = new ZkServiceRegistry();

    //interfaceName -> Invoker List
    private static final ConcurrentHashMap<String, List<Invoker>> INVOKER_MAP = new ConcurrentHashMap<>();

    //address -> Client
    private static final Map<String, Invoker> CLIENT_MAP = new ConcurrentHashMap<>();

    private static final LoadBalance LOAD_BALANCE = new RandomLoadBalancer();

    //因为是IO密集型任务，所以这里我设置为电脑的核心数*2
    private static final ExecutorService POOL = Executors.newFixedThreadPool(8);

    @Override
    public Invoker refer(Class<?> interfaceClass) {
        String serviceName = interfaceClass.getName();
        //首先去ZK获取IP和端口号列表
        Set<String> serviceAddressList = ZK_SERVICE_REGISTRY.findService(serviceName, (addAddress, closeAddress) -> {
            // open Client
            for (String address : addAddress) {
                POOL.submit(() -> {
                    Client client = new NettyClient(address);
                    Invoker invoker = new ClientInvoker(client, interfaceClass);
                    CLIENT_MAP.put(address, invoker);
                });
            }

            // close Client
            for (String address : closeAddress) {
                POOL.submit(() -> {
                    if (CLIENT_MAP.containsKey(address)) {
                        Invoker invoker = CLIENT_MAP.get(address);
                        invoker.destroy();
                        CLIENT_MAP.remove(address);
                    }
                });
            }
        });
        // 根据从Zookeeper中获取到的服务地址列表serviceAddressList来创建Invoker
        List<Invoker> invokerList = new LinkedList<>();
        for (String serviceAddress : serviceAddressList) {
            Invoker invoker;
            // 先从缓存中取出Invoker
            if (CLIENT_MAP.containsKey(serviceAddress)) {
                invoker = CLIENT_MAP.get(serviceAddress);
            } else {
                // 没有则新建一个Invoker，一个Client可以对应多个Invoker
                Client client = new NettyClient(serviceAddress);
                invoker = new ClientInvoker(client, interfaceClass);
                CLIENT_MAP.put(serviceAddress, invoker);
            }
            invokerList.add(invoker);
        }
        // 放入缓存
        INVOKER_MAP.put(serviceName, invokerList);
        //负载均衡
        return LOAD_BALANCE.select(invokerList);
    }

    @Override
    public void export(URL url) {
        ZK_SERVICE_REGISTRY.register(url);


    }


}
