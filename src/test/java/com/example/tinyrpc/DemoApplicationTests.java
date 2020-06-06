package com.example.tinyrpc;

import com.example.tinyrpc.demo.HelloWorld;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @auther zhongshunchao
 * @date 02/06/2020 22:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class DemoApplicationTests {

//    @MyReference(async = true, callback = true)
//    private IRemoteService helloService;

    @Autowired
    public HelloWorld helloWorld;

    @Test
    void contextLoads() {
        String run = helloWorld.run();
//        String hello = helloService.hello("he");
        System.out.close();
    }

    @Test
    void runClient() {

    }

}