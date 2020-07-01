package com.example.tinyrpc.registry;

import com.example.tinyrpc.common.URL;

import java.util.Set;

/**
 * @auther zhongshunchao
 * @date 30/06/2020 21:13
 */
public interface Registry {

    Set<String> subscribe(String interfaceName, UpdateAddressCallBack updateAddressCallBack);

    void register(URL url);
}
