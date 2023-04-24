package com.chat.java.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.base.B;
import com.chat.java.model.User;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.*;
import com.chat.java.model.res.UserInfoRes;
import com.chat.java.model.res.AdminHomeRes;

/**
 * 用户表(User)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IUserService extends IService<User> {



    B<JSONObject> queryPage(UserPageReq req);


    B<Void> add(UserAddReq req);


    B<Void> update(UserUpdateReq req);


    B<Void> delete(BaseDeleteEntity params);

    B<Void> register(RegisterReq req);

    B<String> registerMsm(MsmRegisterReq req);

    B<String> registerEmail(EmailRegisterReq req);
    B<UserInfoRes> home();

    B<AdminHomeRes> adminHome();
    B<UserInfoRes> getType();

    B<UserInfoRes> getType(Long userId);


}
