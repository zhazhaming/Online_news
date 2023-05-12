package com.example.api.controller.user;

import com.example.CommonResponse.CommonResponse;
import com.example.pojo.bo.RegistLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Api(value = "用户登录注册",tags = {"PassportController的功能"})
@RequestMapping("passport")
public interface PassportControllerApi {
    @ApiOperation(value = "获取短信验证码",notes = "获取短信验证码",httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public CommonResponse getSMsCode(@RequestParam String moblie, HttpServletRequest request);

    @ApiOperation(value = "一键注册登录接口", notes = "一键注册登录接口", httpMethod = "POST")
    @PostMapping("/doLogin")
    public CommonResponse doLogin(@RequestBody @Valid RegistLoginBO registLoginBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response);

    @ApiOperation(value = "用户退出登录接口", notes = "用户退出登录接口", httpMethod = "POST")
    @PostMapping("/logout")
    public CommonResponse Logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response);
}
