package com.example.api.controller.admin;


import com.example.CommonResponse.CommonResponse;
import com.example.pojo.bo.SaveFriendLinkBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "首页友情链接维护", tags = {"首页友情链接维护controller"})
@RequestMapping("friendLinkMng")
public interface FriendLinkControllerAPI {

    @ApiOperation(value = "新增或者修改友情链接", notes = "新增或者修改友情链接", httpMethod = "POST")
    @PostMapping("/saveOrUpdateFriendLink")
    public CommonResponse saveOrUpdateFriendLink(
            @RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO, BindingResult result);

    @ApiOperation(value = "查询友情链接列表", notes = "查询友情链接列表", httpMethod = "POST")
    @PostMapping("/getFriendLinkList")
    public CommonResponse getFriendLinkList();

    @ApiOperation(value = "删除友情链接", notes = "删除友情链接", httpMethod = "POST")
    @PostMapping("/delete")
    public CommonResponse delete(@RequestParam String linkId);

    @ApiOperation(value = "查询客户端友情链接", notes = "查询客户端友情链接", httpMethod = "GET")
    @GetMapping("portal/list")
    public CommonResponse queryPortalALLFriendLink();
}
