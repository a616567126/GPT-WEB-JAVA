package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.UserAddReq;
import com.intelligent.bot.model.req.sys.admin.UserQueryPageReq;
import com.intelligent.bot.model.req.sys.admin.UserUpdateReq;
import com.intelligent.bot.model.res.sys.admin.AdminHomeRes;
import com.intelligent.bot.model.res.sys.admin.UserQueryPageRes;


public interface IUserService extends IService<User> {

    B<Page<UserQueryPageRes>>  queryPage(UserQueryPageReq req);

    B<Void> add(UserAddReq req);

    B<Void> update(UserUpdateReq req);

    B<Void> delete(BaseDeleteEntity req);

    User checkTempUser(String browserFingerprint, String ipaddress);

    B<AdminHomeRes> adminHome();

    User getOne(String fromUser,String mobile);


}
