package com.example.api.interceptors;

import com.example.enums.UserStatus;
import com.example.CommonException.CommonException;
import com.example.CommonResponse.ResponseEnum;
import com.example.pojo.AppUser;
import com.example.utilis.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader ("headerUserId");
        String userJson = redis.get (REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if (StringUtils.isNotBlank (userJson)) {
            user = JsonUtils.jsonToPojo (userJson, AppUser.class);
        } else {
            CommonException.display (ResponseEnum.UN_LOGIN);
            return false;
        }
        if (user.getActiveStatus ( ) == null
                || user.getActiveStatus ( ) != UserStatus.ACTIVE.type) {
            CommonException.display (ResponseEnum.USER_INACTIVE_ERROR);
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
