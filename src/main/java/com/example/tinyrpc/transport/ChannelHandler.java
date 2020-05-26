package com.example.tinyrpc.transport;

import io.netty.channel.Channel;

/**
 * @auther zhongshunchao
 * @date 24/05/2020 10:11
 */
public interface ChannelHandler {
    void send(Channel channel, Object message);
}
