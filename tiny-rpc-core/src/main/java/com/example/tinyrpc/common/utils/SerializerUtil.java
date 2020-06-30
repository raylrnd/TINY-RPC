package com.example.tinyrpc.common.utils;

import com.example.tinyrpc.serialization.Serializer;
import com.example.tinyrpc.serialization.impl.HessianSerializer;
import com.example.tinyrpc.serialization.impl.ProtostuffSerializer;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 26/05/2020 09:21
 */
public class SerializerUtil {
    public static final ConcurrentHashMap<Integer, Serializer> SERIALIZER_CACHE = new ConcurrentHashMap<>();
    public static final HashMap<String, Integer> SERIALIZER_MAP = new HashMap<>();

    static {
        SERIALIZER_MAP.put("jdk", 1);
        SERIALIZER_MAP.put("protobuff", 2);
        SERIALIZER_MAP.put("hessian", 3);
    }
    public static Serializer getSerializer(Integer serializerId) throws Exception {
        if (SERIALIZER_CACHE.contains(serializerId)) {
            return SERIALIZER_CACHE.get(serializerId);
        }
        Serializer serializer = null;
        if (1 == serializerId) {
            serializer = new HessianSerializer();
        } else if (2 == serializerId) {
            serializer = new ProtostuffSerializer();
        }
        if (serializer != null) {
            SERIALIZER_CACHE.put(serializerId, serializer);
        } else {
            throw new Exception("server error: serializerId解析失败");
        }
        return null;
    }

}
