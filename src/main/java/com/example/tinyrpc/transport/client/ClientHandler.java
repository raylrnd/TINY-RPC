package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.transport.FutureContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:33
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private Channel channel;


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        //注册channel的时候把它塞进来了
        this.channel = ctx.channel();
    }


    //读取responde消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        Object result = response.getResult();
        long requestId = response.getRequestId();
        CompletableFuture future = FutureContext.FUTURE_CACHE.remove(requestId);
        if (future == null) {
            throw new Exception("requestId错误，response没有对应的request相匹配");
        }
        future.complete(result);
    }



}
