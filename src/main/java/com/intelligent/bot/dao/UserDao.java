package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.req.sys.admin.UserQueryPageReq;
import com.intelligent.bot.model.res.sys.admin.AdminHomeRes;
import com.intelligent.bot.model.res.sys.admin.UserQueryPageRes;
import org.apache.ibatis.annotations.Param;


public interface UserDao extends BaseMapper<User> {


    User checkTempUser(@Param("browserFingerprint") String browserFingerprint, @Param("ipAddress") String ipAddress);

    Page<UserQueryPageRes> queryUserPage(@Param("page") Page<UserQueryPageRes> page, @Param("req") UserQueryPageReq req);

    AdminHomeRes adminHome();

    User getOne(@Param("fromUser") String fromUser, @Param("mobile") String mobile);}
