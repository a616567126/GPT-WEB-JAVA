package com.chat.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.UserAddReq;
import com.chat.java.model.req.UserPageReq;
import com.chat.java.model.req.UserUpdateReq;
import com.chat.java.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = {"用户管理"})
public class UserController {


    /**
     * UserService
     */
    final IUserService userService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询User")
    public B<JSONObject> queryPage(@Validated @RequestBody UserPageReq req) {
        return userService.queryPage(req);
    }


//    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增User")
    public B<Void> add(@Validated @RequestBody UserAddReq req) {
        return userService.add(req);
    }



    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑User")
    public B<Void> update(@Validated @RequestBody UserUpdateReq req) {
        return userService.update(req);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除User")
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return userService.delete(params);
    }

}
