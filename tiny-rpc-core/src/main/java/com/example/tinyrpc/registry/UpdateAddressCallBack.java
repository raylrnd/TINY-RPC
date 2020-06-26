package com.example.tinyrpc.registry;

import java.util.List;
import java.util.Set;

/**
 * @auther zhongshunchao
 * @date 23/06/2020 00:03
 */
@FunctionalInterface
public interface UpdateAddressCallBack {
    //更新缓存中的zk地址
    void updateAddress(List<String> addUrlList, Set<String> closeUrlSet);

}
