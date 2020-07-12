package com.example.tinyrpc.common.exception;

/**
 * @auther zhongshunchao
 * @date 24/06/2020 22:32
 * 用于处理业务上的异常
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

