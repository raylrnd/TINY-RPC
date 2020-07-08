package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.common.domain.Request;
import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.client.ClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther zhongshunchao
 * @date 05/05/2020 15:02
 */
// 消息被读取后，会自动释放资源
public class ServerHandler extends SimpleChannelInboundHandler<Request> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private Server server;

    ServerHandler(NettyServer nettyServer) {
        this.server = nettyServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        logger.info("服务端 ServerHandler 收到Request为：{}", request);
        server.received(ctx, request);
    }
}
