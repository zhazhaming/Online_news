package com.example.admin.mapper;

import com.example.my.mapper.MyMapper;
import com.example.pojo.AdminUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserMapper extends MyMapper<AdminUser> {
}