package com.example.tinyrpc.Codec;

/**
 * @auther zhongshunchao
 * @date 17/05/2020 15:40
 */
public interface Resolver {

    boolean support(Message message);

    Message resolve(Message message);
}
