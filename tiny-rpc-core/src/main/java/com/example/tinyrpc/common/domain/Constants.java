package com.example.tinyrpc.common.domain;

/**
 * @auther zhongshunchao
 * @date 28/06/2020 23:18
 */
public interface Constants {

    String LOCAL_HOST = "127.0.0.1";

    int CLIENT_SIDE = 1;

    int SERVER_SIDE = 0;

    String SPAN_KEY = "span";

    String INTERNAL_PATH = "/META-INF/TINY-RPC/internal/";

    String EXTERNAL_PATH = "META-INF/TINY-RPC";

    String DEFAULT_SERIALIATION = "protostuff";

    int HEART_BEAT_TIME_OUT = 20;

    int HEART_BEAT_TIME_OUT_MAX_TIME = 3;
}
