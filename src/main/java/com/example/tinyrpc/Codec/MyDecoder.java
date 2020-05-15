package com.example.tinyrpc.Codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:23
 */
public class MyDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public MyDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    //先读头部，解析出消息的长度
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = SerializationUtil.deserializer(data, genericClass);
        out.add(obj);
    }
}
