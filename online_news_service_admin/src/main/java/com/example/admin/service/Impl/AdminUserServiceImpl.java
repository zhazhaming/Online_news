package com.example.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.example.admin.mapper.AdminUserMapper;
import com.example.admin.service.AdminUserService;
import com.example.api.service.BaseService;
import com.example.CommonException.CommonException;
import com.example.CommonResponse.ResponseEnum;
import com.example.pojo.AdminUser;
import com.example.pojo.bo.NewAdminBO;
import com.example.utilis.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
//import org.n3r.idworker.Sid;
import com.example.utilis.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AdminUserServiceImpl extends BaseService implements AdminUserService {

    @Autowired
    public AdminUserMapper adminUserMapper;

    @Autowired
    private Sid sid;

    @Override
    public AdminUser queryAdminByUsername(String username) {

        Example adminExample = new Example(AdminUser.class);
        Example.Criteria criteria = adminExample.createCriteria();
        criteria.andEqualTo("username", username);

        AdminUser admin = adminUserMapper.selectOneByExample(adminExample);
        return admin;
    }

    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) {

        String adminId = sid.nextShort();

        AdminUser adminUser = new AdminUser();
        adminUser.setId(adminId);
        adminUser.setUsername(newAdminBO.getUsername());
        adminUser.setAdminName(newAdminBO.getAdminName());

        // 如果密码不为空，则需要加密密码，存入数据库
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            String pwd = BCrypt.hashpw(newAdminBO.getPassword(), BCrypt.gensalt());
            adminUser.setPassword(pwd);
        }

        // 如果人脸上传以后，则有faceId，需要和admin信息关联存储入库
        if (StringUtils.isNotBlank(newAdminBO.getFaceId())) {
            adminUser.setFaceId(newAdminBO.getFaceId());
        }

        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());

        int result = adminUserMapper.insert(adminUser);
        if (result != 1) {
            CommonException.display(ResponseEnum.ADMIN_CREATE_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        Example adminExample = new Example(AdminUser.class);
        adminExample.orderBy("createdTime").asc ();

        PageHelper.startPage(page, pageSize);
        List<AdminUser> adminUserList =
                adminUserMapper.selectByExample(adminExample);

        return setterPagedGrid(adminUserList, page);
    }

//    private PagedGridResult setterPagedGrid(List<?> adminUserList,
//                                            Integer page) {
//        PageInfo<?> pageList = new PageInfo<>(adminUserList);
//        PagedGridResult gridResult = new PagedGridResult();
//        gridResult.setRows(adminUserList);
//        gridResult.setPage(page);
//        gridResult.setRecords(pageList.getPages());
//        gridResult.setTotal(pageList.getTotal());
//        return gridResult;
//    }
}
