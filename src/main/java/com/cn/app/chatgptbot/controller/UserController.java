package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表(User)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = {"用户管理"})
public class UserController {


    /**
     * UserService
     */
    final IUserService userService;

    /**
     * 分页查询User
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询User")
    public B queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return userService.queryPage(basePageHelper);
    }

    /**
     * 新增User
     *
     * @param params 参数
     * @return 返回对象
     */
//    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增User")
    public B add(@Validated @RequestBody String params) {
        return userService.add(params);
    }


    /**
     * 编辑User
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑User")
    public B update(@Validated @RequestBody String params) {
        return userService.update(params);
    }

    /**
     * 通过id删除User
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除User")
    public B delete(@Validated @RequestBody BaseDeleteEntity params) {
        return userService.delete(params);
    }

}
