package com.example.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @auther zhongshunchao
 * @date 29/05/2020 22:06
 */
public interface Codec {
    // message flag.
    byte FLAG_REQUEST = (byte) 0x80;
    byte FLAG_TWOWAY = (byte) 0x40;
    byte FLAG_EVENT = (byte) 0x20;

    int SERIALIZATION_MASK = 0x0f;
    // status
    int STATUS = 0xf0;

    //自定义魔数"0xdeff"
    public static final byte [] MAGIC_ARRAY = {(byte)0xde, (byte)0xff};
    public static final ByteBuf MAGIC = Unpooled.copiedBuffer(MAGIC_ARRAY);    // 头部长度固定8个字节
//    byte [] MAGIC_ARRAY = "$$".getBytes();
//    public static final ByteBuf MAGIC = Unpooled.copiedBuffer(MAGIC_ARRAY);
    public static final int HEAD_LENGTH = 8;
    public static final int HEADER_LENGTH = 16;
    // 最大报文长度 8M
    public static final int MAX_LENGTH = 1024 * 1024;

    public static final int MAX_FRAME_LENGTH = 1024 * 1024;
    public static final int LENGTH_FIELD_OFFSET = 0;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    // 这样下一个Handler接收到的就不包含length了，直接就是message
    public static final int INITIAL_BYTES_TO_STRIP = 4;
}
