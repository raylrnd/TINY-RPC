package com.example.tinyrpc.codec;


import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.ResponseBody;
import com.example.tinyrpc.serialization.Serializer;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.serialization.serializer.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;



/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:23
 */

/**
 * 仿照Dubbo协议
 *  ------------ (32 bits)
 *  totoal length except this             // 往下的所有字段长度的总和
 *  ------------ (32 bits)
 * | magic (16 bits)                      // 魔数，为"0xdeff"
 * | Req/Res (1 bit)                      // Req/Res, 请求: 1; 响应: 0
 * | 2way (1 bit)                         // 仅在 Req/Res 为1（请求）时才有用，标记是否期望从服务器返回值。如果需要来自服务器的返回值，则设置为1。
 * | event (1 bit)                        // 表示为PING/PONG事件，如果是req，则为PING请求；如果是res，则为PONG请求
 * | default (1 bit)                      // 缺省字段，占位用
 * | serialization_id (4 bits)            // 序列化器的ID
 * | status (8 bits)                      // 消息的状态：200/404/500等
 *  ----------- (64 bits)
 * | RPC Request ID (64 bits)
 *  -----------
 * | Data                                 // 支持变长格式
 */
// 如果是Client，接收到的入站消息应该是response，直接将response移交给上层
// 如果是Server，接收到的入站消息应该是request，利用反射得到RPC调用结果，然后发送给Client端
// 如果req == 1, 应该移交给Client端进行处理。考虑到一台机器可能同时有client和server
public class Decoder extends ByteToMessageDecoder implements Codec{

    public Serializer serializer;

    // 先读头部，解析出消息的长度
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        int len = buffer.readableBytes();
        if (buffer.readableBytes() < 16) {
            // 头部异常
            throw new Exception("头部异常");
        }
        // 解析头部序列化编号、状态码
        byte [] magic = new byte[2];
        buffer.readBytes(magic);
        byte [] header = new byte[2];
        buffer.readBytes(header);
        // 解析Request ID
        long requestId = buffer.readLong();
        // 解析data length
//        int dataLength = buffer.readInt();
//        if (buffer.readableBytes() != dataLength) {
//            throw new Exception("实际长度与dataLength不符");
//        }
        // 是否是请求消息
        boolean isRequest = (header[0] & FLAG_REQUEST) != 0;
        // 是否期望从服务器返回值
        boolean is2way = (header[0] & FLAG_TWOWAY) != 0;
        // 是否是事件
        boolean isEvent = (header[0] & FLAG_TWOWAY) != 0;
        // 取后5位，解析出serializationId
        int serializationId =  (header[0] & SERIALIZATION_MASK);
        // 解析status
        byte status = header[1];
        byte [] body = new byte [len - 12];
        // 解析消息体
        buffer.readBytes(body);
        if (isRequest) {
            Request request = new Request(requestId);
            request.setEvent(isEvent);
            Invocation data = new ProtostuffSerializer().deserialize(body, Invocation.class);
            request.setData(data);
            out.add(request);
        } else {
            Response response = new Response(requestId);
            response.setEvent(isEvent);
            ResponseBody responseBody = new ProtostuffSerializer().deserialize(body, ResponseBody.class);
            response.setResponseBody(responseBody);
            out.add(response);
        }
    }
}
