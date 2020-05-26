package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.protocol.Invoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * @auther zhongshunchao
 * @date 05/05/2020 15:02
 */
// 消息被读取后，会自动释放资源
public class ServerHandler extends SimpleChannelInboundHandler<Invoker> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Invoker msg) throws Exception {
        //调用代理，通过反射的方式调用本地jvm中的方法

    }
}
