package com.example.tinyrpc.codec;

import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.serialization.Serializer;
import com.example.tinyrpc.serialization.serializer.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:23
 */
public class Encoder extends MessageToByteEncoder implements Codec{

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
        if (msg instanceof Request) {
            encodeRequest(ctx, (Request) msg, buffer);
        } else {
            encodeResponse(ctx, (Response) msg, buffer);
        }
    }

    public void encodeRequest(ChannelHandlerContext ctx, Request request, ByteBuf buffer) throws Exception {
        Serializer serializer = new ProtostuffSerializer();
        // header.
        byte [] flag = new byte[2];
        buffer.readBytes(MAGIC_ARRAY);
        if (request.isIs2way()) {
            flag[0] |= FLAG_TWOWAY;
        }
        // is PING
        if (request.isEvent()) {
            flag[0] |= FLAG_EVENT;
        }
        // set serializer id.
        flag[0] |= 0x01;
        buffer.readBytes(flag);
        long requestId = request.getRequestId();
        buffer.writeLong(requestId);
        byte[] body = serializer.serialize(request.getData());
        buildBody(buffer, body);
    }

    //比request多了个status
    private void encodeResponse(ChannelHandlerContext ctx, Response response, ByteBuf buffer) throws Exception {
        Serializer serializer = new ProtostuffSerializer();
        // header.
        byte [] flag = new byte[2];
        buffer.readBytes(MAGIC_ARRAY);
        // is PONG
        if (response.isEvent()) {
            flag[0] |= FLAG_EVENT;
        }
        // set serializer id.
        flag[0] |= 0x01;
        flag[1] |= response.getStatus();
        buffer.readBytes(flag);
        long requestId = response.getRequestId();
        buffer.writeLong(requestId);
        byte[] body = serializer.serialize(response.getResult());
        buildBody(buffer, body);
    }

    private void buildBody(ByteBuf buffer, byte[] body) {
        int savedWriterIndex = buffer.writerIndex();
        // skip 2 bytes
        buffer.writerIndex(savedWriterIndex + 2);
        // write body
        buffer.readBytes(body);
        int bodyLength = buffer.writerIndex() - (2 + savedWriterIndex);
        buffer.writerIndex(savedWriterIndex);
        // write data length
        buffer.writeInt(bodyLength);
    }
}
