package com.example.tinyrpc.common;

import java.util.HashSet;
import java.util.Set;

/**
 * @auther zhongshunchao
 * @date 28/06/2020 23:18
 */
public interface Constants {

    String LOCAL_HOST = "127.0.0.1";

    int CLIENT_SIDE = 1;

    int SERVER_SIDE = 0;

    String SPAN_KEY = "span";

    Set<String> FILTER_SET = new HashSet<>();
}
