package com.example.user.controller;


import com.example.api.controller.user.HelloControllerAPI;
import com.example.CommonResponse.CommonResponse;
import com.example.utilis.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerAPI {

    @Autowired
    private RedisOperator redis;

    @Override
    public CommonResponse user(){
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse redis() {
        redis.set ("name","zhazha");
        return CommonResponse.ok ( redis.get ("name") );
    }
}
