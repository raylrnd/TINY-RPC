package com.example.tinyrpc.transport;

import com.example.tinyrpc.demo.HelloWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @auther zhongshunchao
 * @date 01/06/2020 23:28
 */
@Component
public class TestHello {

    @Autowired
    public HelloWorld helloWorld;

    public void contextLoads() {
        String run = helloWorld.run();
    }
}
