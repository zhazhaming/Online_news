package com.example.api.controller.user;

import com.example.CommonResponse.CommonResponse;
import com.example.pojo.bo.UpdateUserInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "controller的标题",tags = {"UserController的功能"})
@RequestMapping("user")
public interface UserControllerAPI {

    @ApiOperation(value = "获得用户基本信息", notes = "获得用户基本信息", httpMethod = "POST")
    @PostMapping("/getUserInfo")
    public CommonResponse getUserInfo(@RequestParam String userId);

    @ApiOperation(value = "获得用户基本信息", notes = "获得用户基本信息", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    public CommonResponse getAccountInfo(@RequestParam String userId);

    @ApiOperation(value = "修改/完善用户信息", notes = "修改/完善用户信息", httpMethod = "POST")
    @PostMapping("/updateUserInfo")
    public CommonResponse updateUserInfo(
            @RequestBody @Valid UpdateUserInfoBO updateUserInfoBO, BindingResult result);

    @GetMapping("queryByIds")
    @ApiOperation(value = "根据用户id查询用户", notes = "根据用户id查询用户", httpMethod = "GET")
    public CommonResponse queryByIds(@RequestParam String userIds);
}
