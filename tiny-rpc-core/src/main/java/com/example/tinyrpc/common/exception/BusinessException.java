package com.example.tinyrpc.common.exception;

/**
 * @auther zhongshunchao
 * @date 24/06/2020 22:32
 */
public class BusinessException extends RuntimeException {
    private String code;

    private String keyword;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String keyword, String message) {
        super(message);
        this.code = code;
        this.keyword = keyword;
    }
}

