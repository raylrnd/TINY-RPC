package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.codec.Decoder;
import com.example.tinyrpc.codec.Encoder;

import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.client.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    private ServerBootstrap bootstrap;

    private String address;

    public NettyServer(String address) {
        this.address = address;
        this.bootstrap = new ServerBootstrap();
        open(address);
    }
    //Start Server
    public void open(String address) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
//                            插入到ChannelHandlerContext这个双链表当中
                            ch.pipeline()
                                    .addLast(new Encoder())
                                    .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP))
                                    .addLast(new Decoder())
                                    .addLast(new ServerHandler());
                        }
                    });
            String[] ipAndPort = address.trim().split(":");
            ChannelFuture future = bootstrap.bind(ipAndPort[0], Integer.valueOf(ipAndPort[1])).sync();
            this.channel = future.channel();
            log.info("conect Client" + address + "SUCESS");
//            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        this.channel.closeFuture();
    }
}
