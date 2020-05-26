package com.example.tinyrpc.demo;

import com.example.tinyrpc.common.annotation.Reference;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 3:01 下午
 */
public class HelloClient {

    //client端通过接口的全限定名去zk注册中心寻找服务地址
    @Reference(async = true, callback = true)
    private HelloService helloService;


}
