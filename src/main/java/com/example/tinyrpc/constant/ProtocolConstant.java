package com.example.tinyrpc.constant;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @auther zhongshunchao
 * @date 17/05/2020 13:03
 */
public final class ProtocolConstant {
    //自定义魔数"0xdeff"
    private static byte [] magic = {(byte)0xde, (byte)0xff};
    public static final ByteBuf MAGIC = Unpooled.copiedBuffer(magic);    // 头部长度固定8个字节
    public static final int HEAD_LENGTH = 8;
    // 最大报文长度 8M
    public static final int MAX_LENGTH = 1024 * 1024;

}
