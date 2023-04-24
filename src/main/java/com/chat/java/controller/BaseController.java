package com.chat.java.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.chat.java.constant.CommonConst;
import com.chat.java.exception.CustomException;
import com.chat.java.model.SysConfig;
import com.chat.java.model.User;
import com.chat.java.model.base.UserLogin;
import com.chat.java.model.req.*;
import com.chat.java.model.res.UserInfoRes;
import com.chat.java.utils.EmailServiceUtil;
import com.chat.java.utils.JwtUtil;
import com.chat.java.utils.MsmServiceUtil;
import com.chat.java.utils.RedisUtil;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.config.AvoidRepeatRequest;
import com.chat.java.model.res.AdminHomeRes;
import com.chat.java.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @date 2022-03-25 16:00
 */
@RestController
@RequestMapping("/user/token")
@RequiredArgsConstructor
@Api(tags = {"用户/管理员登录、注册，首页，获取用户类型"})
public class BaseController {


    final IUserService userService;

    final RedisUtil redisUtil;

    final EmailServiceUtil emailServiceUtil;



    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录")
    @AvoidRepeatRequest(intervalTime = 60 * 3L ,msg = "请勿短时间连续登录")
    public B<JSONObject> userLogin(@Validated @RequestBody UserLogin userLogin) {
        List<User> list = userService.lambdaQuery()
                .eq(User::getMobile, userLogin.getMobile())
                .eq(User::getPassword, userLogin.getPassword())
                .ne(User::getType,-1)
                .list();
        if (list == null || list.size() == 0) {
            return B.finalBuild("用户名或密码错误");
        }
        User user = list.get(0);
        JSONObject jsonObject = new JSONObject();
        //生成token
        String token = JwtUtil.createToken(user);
        jsonObject.put("token", token);
        jsonObject.put("userId", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("type", user.getType());
        jsonObject.put("expirationTime", user.getExpirationTime());
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        jsonObject.put("lastLoginTime", null == user.getLastLoginTime() ? nweUser.getLastLoginTime() : user.getLastLoginTime());
        userService.updateById(nweUser);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);

    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "注册")
    @AvoidRepeatRequest(msg = "请勿短时间内重复注册")
    public B register(@Validated @RequestBody RegisterReq req) {
        return userService.register(req);
    }

    @RequestMapping(value = "/register/msm", method = RequestMethod.POST)
    @ApiOperation(value = "短信验证码注册")
    @AvoidRepeatRequest(msg = "请勿短时间内重复注册")
    public B<String> registerMsm(@Validated @RequestBody MsmRegisterReq req) {
        return userService.registerMsm(req);
    }


    @RequestMapping(value = "/register/email", method = RequestMethod.POST)
    @ApiOperation(value = "邮件验证码注册")
    @AvoidRepeatRequest(msg = "请勿短时间内重复注册")
    public B<String> registerEmail(@Validated @RequestBody EmailRegisterReq req) {
        return userService.registerEmail(req);
    }

    @RequestMapping(value = "/home", method = RequestMethod.POST)
    @ApiOperation(value = "首页信息")
    public B<UserInfoRes> home() {
        return userService.home();
    }

    @RequestMapping(value = "/getType", method = RequestMethod.POST)
    @ApiOperation(value = "查询用户类型")
    public B<UserInfoRes> getType() {
        return userService.getType();
    }


    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录")
    public B<JSONObject> adminLogin(@Validated @RequestBody UserLogin userLogin) {
        List<User> list = userService.lambdaQuery()
                .eq(User::getMobile, userLogin.getMobile())
                .eq(User::getPassword, userLogin.getPassword())
                .eq(User::getType, -1)
                .list();
        if (list == null || list.size() == 0) {
            return B.finalBuild("用户名或密码错误");
        }
        User user = list.get(0);
        JSONObject jsonObject = new JSONObject();
        //生成token
        String token = JwtUtil.createToken(user);
        jsonObject.put("token", token);
        jsonObject.put("userId", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("type", user.getType());
        jsonObject.put("expirationTime", user.getExpirationTime());
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        jsonObject.put("lastLoginTime", null == user.getLastLoginTime() ? nweUser.getLastLoginTime() : user.getLastLoginTime());
        userService.updateById(nweUser);
        redisUtil.setCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + user.getId(), SecureUtil.md5(token),
                CommonConst.TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);

    }

    @RequestMapping(value = "/admin/home", method = RequestMethod.POST)
    @ApiOperation(value = "管理端首页信息")
    public B<AdminHomeRes> adminHome() {
        return userService.adminHome();
    }


    @RequestMapping(value = "/send/msg", method = RequestMethod.POST)
    @ApiOperation(value = "发送短信")
    @AvoidRepeatRequest(intervalTime = 180,msg = "请勿频繁发送验证码")
    public B<Void> sendMsg(@Validated @RequestBody SendMsgReq req) throws ClientException {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        if(sysConfig.getRegistrationMethod() != 2 ){
            throw new ClientException("短信注册暂未开启");
        }
        Long count = this.userService.lambdaQuery().eq(User::getMobile, req.getMobile()).count();
        if(count > 0){
            throw new CustomException("用户已存在，请勿重复注册");
        }
        String code = RandomUtil.randomNumbers(6);
        boolean result = MsmServiceUtil.send(req.getMobile(),code);
        if(result){
            RedisUtil.setCacheObject("MSM:"+req.getMobile(),code, 3L, TimeUnit.MINUTES);
        }
        return B.okBuild();
    }
    @RequestMapping(value = "/get/register/method", method = RequestMethod.POST)
    @ApiOperation(value = "查询注册方式")
    public B<Integer> getRegisterMethod() {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        return B.okBuild(sysConfig.getRegistrationMethod());
    }

    @RequestMapping(value = "/send/mail", method = RequestMethod.POST)
    @ApiOperation(value = "发送邮件")
    @AvoidRepeatRequest(intervalTime = 180,msg = "请勿频繁发送验证码")
    public B<Void> sendEmail(@Validated @RequestBody SendEmailReq req) throws ClientException {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        if(sysConfig.getRegistrationMethod() != 4 ){
            throw new ClientException("邮件注册暂未开启");
        }
        Long count = this.userService.lambdaQuery().eq(User::getEmail, req.getEmail()).count();
        if(count > 0){
            throw new ClientException("用户已存在，请勿重复注册");
        }
        emailServiceUtil.sendEmail(req.getEmail());
        return B.okBuild();
    }

}
