package com.example.api.interceptors;

import com.example.CommonException.CommonException;
import com.example.CommonResponse.ResponseEnum;
import com.example.utilis.IPUtil;
import com.example.utilis.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userIp = IPUtil.getRequestIp (request);
        boolean keyIsExit = redis.keyIsExist (MOBILE_SMSCODE+":"+userIp);
        if (keyIsExit){
            CommonException.display (ResponseEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle (request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion (request, response, handler, ex);
    }
}
