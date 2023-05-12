package com.example.CommonException;


import com.example.CommonResponse.ResponseEnum;

public class CommonException {

    public static void display(ResponseEnum responseEnum){
        throw new MyCustomException(responseEnum);
    }
}
