package com.example.tinyrpc.constant;

/**
 * @auther zhongshunchao
 * @date 08/05/2020 08:03
 */
public class FrameConstant {
    public static final int MAX_FRAME_LENGTH = 1024 * 1024;
    public static final int LENGTH_FIELD_OFFSET = 0;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    // 这样下一个Handler接收到的就不包含length了，直接就是message
    public static final int INITIAL_BYTES_TO_STRIP = 4;
}
