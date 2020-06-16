package com.example.tinyrpc.protocol;

import com.example.tinyrpc.transport.Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 13/06/2020 21:15
 */
public class Protocol {

//    private Map<String, Client> clientMap = new ConcurrentHashMap<>();
    private final Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();



    /**
     * Dubbo中的export()是将invoker转化为Exporter, DubboExporter持有exporterMap对象，该exporterMap可以理解为是当前的invokerList，使用者可以通过export方法得到Exporter，进而得到当前的invokerList集合
     * 我这的export()是将接口信息（接口全限定名 -> ip:port）注册到Zookeeper中，然后开启Server
     * @param invoker
     */
    public void export(Invoker invoker) {


        //开启server

    }


}
