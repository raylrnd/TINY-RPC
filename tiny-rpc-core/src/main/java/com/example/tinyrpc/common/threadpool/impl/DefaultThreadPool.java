package com.example.tinyrpc.common.threadpool.impl;

import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.threadpool.ThreadPool;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 05/07/2020 15:23
 */
public class DefaultThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        return new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)
                , new CustomizableThreadFactory("default-business-thread-"), new ThreadPoolExecutor.AbortPolicy());
    }
}
