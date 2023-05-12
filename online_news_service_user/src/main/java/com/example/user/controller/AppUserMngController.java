package com.example.user.controller;

import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import com.example.api.BaseController;
import com.example.api.controller.user.AppUserMngControllerApi;
import com.example.enums.UserStatus;
import com.example.pojo.AppUser;
import com.example.pojo.vo.AppUserVO;
import com.example.user.service.AppUserMngService;
import com.example.user.service.UserService;
import com.example.utilis.JsonUtils;
import com.example.utilis.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class AppUserMngController extends BaseController implements AppUserMngControllerApi {

    @Autowired
    AppUserMngService appUserMngService;

    @Autowired
    UserService userService;

    @Override
    public CommonResponse queryAll(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        if (page == null) {
            page =COMMON_START_PAGE;
        }
        if (pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        PagedGridResult result = appUserMngService.queryAllUserList (nickname, status, startDate, endDate, page, pageSize);
        return CommonResponse.ok (result);
    }

    @Override
    public CommonResponse userDetail(String userId) {
        return CommonResponse.ok (userService.getUser (userId));
    }

    @Override
    public CommonResponse freezeUserOrNot(String userId, Integer doStatus) {
        if (!UserStatus.isUserStatusValid (doStatus)) {
            return CommonResponse.errorCustom(ResponseEnum.USER_STATUS_ERROR);
        }
        appUserMngService.freezeUserOrNot (userId,doStatus);
        redis.del (REDIS_USER_INFO+":"+userId);
        return CommonResponse.ok ();
    }


}
