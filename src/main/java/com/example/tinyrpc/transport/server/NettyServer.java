package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;

import com.example.tinyrpc.transport.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import static com.example.tinyrpc.codec.Codec.*;

/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */

/**
 * RPC的执行流程
 * 1）在项目启动的时候，进行服务的注册和发现。设计到的层次为Protocol、Registry
 *
 * 2）进入RPC调用的过程
 *  收到Client发来的请求or响应消息，根据序列化框架反序列化出消息体中的内容，用Invoker表示该内容。
 *  利用Proxy实现透明化的服务调用，将调用的结果回传给Client端
 */
public class NettyServer implements Server {

    //Start Server
    @Override
    public void run(String hostName, int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(nioEventLoopGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //插入到ChannelHandlerContext这个双链表当中
                            ch.pipeline().addLast(new Encoder())
                                    .addLast(new DelimiterBasedFrameDecoder(MAX_LENGTH, MAGIC))
                                    .addLast(new Decoder())
                                    .addLast(new ServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024);
            ChannelFuture future = bootstrap.bind(hostName, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void received(Object message) {

    }
}
