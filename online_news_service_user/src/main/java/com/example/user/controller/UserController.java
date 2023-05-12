package com.example.user.controller;

import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import com.example.api.BaseController;
import com.example.api.controller.user.UserControllerAPI;
import com.example.pojo.bo.UpdateUserInfoBO;
import com.example.pojo.AppUser;
import com.example.user.service.UserService;
import com.example.utilis.JsonUtils;
import com.example.utilis.RedisOperator;
import com.example.pojo.vo.AppUserVO;
import com.example.pojo.vo.UserAccountInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class UserController extends BaseController implements UserControllerAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redis;

    @Override
    public CommonResponse getUserInfo(String userId) {
        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return CommonResponse.errorCustom(ResponseEnum.UN_LOGIN);
        }

        // 1. 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);

//        // 3. 查询redis中用户的关注数和粉丝数，放入userVO到前端渲染
//        userVO.setMyFansCounts(getCountsFromRedis(REDIS_WRITER_FANS_COUNTS + ":" + userId));
//        userVO.setMyFollowCounts(getCountsFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId));

        return CommonResponse.ok(userVO);
    }

    @Override
    public CommonResponse getAccountInfo(String userId) {
        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return CommonResponse.errorCustom(ResponseEnum.UN_LOGIN);
        }

        // 1. 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        UserAccountInfoVO accountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, accountInfoVO);

        return CommonResponse.ok(accountInfoVO);
    }

    @Override
    public CommonResponse updateUserInfo(UpdateUserInfoBO updateUserInfoBO, BindingResult result) {
//      校验BO
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return CommonResponse.errorMap(map);
        }
        userService.updateUserInfo (updateUserInfoBO);
        return CommonResponse.ok ();
    }

    private AppUser getUser(String userId) {
        // 查询判断redis中是否包含用户信息，如果包含，则查询后直接返回，就不去查询数据库了
        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        } else {
            user = userService.getUser(userId);
            // 由于用户信息不怎么会变动，对于一些千万级别的网站来说，这类信息不会直接去查询数据库
            // 那么完全可以依靠redis，直接把查询后的数据存入到redis中
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }
        return user;
    }

    @Override
    public CommonResponse queryByIds(String userIds) {
        if (StringUtils.isBlank (userIds)){
            return CommonResponse.errorCustom (ResponseEnum.USER_NOT_EXIST_ERROR);
        }
        List<AppUserVO> publicerList = new ArrayList<> (  );
        List<String > userIdList = JsonUtils.jsonToList (userIds,String.class);
        for (String usrId:userIdList){
            AppUserVO baseUserInfo = getBaseUserInfo (usrId);
            publicerList.add (baseUserInfo);
        }
        return CommonResponse.ok (publicerList);
    }

    private AppUserVO getBaseUserInfo(String userId){
        AppUserVO appUserVO = new AppUserVO ();
        AppUser user = getUser (userId);
        BeanUtils.copyProperties (user,appUserVO);
        return appUserVO;
    }
}
