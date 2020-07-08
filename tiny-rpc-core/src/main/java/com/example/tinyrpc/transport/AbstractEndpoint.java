package com.example.tinyrpc.transport;

import com.example.tinyrpc.common.extension.ExtensionLoader;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;

/**
 * @auther zhongshunchao
 * @date 07/07/2020 23:24
 */
public abstract class AbstractEndpoint implements Endpoint{

    private static final Logger logger = LoggerFactory.getLogger(AbstractEndpoint.class);

    protected Channel channel;

    protected String address;

    protected ExecutorService executor = ExtensionLoader.getExtensionLoader().getDefaultExecutor();

    public AbstractEndpoint(String address) {
        this.address = address;
    }

    public void close() {
        executor.submit(() -> {
            logger.info("正在关闭 Client:{}", address);
            if (this.channel != null && channel.isOpen()) {
                try {
                    this.channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    logger.error("Fail to close client, address:" + address);
                }
            }
        });
    }

}
