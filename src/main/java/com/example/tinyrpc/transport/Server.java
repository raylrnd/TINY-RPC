package com.example.tinyrpc.transport;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:29 上午
 */
public interface Server {

    void run(String hostName, int port);
    void close();
    void received(Object message);
}
