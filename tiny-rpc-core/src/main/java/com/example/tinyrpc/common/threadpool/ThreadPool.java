package com.example.tinyrpc.common.threadpool;

import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.common.extension.SPI;

import java.util.concurrent.Executor;

/**
 * @auther zhongshunchao
 * @date 05/07/2020 15:18
 */
@SPI("default-pool")
public interface ThreadPool {

    Executor getExecutor(URL url);

}
