package com.example.tinyrpc.codec;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.serialization.Serializer;
import com.example.tinyrpc.serialization.serializer.JsonSerializer;
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
        int savedStart = buffer.writerIndex();
        buffer.writerIndex(savedStart + 4);
        buffer.writeBytes(MAGIC_ARRAY);
        // header.
        byte [] flag = new byte[2];
        if (request.isIs2way()) {
            flag[0] |= FLAG_TWOWAY;
        }
        // is PING
        if (request.isEvent()) {
            flag[0] |= FLAG_EVENT;
        }
        // set serializer id.
        flag[0] |= 0x01;
        buffer.writeBytes(flag);
        long requestId = request.getRequestId();
        buffer.writeLong(requestId);
        byte[] body = null;
        body = new ProtostuffSerializer().serialize(request.getData());
        buffer.writeBytes(body);
        int end = buffer.writerIndex();
        buffer.writerIndex(savedStart);
        buffer.writeInt(end - savedStart -4);
        buffer.writerIndex(end);
    }

    //比request多了个status
    private void encodeResponse(ChannelHandlerContext ctx, Response response, ByteBuf buffer) throws Exception {
        Serializer serializer = new ProtostuffSerializer();
        // header.
        byte [] flag = new byte[2];
        buffer.writeBytes(MAGIC_ARRAY);
        // is PONG
        if (response.isEvent()) {
            flag[0] |= FLAG_EVENT;
        }
        // set serializer id.
        flag[0] |= 0x01;
        flag[1] |= response.getStatus();
        buffer.writeBytes(flag);
        long requestId = response.getRequestId();
        buffer.writeLong(requestId);
        byte[] body = serializer.serialize(response.getResponseBody());
        buffer.writeBytes(body);
    }

//    private int buildBody(ByteBuf buffer, byte[] body) {
//        int savedWriterIndex = buffer.writerIndex();
////        // skip 2 bytes
////        buffer.writerIndex(savedWriterIndex + 2);
//        // write body
//        buffer.writeBytes(body);
////        //每回写一次就要save一次
////        int savedEnd = buffer.writerIndex();
////        int bodyLength = savedEnd - (2 + savedWriterIndex);
//        buffer.writerIndex(savedWriterIndex);
//        // write data length
////        buffer.writeInt(bodyLength);
//        buffer.writerIndex(savedEnd);
////        return bodyLength;
//    }
}
