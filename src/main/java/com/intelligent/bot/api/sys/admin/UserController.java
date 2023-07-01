package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.UserAddReq;
import com.intelligent.bot.model.req.sys.admin.UserQueryPageReq;
import com.intelligent.bot.model.req.sys.admin.UserUpdateReq;
import com.intelligent.bot.model.res.sys.admin.AdminHomeRes;
import com.intelligent.bot.model.res.sys.admin.UserQueryPageRes;
import com.intelligent.bot.service.sys.IUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/user")
public class UserController {


    @Resource
    IUserService userService;


    @RequestMapping(value = "/queryPage",name = "分页查询User", method = RequestMethod.POST)
    public B<Page<UserQueryPageRes>> queryPage(@Validated @RequestBody UserQueryPageReq req) {
        return userService.queryPage(req);
    }

    @RequestMapping(value = "/add",name = "修改用户", method = RequestMethod.POST)
    public B<Void>  add(@Validated @RequestBody UserAddReq req) {
        return userService.add(req);
    }
    @RequestMapping(value = "/update",name = "修改用户", method = RequestMethod.POST)
    public B<Void>  update(@Validated @RequestBody UserUpdateReq req) {
        return userService.update(req);
    }

    @RequestMapping(value = "/delete",name = "删除User", method = RequestMethod.POST)
    public B<Void>  delete(@Validated @RequestBody BaseDeleteEntity params) {
        return userService.delete(params);
    }


    @RequestMapping(value = "/admin/home",name = "管理端首页信息", method = RequestMethod.POST)
    public B<AdminHomeRes> adminHome() {
        return userService.adminHome();
    }
}
