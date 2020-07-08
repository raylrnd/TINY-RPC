package com.example.tinyrpc.common.utils;

import com.example.tinyrpc.common.domain.Constants;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther zhongshunchao
 * @date 26/05/2020 09:21
 */
public class CodecSupport {

    private static final Map<Byte, String> SERIALIZER_CACHE = new HashMap<>();

    private static Map<String, Byte> SERIALIZATIONNAME_ID_MAP = new HashMap<>();

    public static final HashMap<String, Integer> SERIALIZER_MAP = new HashMap<>();

    private CodecSupport() {
    }

    static {
        SERIALIZER_CACHE.put((byte) 0x00, "hessian");
        SERIALIZER_CACHE.put((byte) 0x01, "protostuff");
        SERIALIZER_CACHE.put((byte) 0x02, "fastjson");

        SERIALIZATIONNAME_ID_MAP.put("hessian", (byte) 0x00);
        SERIALIZATIONNAME_ID_MAP.put("protostuff", (byte) 0x01);
        SERIALIZATIONNAME_ID_MAP.put("fastjson", (byte) 0x02);

    }

    public static byte getIDByName(String name) {
        if (StringUtils.isEmpty(name)) {
            name = Constants.DEFAULT_SERIALIATION;
        }
        return SERIALIZATIONNAME_ID_MAP.get(name);
    }

    public static String getNameById(byte id) {
        return SERIALIZER_CACHE.get(id);
    }

}
