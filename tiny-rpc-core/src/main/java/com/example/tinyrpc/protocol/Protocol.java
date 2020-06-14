package com.example.tinyrpc.protocol;

import com.example.tinyrpc.transport.Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 13/06/2020 21:15
 */
public class Protocol {
    private Map<String, Client> clientMap = new ConcurrentHashMap<>();


}
