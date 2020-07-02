package com.example.tinyrpc.transport;

import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:25 上午
 */
@Component
public interface Client {

    Future<Object> send(Request message);

    void received(ChannelHandlerContext ctx, Response response) throws Exception;

    void close();
}
