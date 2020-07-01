package com.example.tinyrpc.protocol.impl;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.common.ExtensionLoader;
import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.registry.Registry;
import com.example.tinyrpc.transport.Client;
import com.example.tinyrpc.transport.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:16 上午
 */
public class InvokerClientWrapper implements Invoker {

    private static final Logger logger = LoggerFactory.getLogger(InvokerClientWrapper.class);

    private Invoker realInvoker;

    private Invocation invocation;

    /**
     * address -> Client(Invoker) ：全局InvokerMap
     */
    private final Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();

    private final Registry zkServiceRegistry;

    private final LoadBalance loadBalance;

    InvokerClientWrapper(Invocation invocation) {
        this.invocation = invocation;
        zkServiceRegistry = ExtensionLoader.getExtensionLoader().getExtension(Registry.class, invocation.getUrl().getRegistry());
        loadBalance = ExtensionLoader.getExtensionLoader().getExtension(LoadBalance.class, invocation.getUrl().getLoadbalance());
        init();
    }

    /**
     * 初始化方法，每个对象只执行一次，初始化invokerMap
     */
    private void init() {
        String serviceName = this.invocation.getInterfaceClass().getName();
        Set<String> serviceUrlList = zkServiceRegistry.subscribe(serviceName, this::handleZkCallBack);
        for (String url : serviceUrlList) {
            createInvoker(url);
        }
        this.realInvoker = loadBalance.select(new ArrayList<>(invokerMap.values()));
    }

    //这里之前犯过一个错误，不能异步删除和添加Client，防止子线程还没来的及删除该client，然后该client被select了，这样就会导致使用了无效的client
    private void handleZkCallBack(List<String> addUrlList, Set<String> closeUrlSet) {

        logger.info("接收到来自Zookeeper的CallBack， 需要添加的地址为 addUrlList : {}", addUrlList, " ； 需要关闭的url为 closeUrlSet ：{}", closeUrlSet);

        // open Client
        for (String url : addUrlList) {
            createInvoker(url);
        }

        // close Client
        for (String url : closeUrlSet) {
            String[] splits = getSplitsFromUrlString(url);
            String address = splits[0];
            Invoker invoker = invokerMap.get(address);
            if (invoker != null) {
                invoker.destroy();
                invokerMap.remove(address);
            }
        }

        logger.info("系统开始进行负载均衡...");
        logger.info("全局zk地址缓存invokerMap为{}", invokerMap);
        this.realInvoker = loadBalance.select(new ArrayList<>(invokerMap.values()));
        logger.info("此次被LoadBalancer选中的invoker 为 ：{}", realInvoker);

    }

    private String[] getSplitsFromUrlString(String url) {
        String[] split = url.split("&");
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
        URL realUrl = new URL();
        realUrl.setAddress(address);
        RealInvoker invoker = new RealInvoker(invocation.getInterfaceClass(), weight, realUrl);
        invoker.setClient(client);
        invokerMap.put(address, invoker);
    }


    @Override
    public URL getUrl() {
        return realInvoker.getUrl();
    }

    @Override
    public Class<?> getInterface() {
        return invocation.getInterfaceClass();
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
