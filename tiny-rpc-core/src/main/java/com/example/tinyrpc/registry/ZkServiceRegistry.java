package com.example.tinyrpc.registry;

import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo中zookeeper的数据存储格式为/dubbo/serviceName/providers, configurators, consumer, routers
 * 这里参考Dubbo的做法，初始化的时候拉取全量的数据。之后采用轮询的方式拉取注册中心的变更的Node
 * Zookeeper数据格式：/Root/Service/Url
 * /TINY-RPC/com.example.tinyrpc.AService/192.168.1.1:1221
 * /TINY-RPC/com.example.tinyrpc.AService/192.168.1.2:1221
 * /TINY-RPC/com.example.tinyrpc.AService/192.168.1.4:1221
 * /TINY-RPC/com.example.tinyrpc.BService/192.168.1.3:1221
 * @auther zhongshunchao
 * @date 20/06/2020 17:16
 */
public class ZkServiceRegistry {

    private static Logger log = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private static final String ZK_REGISTRY_PATH = "/TINY-RPC/";

    private static final ZkSupport ZK_SUPPORT = new ZkSupport();

    private static final Map<String, Set<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    // 初始化时拉取全量的数据，dubbo把Zookeeper中所有服务的地址都拉取过来并持久化到硬盘,这里为了实现方便，不持久化到硬盘
    static {
        try {
            List<String> allServiceNameList = ZK_SUPPORT.getChildren(ZK_REGISTRY_PATH, false);
            if (allServiceNameList.isEmpty()) {
                throw new BusinessException("拉取注册中心全量数据失败");
            }
            for (String serviceName : allServiceNameList) {
                List<String> addressList = ZK_SUPPORT.getChildren(getPath(serviceName), false);
                SERVICE_ADDRESS_MAP.put(serviceName, new HashSet<>(addressList));
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化之后，采用订阅-通知模式进行部分数据同步
     * @param interfaceName 被代理的接口名
     * @param updateAddressCallBack 回调函数，用于更新缓存中的zk地址
     */
    public Set<String> findService(String interfaceName, UpdateAddressCallBack updateAddressCallBack) {
        try {
            List<String> newAddressesList = ZK_SUPPORT.getChildren(getPath(interfaceName), new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        findService(interfaceName, updateAddressCallBack);
                    }
                }
            });
            return updateAddress(interfaceName, newAddressesList, updateAddressCallBack);
        } catch (KeeperException | InterruptedException e) {
            log.info("更新服务地址缓存失败");
            //如果和Zookeeper断线失联，则从缓存中获取
            return SERVICE_ADDRESS_MAP.get(interfaceName);
        }
    }

    private Set<String> updateAddress(String interfaceName, List<String> newAddressesList, UpdateAddressCallBack updateAddressCallBack) {
        Set<String> addressSet = SERVICE_ADDRESS_MAP.get(interfaceName);
        Set<String> newAddressSet = new HashSet<>();
        //与最新的地址进行比较，如果有多出来的地址，则关闭对应的连接，如果少了，则新增对应的连接
        for (String address : newAddressesList) {
            //两个集合都有，则保留
            if (addressSet.contains(address)) {
                newAddressesList.remove(address);
                newAddressSet.add(address);
            }
        }
        //开启之后将它们加入到cache中
        newAddressSet.addAll(newAddressesList);
        SERVICE_ADDRESS_MAP.put(interfaceName, newAddressSet);
        //此时addressSet为需要被关闭的连接，newAddressesList为新增的连接，分别对这两种情况关闭和建立连接，用线程池处理
        //这里用lambada表达式做回调
        updateAddressCallBack.updateAddress(newAddressesList, addressSet);
        return newAddressSet;
    }

    public void register(URL url) {
        String serviceName = url.getInterfaceName();
        String exposeURL = url.exposeURL();
        ZK_SUPPORT.createNodeIfAbsent(exposeURL, getPath(serviceName));
    }

    private static String getPath(String path) {
        return ZK_REGISTRY_PATH + path;
    }
}
