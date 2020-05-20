package com.example.tinyrpc.Client;

import com.example.tinyrpc.Codec.Decoder;
import com.example.tinyrpc.Codec.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import static com.example.tinyrpc.Constant.FrameConstant.*;


/**
 * @auther zhongshunchao
 * @date 13/04/2020 12:54
 */
public class Client {
    public static void startClient(String hostName, int port) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //Encoder: netty自带编码器，可以自动将长度加到头部
                            ch.pipeline().addLast(new LengthFieldPrepender(LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT))
                                    //Encoder: message to byte
                                    .addLast(new Encoder())
                                    .addLast(new Decoder())
                                    //自定义处理逻辑，解析请求
                                    .addLast(new ClientHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024);
            ChannelFuture future = bootstrap.bind(hostName, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
