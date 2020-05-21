package com.example.tinyrpc.demo;

import com.example.tinyrpc.common.annotation.Reference;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 3:01 下午
 */
public class HelloClient {

    @Reference
    private HelloService helloService;


}
