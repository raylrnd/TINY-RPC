package com.example.tinyrpc.registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkSupport {

    private static Logger log = LoggerFactory.getLogger(ZkSupport.class);

    /**
     * zk变量
     */
    protected ZooKeeper zookeeper = null;
    /**
     * 信号量设置，用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行
     */
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private static final int ZK_SESSION_TIMEOUT = 5000;

    public void connect(String address) {
        try {
            this.zookeeper = new ZooKeeper(address, ZK_SESSION_TIMEOUT, (WatchedEvent event) -> {
                //获取事件的状态
                Watcher.Event.KeeperState keeperState = event.getState();
                Watcher.Event.EventType eventType = event.getType();
                //如果是建立连接
                if (Watcher.Event.KeeperState.SyncConnected == keeperState) {
                    if (Watcher.Event.EventType.None == eventType) {
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        log.info("ZK建立连接");
                    }
                }
            });
            log.info("开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNodeIfAbsent(String data, String path) {
        try {
            byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
            zookeeper.create(path + "/" + data, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (InterruptedException | KeeperException ex) {
            ex.printStackTrace();
            log.error("服务注册失败" + "path" + path + ":data" + data);
        }
    }

    public List<String> getChildren(final String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zookeeper.getChildren(path, watcher);
    }

    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return zookeeper.getChildren(path, watch);
    }

    public byte[] getData(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zookeeper.getData(path, watcher, null);
    }

    /**
     * @param path
     * @param createMode
     */
    public void createPathIfAbsent(String path, CreateMode createMode) throws KeeperException, InterruptedException {
        String[] split = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (StringUtils.hasText(split[i])) {
                sb.append(split[i]);
                Stat s = zookeeper.exists(sb.toString(), false);
                if (s == null) {
                    zookeeper.create(sb.toString(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                }
            }
            if (i < split.length - 1) {
                sb.append("/");
            }
        }
    }

    /**
     * 关闭ZK连接
     */
    public void close() {
        try {
            this.zookeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
