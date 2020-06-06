package com.example.tinyrpc.transport;

import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 11:25 上午
 */
@Component
public interface Client {

    void run(String hostName, int port);

    Future<Object> send(Request message);
}
