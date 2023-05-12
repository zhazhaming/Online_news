package com.example.user.mapper;

import com.example.my.mapper.MyMapper;
import com.example.pojo.AppUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMapper extends MyMapper<AppUser> {
}