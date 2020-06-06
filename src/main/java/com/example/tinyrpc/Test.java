package com.example.tinyrpc;

import com.example.tinyrpc.demo.HelloWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @auther zhongshunchao
 * @date 02/06/2020 08:49
 */
@Component
public class Test {

    @Autowired
    HelloWorld helloWorld;

    void fun(){
        helloWorld.run();
    }
}
