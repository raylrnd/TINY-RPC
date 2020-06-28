package com.example.tinyrpc.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther zhongshunchao
 * @date 28/06/2020 14:00
 */
public class RPCStatus {
    private static final Map<String, Integer> ACTIVE_COUNT = new ConcurrentHashMap<>();

    public synchronized static int getCount(String interfaceName, String methodName, String address) {
        Integer count = ACTIVE_COUNT.get(generateKey(interfaceName, methodName, address));
        return count == null ? 0 : count;
    }

    public synchronized static void incCount(String interfaceName, String methodName, String address) {
        String key = generateKey(interfaceName, methodName, address);
        if (ACTIVE_COUNT.containsKey(key)) {
            ACTIVE_COUNT.put(key, ACTIVE_COUNT.get(key) + 1);
        } else {
            ACTIVE_COUNT.put(key, Integer.valueOf(1));
        }
    }

    public synchronized static void decCount(String interfaceName, String methodName, String address) {
        String key = generateKey(interfaceName, methodName, address);
        ACTIVE_COUNT.put(key, ACTIVE_COUNT.get(key) - 1);
    }

    private static String generateKey(String interfaceName, String methodName, String address) {
        return new StringBuilder(interfaceName).append(".").append(methodName).append(".").append(address).toString();
    }
}
