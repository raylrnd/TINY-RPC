package com.example.tinyrpc.common.utils;

import com.example.tinyrpc.common.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 25/05/2020 22:58
 */
public class FutureContext {
    //CompletableFuture 为jdk1.8新增的异步计算框架
    public static final ConcurrentHashMap<Long, CompletableFuture<Response>> FUTURE_CACHE = new ConcurrentHashMap<>();


}
