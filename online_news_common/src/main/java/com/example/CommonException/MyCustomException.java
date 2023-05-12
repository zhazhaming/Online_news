package com.example.CommonException;


import com.example.CommonResponse.ResponseEnum;

public class MyCustomException extends RuntimeException{

    private ResponseEnum responseEnum;

    public MyCustomException(ResponseEnum responseEnum){
        super("异常状态码为:"+responseEnum.status ()+";"+"异常信息为:"+responseEnum.msg ());
        this.responseEnum = responseEnum;
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }
}
