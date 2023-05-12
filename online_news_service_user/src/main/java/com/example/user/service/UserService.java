package com.example.user.service;

import com.example.pojo.bo.UpdateUserInfoBO;
import com.example.pojo.AppUser;

public interface UserService {

    public AppUser queryMobileIsExist(String mobile);

    public AppUser createUser(String mobile);

    public AppUser getUser(String userId);

    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO);


}
