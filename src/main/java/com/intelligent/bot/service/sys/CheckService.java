package com.intelligent.bot.service.sys;


import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("CheckService")
@Transactional(rollbackFor = E.class)
public class CheckService {

    @Resource
    IUserService userService;

    @Resource
    IMessageLogService useLogService;


    public Long checkUser(MessageLog messageLog, Long id) {
        //查询当前用户信息
        User user = userService.getById(messageLog.getUserId());
        messageLog.setUseType(1);
        if(user.getType() != -1){
            //判断剩余次数
            if(user.getRemainingTimes() < messageLog.getUseNumber()){
                throw new E("剩余次数不足请充值");
            }
            user.setRemainingTimes(user.getRemainingTimes() - messageLog.getUseNumber());
        }
        userService.saveOrUpdate(user);
        if(null != id){
            messageLog.setId(id);
            messageLog.setUseNumber(useLogService.getById(id).getUseNumber() + messageLog.getUseNumber());
        }
        useLogService.saveOrUpdate(messageLog);
        return messageLog.getId();
    }

    public void checkUser(Long userId,Integer number) {
        //查询当前用户信息
        User user = userService.getById(userId);
        if(user.getType() != -1){
            //判断剩余次数
            if(user.getRemainingTimes() < number){
                throw new E("剩余次数不足请充值");
            }
            user.setRemainingTimes(user.getRemainingTimes() - number);
        }
        userService.saveOrUpdate(user);
    }

    public boolean checkWxUser(Long userId,Integer number) {
        //查询当前用户信息
        User user = userService.getById(userId);
        if(user.getType() != -1){
            //判断剩余次数
            if(user.getRemainingTimes() < number){
                return false;
            }
            user.setRemainingTimes(user.getRemainingTimes() - number);
        }
        userService.saveOrUpdate(user);
        return true;
    }
}
