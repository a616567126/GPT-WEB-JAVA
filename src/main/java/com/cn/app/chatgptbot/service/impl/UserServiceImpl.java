package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ValidateException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.dao.OrderDao;
import com.cn.app.chatgptbot.dao.RefuelingKitDao;
import com.cn.app.chatgptbot.dao.UserDao;
import com.cn.app.chatgptbot.model.Announcement;
import com.cn.app.chatgptbot.model.RefuelingKit;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.RegisterReq;
import com.cn.app.chatgptbot.model.res.*;
import com.cn.app.chatgptbot.service.IAnnouncementService;
import com.cn.app.chatgptbot.service.IUseLogService;
import com.cn.app.chatgptbot.service.IUserService;
import com.cn.app.chatgptbot.uitls.JwtUtil;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 分页查询User
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    @Override
    public B queryPage(BasePageHelper basePageHelper) {
        JSONObject jsonObject = new JSONObject();
        Page<User> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("id");
        queryWrapper.like(null != basePageHelper.getName(),"name",basePageHelper.getName());
        queryWrapper.like(null != basePageHelper.getMobile(),"mobile",basePageHelper.getMobile());
        Page<User> userPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("userPage",userPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }

    /**
     * 新增User
     *
     * @param params 参数
     * @return 返回对象
     */
    @Override
    public B add(String params) {
        User user = JSONObject.parseObject(params, User.class);
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

    /**
     * 修改User
     *
     * @param params 参数
     * @return 返回对象
     */
    @Override
    public B update(String params) {
        User user = JSONObject.parseObject(params, User.class);
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

    /**
     * 通过id删除User
     *
     * @param params 参数
     * @return 返回对象
     */
    @Override
    public B delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B register(RegisterReq req) {
        User user = BeanUtil.copyProperties(req, User.class);
        Long count = this.lambdaQuery().eq(null != user.getName(), User::getName, user.getName())
                .eq(null != user.getMobile(), User::getMobile, user.getMobile())
                .count();
        if(count > 0){
            return B.finalBuild("用户已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setOperateTime(LocalDateTime.now());
        user.setRemainingTimes(10);
        this.save(user);
        return B.okBuild();
    }

    @Override
    public B<UserInfoRes> home() {
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
        List<UseLog> useLogList = useLogService.lambdaQuery().eq(UseLog::getUserId, JwtUtil.getUserId()).orderByDesc(UseLog::getId).last("limit 10").list();
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
        if(null == userInfo.getExpirationTime() || LocalDateTime.now().compareTo(userInfo.getExpirationTime()) > 0){
            userInfo.setType(0);
        }else if(LocalDateTime.now().compareTo(userInfo.getExpirationTime()) <= 0){
            userInfo.setType(2);
        }else {
            userInfo.setType(0);
        }
        return B.okBuild(userInfo);
    }
}
