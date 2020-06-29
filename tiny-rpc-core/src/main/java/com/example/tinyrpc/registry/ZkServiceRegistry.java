package com.example.tinyrpc.registry;

import com.example.tinyrpc.common.URL;
import com.example.tinyrpc.common.exception.BusinessException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo中zookeeper的数据存储格式为/dubbo/serviceName/providers, configurators, consumer, routers
 * 这里参考Dubbo的做法，初始化的时候拉取全量的数据。之后每当注册中心的值变化时，更新之前缓存过的全量数据中的被改变的部分。
 * 本项目的Zookeeper目录格式为 ：
 * |path                                   |  value               |
 * |/TINY-RPC/com.example.tinyrpc.AService | 192.168.1.1:1221&100 |
 * |/TINY-RPC/com.example.tinyrpc.BService | 192.168.1.4:1221&200 |
 * |/TINY-RPC/com.example.tinyrpc.AService | 192.168.1.2:1221&300 |
 * @auther zhongshunchao
 * @date 20/06/2020 17:16
 */
public class ZkServiceRegistry {

    private static Logger log = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private static final String ZK_REGISTRY_PATH = "/TINY-RPC";

    private static final ZkSupport ZK_SUPPORT = new ZkSupport();

    //本地缓存的Zookeeper的全量信息。无法连接Zookeeper时，可以从该缓存中获取地址。每次调用updateAddress()时被更新
    private static final Map<String, Set<String>> SERVICE_URL_MAP = new ConcurrentHashMap<>();

    private static volatile ZkServiceRegistry instance = null;

    private ZkServiceRegistry() {}

    // 初始化时拉取全量的数据，dubbo把Zookeeper中所有服务的地址都拉取过来并持久化到硬盘,这里为了实现方便，不持久化到硬盘
    static {
        try {
            //检查是否有根目录
            if (ZK_SUPPORT.hasNoRoot(ZK_REGISTRY_PATH)) {
                ZK_SUPPORT.createPathIfAbsent(ZK_REGISTRY_PATH, CreateMode.PERSISTENT);
            }
            List<String> allServiceNameList = ZK_SUPPORT.getChildren(ZK_REGISTRY_PATH, false);
            if (allServiceNameList.isEmpty()) {
                log.warn("There is no Node under root path:{}", ZK_REGISTRY_PATH);
            } else {
                for (String serviceName : allServiceNameList) {
                    List<String> addressList = ZK_SUPPORT.getChildren(getPath(serviceName), false);
                    SERVICE_URL_MAP.put(serviceName, new HashSet<>(addressList));
                }
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("Error when initializing SERVICE_URL_MAP, exception:" + e.getMessage());
        }
    }

    public static ZkServiceRegistry getInstance() {
        if (instance == null) {
            synchronized (ZkServiceRegistry.class) {
                if (instance == null) {
                    instance = new ZkServiceRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化之后，采用订阅-通知模式进行部分数据同步
     * @param interfaceName 被代理的接口名
     * @param updateAddressCallBack 回调函数，用于更新缓存中的zk地址
     */
    public Set<String> findServiceUrl(String interfaceName, UpdateAddressCallBack updateAddressCallBack) {
        try {
            //在与Zookeeper非失联的情况下，从Zookeeper拉取最新的数据。如果在客户端已经启动的情况下，处于RPC通信状态，此时Zookeeper的数据变化，那么会通过Watcher回调来更新缓存中的地址
            List<String> newUrlList = ZK_SUPPORT.getChildren(getPath(interfaceName), new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        findServiceUrl(interfaceName, updateAddressCallBack);
                    }
                }
            });
            if (newUrlList == null) {
                throw new BusinessException("在Zookeeper没有找到interfaceName为：" + interfaceName + "的地址");
            }
            return updateAddress(interfaceName, newUrlList, updateAddressCallBack);
        } catch (KeeperException | InterruptedException e) {
            log.info("更新服务地址缓存失败");
            //如果和Zookeeper断线失联，则从缓存中获取
            return SERVICE_URL_MAP.get(interfaceName);
        }
    }

    private Set<String> updateAddress(String interfaceName, List<String> newUrlList, UpdateAddressCallBack updateAddressCallBack) {
        Set<String> oldUrlSet = SERVICE_URL_MAP.get(interfaceName);
        if (oldUrlSet == null) {
            Set<String> newUrlSet = new HashSet<>(newUrlList);
            synchronized (this) {
                oldUrlSet = SERVICE_URL_MAP.get(interfaceName);
                if (oldUrlSet == null) {
                    SERVICE_URL_MAP.put(interfaceName, newUrlSet);
                    return newUrlSet;
                }
            }
        }
        Set<String> newAddressSet = new HashSet<>();
        //与最新的地址进行比较，如果有多出来的地址，则关闭对应的连接，如果少了，则新增对应的连接
        Iterator<String> iterator = newUrlList.iterator();
        while (iterator.hasNext()) {
            String address = iterator.next();
            //两个集合都有，则保留
            if (oldUrlSet.contains(address)) {
                oldUrlSet.remove(address);
                iterator.remove();
                newAddressSet.add(address);
            }
        }
        //开启之后将它们加入到cache中
        newAddressSet.addAll(newUrlList);
        SERVICE_URL_MAP.put(interfaceName, newAddressSet);
        //此时oldUrlSet为需要被关闭的连接，newUrlList为新增的连接，分别对这两种情况关闭和建立连接，用线程池处理
        //这里用lambada表达式做回调
        updateAddressCallBack.updateAddress(newUrlList, oldUrlSet);
        return newAddressSet;
    }

    public void register(URL url) {
        String path = getPath(url.getInterfaceName());
        String data = url.exposeURL();
        //先创建目录然后创建目录下的值
        try {
            ZK_SUPPORT.createPathIfAbsent(path, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            throw new BusinessException("无法创建Zookeeper目录：" + path);
        }
        ZK_SUPPORT.createNodeIfAbsent(data, path);
    }

    private static String getPath(String path) {
        return ZK_REGISTRY_PATH + "/" + path;
    }
}
