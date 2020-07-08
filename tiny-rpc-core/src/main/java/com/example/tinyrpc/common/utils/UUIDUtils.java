package com.example.tinyrpc.common.utils;

import java.util.UUID;

/**
 * @auther zhongshunchao
 * @date 04/07/2020 17:47
 */
public class UUIDUtils {

    public static long getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits();
    }
}
