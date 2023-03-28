package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.RegisterReq;
import com.cn.app.chatgptbot.model.res.AdminHomeRes;
import com.cn.app.chatgptbot.model.res.UserInfoRes;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户表(User)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IUserService extends IService<User> {


    /**
     * 分页查询User
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    B queryPage(BasePageHelper basePageHelper);

    /**
     * 新增User
     *
     * @param params 参数
     * @return 返回对象
     */
    B add(String params);

    /**
     * 修改User
     *
     * @param params 参数
     * @return 返回对象
     */
    B update(String params);

    /**
     * 通过id删除User
     *
     * @param params 参数
     * @return 返回对象
     */
    B delete(BaseDeleteEntity params);

    B register(RegisterReq req);
    B<UserInfoRes> home();

    B<AdminHomeRes> adminHome();
    B<UserInfoRes> getType();



}
