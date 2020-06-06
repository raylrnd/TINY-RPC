package com.example.tinyrpc.common;

/**
 * @auther zhongshunchao
 * @date 06/06/2020 11:04
 */
public class ResponseBody {
    private String errorMsg;
    private Object result;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
