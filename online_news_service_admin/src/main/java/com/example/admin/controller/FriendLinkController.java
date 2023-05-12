package com.example.admin.controller;

import com.example.CommonResponse.CommonResponse;
import com.example.admin.service.FriendLinkService;
import com.example.api.BaseController;
import com.example.api.controller.admin.FriendLinkControllerAPI;
import com.example.pojo.bo.SaveFriendLinkBO;

import com.example.pojo.mo.FriendLinkMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerAPI {

    final static Logger logger = LoggerFactory.getLogger(FriendLinkController.class);

    @Autowired
    FriendLinkService friendLinkService;

    @Override
    public CommonResponse saveOrUpdateFriendLink(SaveFriendLinkBO saveFriendLinkBO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return CommonResponse.errorMap(map);
        }
        FriendLinkMO friendLinkMO = new FriendLinkMO ();
        BeanUtils.copyProperties (saveFriendLinkBO,friendLinkMO);
        friendLinkMO.setCreateTime (new Date (  ));
        friendLinkMO.setUpdateTime (new Date());
        friendLinkService.saveOrUpdateFriendLink (friendLinkMO);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse getFriendLinkList() {
        return CommonResponse.ok (friendLinkService.queryAllFriendLinkList ( ));
    }

    @Override
    public CommonResponse delete(String linkId) {
        friendLinkService.delete (linkId);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse queryPortalALLFriendLink() {
        List<FriendLinkMO> friendLinkList = friendLinkService.queryPortalAllFriendLinkList ( );
        return CommonResponse.ok (friendLinkList);
    }
}
