package com.example.tinyrpc.transport.server;

import com.example.tinyrpc.transport.Server;

/**
 * @auther zhongshunchao
 * @date 30/05/2020 23:16
 */
public class APP {
    public static void main(String[] args) {
        Server server = new NettyServer();
        server.run("127.0.0.1", 8585);
    }
}
