package com.example.tinyrpc.registry;

import com.example.tinyrpc.common.exception.BusinessException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkSupport {

    private static Logger logger = LoggerFactory.getLogger(ZkSupport.class);

    /**
     * zk变量
     */
    private ZooKeeper zookeeper;
    /**
     * 信号量设置，用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行
     */
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private static final int ZK_SESSION_TIMEOUT = 10000;

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    ZkSupport() {
        try {
            this.zookeeper = new ZooKeeper(ZK_ADDRESS, ZK_SESSION_TIMEOUT, (WatchedEvent event) -> {
                //获取事件的状态
                Watcher.Event.KeeperState keeperState = event.getState();
                Watcher.Event.EventType eventType = event.getType();
                //如果是建立连接
                if (Watcher.Event.KeeperState.SyncConnected == keeperState) {
                    if (Watcher.Event.EventType.None == eventType) {
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        logger.info("Zookeeper connected successfully at address:" + ZK_ADDRESS);
                    }
                }
            });
            logger.info("Start connecting Zookeeper");
            connectedSemaphore.await();
        } catch (Exception e) {
            throw new BusinessException("Cannot connect Zookeeper ：" + ZK_ADDRESS);
        }
    }

    void createNodeIfAbsent(String data, String path) {
        try {
            byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
            zookeeper.create(path + "/" + data, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (InterruptedException | KeeperException ex) {
            ex.printStackTrace();
            logger.error("Fail to register path :" + path + ": data ；" + data);
        }
    }

    List<String> getChildren(final String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zookeeper.getChildren(path, watcher);
    }

    List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return zookeeper.getChildren(path, watch);
    }

    public byte[] getData(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zookeeper.getData(path, watcher, null);
    }

    /**
     * @param path
     * @param createMode
     */
    void createPathIfAbsent(String path, CreateMode createMode) throws KeeperException, InterruptedException {
        Stat s = zookeeper.exists(path, false);
        if (s == null) {
            zookeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
        }
    }

    boolean hasNoRoot(String path) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(path, false);
        return stat == null;
    }

    /**
     * 关闭ZK连接
     */
    public void close() {
        try {
            this.zookeeper.close();
        } catch (InterruptedException e) {
            logger.error("Fail to close connection from Zookeeper, exception:" + e.getMessage());
        }
    }
}
