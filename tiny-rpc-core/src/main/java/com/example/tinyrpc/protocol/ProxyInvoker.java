package com.example.tinyrpc.protocol;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.cluster.RandomLoadBalancer;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.registry.ZkServiceRegistry;
import com.example.tinyrpc.transport.Client;
import com.example.tinyrpc.transport.client.NettyClient;
import java.util.*;
import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:16 上午
 */
public class ProxyInvoker implements Invoker {

    private Invoker realInvoker;

    private Class<?> interfaceClass;

//    private int weight;

    /**
     * address -> Client(Invoker) ：全局InvokerMap
     */
    private final Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();

    private final ZkServiceRegistry zkServiceRegistry = ZkServiceRegistry.getInstance();

    private static final LoadBalance LOAD_BALANCE = new RandomLoadBalancer();

    /**
     * 因为是IO密集型任务，所以这里我设置为电脑的核心数*2
     */
    private static final ExecutorService POOL = Executors.newFixedThreadPool(8);

    public ProxyInvoker(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        init();
    }

    /**
     * 初始化方法，每个对象只执行一次，初始化invokerMap
     */
    private void init() {
        String serviceName = this.interfaceClass.getCanonicalName();
        Set<String> serviceUrlList = zkServiceRegistry.findServiceUrl(serviceName, this::dealZkCallBack);
        for (String url : serviceUrlList) {
            createInvoker(url);
        }
    }

    private void dealZkCallBack(List<String> addUrlList, Set<String> closeUrlSet) {
        // open Client
        for (String url : addUrlList) {
            POOL.submit(() -> createInvoker(url));
        }

        // close Client
        for (String url : closeUrlSet) {
            POOL.submit(() -> {
                String[] splits = getSplitsFromUrlString(url);
                String address = splits[0];
                Invoker invoker = invokerMap.get(address);
                if (invoker != null) {
                    invoker.destroy();
                    invokerMap.remove(address);
                }
            });
        }

        //重新进行负载均衡
        this.realInvoker = LOAD_BALANCE.select((List<Invoker>) invokerMap.values());
    }

    private String[] getSplitsFromUrlString(String url) {
        String[] split = url.split("$");
        if (split.length < 2) {
            throw new BusinessException("UrlUtils解析url失败，无效的url ：" + url);
        }
        return split;
    }

    private void createInvoker(String url) {
        String[] splits = getSplitsFromUrlString(url);
        String address = splits[0];
        int weight = Integer.valueOf(splits[1]);
        Client client = new NettyClient(address);
        RealInvoker invoker = new RealInvoker(interfaceClass, weight);
        invoker.setClient(client);
        invokerMap.put(address, invoker);
    }

    @Override
    public Class<?> getInterface() {
        return interfaceClass;
    }

    @Override
    public Object invoke(Invocation invocation) {
        return this.realInvoker.invoke(invocation);
    }

    @Override
    public void destroy() {
        this.realInvoker.destroy();
    }

}
