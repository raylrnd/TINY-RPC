package com.example.tinyrpc.Server;

import com.example.tinyrpc.Codec.MyDecoder;
import com.example.tinyrpc.Constant.ProtocolConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class Server {

    //Start Server
    public static void startServer(String hostName, int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(nioEventLoopGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //插入到ChannelHandlerContext这个双链表当中
                            ch.pipeline().addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new ServerHandler())
                                    .addLast(new DelimiterBasedFrameDecoder(ProtocolConstant.MAX_LENGTH, ProtocolConstant.MAGIC))
                                    .addLast(new MyDecoder());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024);
            ChannelFuture future = bootstrap.bind(hostName, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
