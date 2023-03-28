package com.cn.app.chatgptbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.res.AdminHomeRes;
import com.cn.app.chatgptbot.model.res.UserInfoRes;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表(User)表数据库访问层
 *
 * @author  
 * @since 2022-03-12 14:35:54
 */
public interface UserDao extends BaseMapper<User> {

    UserInfoRes getUserInfo(@Param("userId") Long userId);

    AdminHomeRes adminHome();

}
