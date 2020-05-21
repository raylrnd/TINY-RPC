package com.example.tinyrpc.transport;

import com.example.tinyrpc.common.Response;

import java.util.concurrent.Future;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:25 上午
 */
public interface Client {

    void run(String hostName, int port);

    void close();

    Future<Response> send(Object message);
}
