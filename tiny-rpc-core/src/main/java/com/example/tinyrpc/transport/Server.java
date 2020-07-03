package com.example.tinyrpc.transport;

import com.example.tinyrpc.common.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:29 上午
 */
public interface Server {

    void start();

    void received(ChannelHandlerContext ctx, Request request);

    void stop();

}
