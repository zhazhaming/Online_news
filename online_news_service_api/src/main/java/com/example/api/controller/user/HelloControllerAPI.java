package com.example.api.controller.user;

import com.example.CommonResponse.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;


@Api(value = "controller的标题",tags = {"HelloController的功能"})
public interface HelloControllerAPI {
    @ApiOperation (value = "hello发法接口测试",notes = "hello发法接口测试")
    @RequestMapping("/user")
    public CommonResponse user();

    @ApiOperation (value = "redis发法接口测试",notes = "redis发法接口测试")
    @RequestMapping("/redis")
    public CommonResponse redis();
}
