package com.example.user.service.impl;

import com.example.api.service.BaseService;
import com.example.enums.UserStatus;
import com.example.pojo.AppUser;
import com.example.user.mapper.AppUserMapper;
import com.example.user.service.AppUserMngService;
import com.example.utilis.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AppUserServiceImpl extends BaseService implements AppUserMngService {

    @Autowired
    public AppUserMapper appUserMapper;

    @Override
    public PagedGridResult queryAllUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        Example example = new Example (AppUser.class);
        example.orderBy ("createdTime").desc ();
        Example.Criteria criteria = example.createCriteria ( );
        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname + "%");
        }

        if (UserStatus.isUserStatusValid(status)) {
            criteria.andEqualTo("activeStatus", status);
        }

        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", startDate);
        }
        if (endDate != null) {
            criteria.andLessThanOrEqualTo("createdTime", endDate);
        }

        PageHelper.startPage(page, pageSize);
        List<AppUser> list = appUserMapper.selectByExample(example);

        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void freezeUserOrNot(String userId, Integer doStatus) {
        AppUser user = new AppUser();
        user.setId(userId);
        user.setActiveStatus(doStatus);
        appUserMapper.updateByPrimaryKeySelective(user);
    }
}
