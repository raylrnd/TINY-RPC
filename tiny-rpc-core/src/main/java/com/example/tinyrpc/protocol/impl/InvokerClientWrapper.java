package com.example.tinyrpc.protocol.impl;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.common.extension.ExtensionLoader;
import com.example.tinyrpc.common.domain.Invocation;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.registry.Registry;
import com.example.tinyrpc.transport.Client;
import com.example.tinyrpc.transport.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类对象被当作代理对象，替换掉被@Reference标识的类对象。该类包装了RealInvoker，实现了invoker之间的负载均衡
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
     * 初始化方法，每个对象只执行一次，从Zookeeper拉取address，封装成invokerMap。并在InvokerClientWrapper初始化的过程中进行负载均衡
     */
    private void init() {
        String serviceName = this.invocation.getInterfaceClass().getName();
        Set<String> serviceUrlList = zkServiceRegistry.subscribe(serviceName, this::handleZkCallBack);
        for (String url : serviceUrlList) {
            createInvoker(url);
        }
        logger.info("###init()系统开始进行负载均衡...");
        logger.info("###init()全局zk地址缓存invokerMap为{}", invokerMap);
        this.realInvoker = loadBalance.select(new ArrayList<>(invokerMap.values()));
        logger.info("###init()此次被LoadBalancer选中的invoker 为 ：{}", realInvoker);
    }

    //这里之前犯过一个错误，不能异步删除和添加Client，防止子线程还没来的及删除该client，然后该client被select了，这样就会导致使用了无效的client
    private void handleZkCallBack(List<String> addUrlList, Set<String> closeUrlSet) {
        if (CollectionUtils.isEmpty(addUrlList) && CollectionUtils.isEmpty(closeUrlSet)) {
            return;
        }
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

        logger.info("###handleZkCallBack()系统开始进行负载均衡...");
        logger.info("###handleZkCallBack()全局zk地址缓存invokerMap为{}", invokerMap);
        this.realInvoker = loadBalance.select(new ArrayList<>(invokerMap.values()));
        logger.info("###handleZkCallBack()此次被LoadBalancer选中的invoker 为 ：{}", realInvoker);

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
        client.start();
        URL realUrl = new URL();
        realUrl.setAddress(address);
        RealInvoker invoker = new RealInvoker(invocation.getInterfaceClass(), weight, realUrl);
        invoker.setEndpoint(client);
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
    public Object invoke(Invocation invocation) throws Exception {
        return this.realInvoker.invoke(invocation);
    }

    @Override
    public void destroy() {
        this.realInvoker.destroy();
    }

}
