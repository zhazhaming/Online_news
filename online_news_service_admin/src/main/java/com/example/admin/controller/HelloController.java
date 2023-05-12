package com.example.admin.controller;


import com.example.CommonResponse.CommonResponse;
import com.example.api.controller.user.HelloControllerAPI;
import com.example.utilis.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerAPI {

    @Override
    public CommonResponse user(){
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse redis() {
        return CommonResponse.ok ();
    }
}
