package com.example.tinyrpc;

import com.example.tinyrpc.demo.HelloWorld;
import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.server.NettyServer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @auther zhongshunchao
 * @date 02/06/2020 22:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class ClientTest {

    @Autowired
    public HelloWorld helloWorld;

    @Test
    void testClient() {
        String result = helloWorld.run();
        System.out.println(result);
        System.out.close();
    }

}