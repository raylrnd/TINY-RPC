package com.example.tinyrpc.transport;

import com.example.tinyrpc.common.domain.Request;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Future;

/**
 * @auther zhongshunchao
 * @date 07/07/2020 23:03
 */
public interface Endpoint {

    void start();

    Future<Object> send(Request request);

    void sendCallBack(Request message);

    void received(ChannelHandlerContext ctx, Object msg) ;

    void close();
}
