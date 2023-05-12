package com.example.user.controller;

import com.example.enums.UserStatus;
import com.example.CommonResponse.ResponseEnum;
import com.example.api.BaseController;
import com.example.api.controller.user.PassportControllerApi;
import com.example.CommonResponse.CommonResponse;
import com.example.pojo.bo.RegistLoginBO;
import com.example.pojo.AppUser;
import com.example.user.service.UserService;
import com.example.utilis.IPUtil;
import com.example.utilis.JsonUtils;
import com.example.utilis.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private UserService userService;

    @Value ("${website.domain-name}")
    private String domain;

    @Override
    public CommonResponse getSMsCode(String mobile, HttpServletRequest request) {
        String userIp = IPUtil.getRequestIp (request);
        redis.setnx60s (MOBILE_SMSCODE+":"+userIp,userIp);
        String random = (int)((Math.random() * 9 + 1) * 100000) + "";
        System.out.println (random );
//        smsUtils.sendSMS (mobile,random);
        redis.set (MOBILE_SMSCODE+":"+mobile,random,30*60);
        return CommonResponse.ok (redis.get (MOBILE_SMSCODE+":"+mobile));
    }

    @Override
    public CommonResponse doLogin(RegistLoginBO registLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        //如果有错误信息就返回给前端
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return CommonResponse.errorMap(map);
        }

        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getsmsCode ();

        //校验验证码是否匹配
        String redisSMSCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSMSCode) || !redisSMSCode.equalsIgnoreCase(smsCode)) {
            return CommonResponse.errorCustom(ResponseEnum.SMS_CODE_ERROR);
        }

        //查询数据库，判断该用户是否注册
        AppUser user = userService.queryMobileIsExist(mobile);
        if (user != null && user.getActiveStatus() == UserStatus.FROZEN.type) {
            // 如果用户不为空，并且状态为冻结，则直接抛出异常，禁止登录
            return CommonResponse.errorCustom(ResponseEnum.USER_FROZEN);
        } else if (user == null) {
            // 如果用户没有注册过，则为null，需要注册信息入库
            user = userService.createUser(mobile);
        }

        // 保存用户分布式会话的相关操作
        int userActiveStatus = user.getActiveStatus();
        if (userActiveStatus != UserStatus.FROZEN.type) {
            // 保存token到redis
            String uToken = UUID.randomUUID().toString();
            redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
            redis.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user));

            // 保存用户id和token到cookie中

            setCookie(request, response, "utoken", uToken, COOKIE_MONTH,domain);
            setCookie(request, response, "uid", user.getId(), COOKIE_MONTH,domain);
        }
        // 用户登录或注册成功以后，需要删除redis中的短信验证码，验证码只能使用一次，用过后则作废
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        //返回用户状态
        return CommonResponse.ok (userActiveStatus);
    }

    @Override
    public CommonResponse Logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        redis.del (REDIS_USER_TOKEN+":"+userId);
        redis.del (MOBILE_SMSCODE+":"+IPUtil.getRequestIp (request));
        setCookie (request,response,"utoken", "",COOKIE_DEL,domain);
        setCookie (request,response,"uid", "",COOKIE_DEL,domain);
        return CommonResponse.ok ();
    }
}
