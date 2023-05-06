package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.OrderDao;
import com.chat.java.dao.RefuelingKitDao;
import com.chat.java.dao.UserDao;
import com.chat.java.exception.CustomException;
import com.chat.java.model.Announcement;
import com.chat.java.model.SysConfig;
import com.chat.java.model.UseLog;
import com.chat.java.model.User;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.*;
import com.chat.java.model.res.*;
import com.chat.java.service.IAnnouncementService;
import com.chat.java.service.IUseLogService;
import com.chat.java.service.IUserService;
import com.chat.java.utils.JwtUtil;
import com.chat.java.model.res.*;

import javax.annotation.Resource;

import com.chat.java.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户表(User)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("userService")
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {


    @Resource
    RefuelingKitDao refuelingKitDao;

    @Resource
    IAnnouncementService announcementService;

    @Resource
    IUseLogService useLogService;

    @Resource
    OrderDao orderDao;


    @Override
    public B<JSONObject> queryPage(UserPageReq req) {
        JSONObject jsonObject = new JSONObject();
        Page<User> page = new Page<>(req.getPageNumber(),req.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("id");
        queryWrapper.like(null != req.getName(),"name",req.getName());
        queryWrapper.like(null != req.getMobile(),"mobile",req.getMobile());
        Page<User> userPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("userPage",userPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    @Override
    public  B<Void> add(UserAddReq req) {
        User user = BeanUtil.copyProperties(req, User.class);
        Long count = this.lambdaQuery().eq(null != user.getName(), User::getName, user.getName())
                .eq(null != user.getMobile(), User::getMobile, user.getMobile())
                .count();
        if(count > 0){
            return B.finalBuild("用户已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setOperateTime(LocalDateTime.now());
        this.save(user);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }


    @Override
    public  B<Void> update(UserUpdateReq req) {
        User user = BeanUtil.copyProperties(req, User.class);
        if (null == user || null == user.getId()) {
            return B.finalBuild("参数校验失败");
        }
        Long count = this.lambdaQuery()
                .eq(null != user.getName(), User::getName, user.getName())
                .eq(null != user.getMobile(), User::getMobile, user.getMobile())
                .ne(User::getId,user.getId())
                .count();
        if(count > 0){
            return B.finalBuild("用户已存在");
        }
        user.setOperateTime(LocalDateTime.now());
        this.saveOrUpdate(user);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }


    @Override
    public  B<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public  B<Void> register(RegisterReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        if(sysConfig.getRegistrationMethod() != 1){
            throw new CustomException("暂未开放账号密码注册");
        }
        User user = BeanUtil.copyProperties(req, User.class);
        Long count = this.lambdaQuery().eq(null != user.getName(), User::getName, user.getName())
                .eq(null != user.getMobile(), User::getMobile, user.getMobile())
                .count();
        if(count > 0){
            return B.finalBuild("用户已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setOperateTime(LocalDateTime.now());
        user.setRemainingTimes(sysConfig.getDefaultTimes());
        this.save(user);
        return B.okBuild();
    }

    @Override
    public B<String> registerMsm(MsmRegisterReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        if(sysConfig.getRegistrationMethod() != 2){
            throw new CustomException("暂未开放账号密码注册");
        }
        String code = RedisUtil.getCacheObject("MSM:" + req.getMobile());
        if(StringUtils.isEmpty(code) || !code.equals(req.getMsgCode())){
            throw new CustomException("验证码错误");
        }else {
            RedisUtil.deleteObject("MSM:" + req.getMobile());
        }
        User user = BeanUtil.copyProperties(req, User.class);
        Long count = this.lambdaQuery().eq(null != user.getName(), User::getName, user.getName())
                .eq(null != user.getMobile(), User::getMobile, user.getMobile())
                .count();
        if(count > 0){
            return B.finalBuild("用户已存在");
        }
        user.setPassword("123456");
        user.setCreateTime(LocalDateTime.now());
        user.setOperateTime(LocalDateTime.now());
        user.setRemainingTimes(sysConfig.getDefaultTimes());
        this.save(user);
        return B.okBuild("密码：123456");
    }

    @Override
    public B<String> registerEmail(EmailRegisterReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        if(sysConfig.getRegistrationMethod() != 4){
            throw new CustomException("暂未开放邮件注册");
        }
        String code = RedisUtil.getCacheObject("EMAIL:" + req.getEmail());
        if(StringUtils.isEmpty(code) || !code.equals(req.getEmailCode())){
            throw new CustomException("验证码错误");
        }else {
            RedisUtil.deleteObject("EMAIL:" + req.getEmail());
        }
        User user = BeanUtil.copyProperties(req, User.class);
        Long count = this.lambdaQuery().eq(null != user.getName(), User::getName, user.getName())
                .eq(null != user.getMobile(), User::getMobile, user.getMobile())
                .count();
        if(count > 0){
            return B.finalBuild("用户已存在");
        }
        user.setPassword("123456");
        user.setCreateTime(LocalDateTime.now());
        user.setOperateTime(LocalDateTime.now());
        user.setRemainingTimes(sysConfig.getDefaultTimes());
        user.setEmail(req.getEmail());
        this.save(user);
        return B.okBuild("密码：123456");
    }

    @Override
    public B<UserInfoRes> home(UserHomeReq req) {
        UserInfoRes userInfo = this.baseMapper.getUserInfo(JwtUtil.getUserId());
        if(userInfo.getType() == -1){
            userInfo.setType(2);
        }
        if(null == userInfo.getExpirationTime() || LocalDateTime.now().compareTo(userInfo.getExpirationTime()) > 0){
            userInfo.setType(0);
        }
        if(null != userInfo.getExpirationTime() &&  LocalDateTime.now().compareTo(userInfo.getExpirationTime()) <= 0){
            userInfo.setType(1);
        }
        List<UserRefuelingKitRes> userRefuelingKitRes = refuelingKitDao.selectUserKit(JwtUtil.getUserId());
        userRefuelingKitRes.forEach( u ->{
            if(u.getExpirationDateTime().compareTo(LocalDateTime.now()) < 0){
                u.setState(1);
            }else {
                u.setState(0);
            }
        });
        userInfo.setKitList(userRefuelingKitRes);
        List<Announcement> list = announcementService.lambdaQuery().select(Announcement::getContent).orderByDesc(Announcement::getSort).last("limit 1").list();
        userInfo.setContent((null != list && list.size() > 0) ? list.get(0).getContent() : "暂无通知公告");
        List<UseLog> useLogList = useLogService.lambdaQuery()
                .eq(UseLog::getUserId, JwtUtil.getUserId())
                .eq(UseLog::getSendType,req.getSendType())
                .orderByDesc(UseLog::getId).last("limit 10").list();
        useLogList.forEach(u -> u.setGptKey(null));
        userInfo.setLogList(useLogList);
        return B.okBuild(userInfo);
    }

    @Override
    public B<AdminHomeRes> adminHome() {
        AdminHomeRes adminHomeRes = this.baseMapper.adminHome();
        List<AdminHomeOrder> adminHomeOrderList = orderDao.queryHomeOrder();
        if(null == adminHomeOrderList || adminHomeOrderList.size() == 0){
            adminHomeOrderList = new ArrayList<>();
        }
        adminHomeRes.setOrderList(adminHomeOrderList);
        List<AdminHomeOrderPrice> adminHomeOrderPrices = orderDao.queryHomeOrderPrice();
        if(null == adminHomeOrderPrices || adminHomeOrderPrices.size() == 0){
            adminHomeOrderPrices = new ArrayList<>();
        }
        adminHomeRes.setOrderPriceList(adminHomeOrderPrices);
        return B.okBuild(adminHomeRes);
    }

    @Override
    public B<UserInfoRes> getType() {
        UserInfoRes userInfo = this.baseMapper.getUserInfo(JwtUtil.getUserId());
        if(userInfo.getType() == -1){
            userInfo.setType(2);
        }
        if(null == userInfo.getExpirationTime() || LocalDateTime.now().isAfter(userInfo.getExpirationTime())){
            userInfo.setType(0);
        }else if(!LocalDateTime.now().isAfter(userInfo.getExpirationTime())){
            userInfo.setType(1);
        }else {
            userInfo.setType(0);
        }
        return B.okBuild(userInfo);
    }

    @Override
    public B<UserInfoRes> getType(Long userId) {
        UserInfoRes userInfo = this.baseMapper.getUserInfo(userId);
        if(userInfo.getType() == -1){
            userInfo.setType(2);
        }
        if(null == userInfo.getExpirationTime() || LocalDateTime.now().isAfter(userInfo.getExpirationTime())){
            userInfo.setType(0);
        }else if(!LocalDateTime.now().isAfter(userInfo.getExpirationTime())){
            userInfo.setType(1);
        }else {
            userInfo.setType(0);
        }
        return B.okBuild(userInfo);
    }
}
