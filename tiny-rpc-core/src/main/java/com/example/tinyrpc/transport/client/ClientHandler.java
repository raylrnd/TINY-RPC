package com.example.tinyrpc.transport.client;

import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.transport.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:33
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }
    //读取responde消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        client.received(ctx, response);
    }
}
