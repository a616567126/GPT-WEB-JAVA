package com.cn.app.chatgptbot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.dao.UseLogDao;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.req.ResetLogReq;
import com.cn.app.chatgptbot.model.req.UpdateLogReq;
import com.cn.app.chatgptbot.service.IUseLogService;
import com.cn.app.chatgptbot.service.IUserService;
import com.cn.app.chatgptbot.utils.JwtUtil;
import javax.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户表(Product)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("UseLogService")
@Transactional(rollbackFor = Exception.class)
public class UseLogServiceImpl extends ServiceImpl<UseLogDao, UseLog> implements IUseLogService {

    @Resource
    @Lazy
    IUserService userService;
    @Override
    public Integer getDayUseNumber() {
        return this.baseMapper.getDayUseNumber(JwtUtil.getUserId());
    }

    @Override
    public B updateLog(UpdateLogReq req) {
        UseLog useLog = this.getById(req.getLogId());
        if(null == useLog){
            return B.okBuild();
        }
        useLog.setUseValue(req.getNewMessages());
        this.saveOrUpdate(useLog);
        return B.okBuild();
    }

    @Override
    public B resetLog(ResetLogReq req) {
        UseLog useLog = this.getById(req.getLogId());
        if(null == useLog || useLog.getState() == 1){
            return B.okBuild();
        }
        useLog.setState(1);
        useLog.setUseValue(req.getNewMessages());
        if(useLog.getUseType() == 1){
            User user = this.userService.getById(JwtUtil.getUserId());
            user.setRemainingTimes(user.getRemainingTimes() + 1);
            this.userService.saveOrUpdate(user);
        }
        this.saveOrUpdate(useLog);
        return B.okBuild();
    }


}
