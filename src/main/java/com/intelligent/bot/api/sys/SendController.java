package com.intelligent.bot.api.sys;

import com.intelligent.bot.annotate.AvoidRepeatRequest;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.req.sys.SendEmailReq;
import com.intelligent.bot.service.sys.IUserService;
import com.intelligent.bot.service.sys.SendMessageService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/send")
@Log4j2
@Transactional(rollbackFor = E.class)
public class SendController {

    @Resource
    IUserService userService;

    @Resource
    SendMessageService sendMessageService;

    @RequestMapping(value = "/email",name="发送邮件验证码")
    @AvoidRepeatRequest(msg = "请求频繁请稍后再试")
    public B<Void> sendMessage(@Validated @RequestBody SendEmailReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(cacheObject.getRegistrationMethod() != 2){
            throw new E("发送邮件功能已关闭");
        }
        Long count = userService.lambdaQuery().eq(User::getEmail, req.getEmail()).or().eq(User::getMobile, req.getMobile()).count();
        if(count > 0){
            throw new E("邮箱或手机号已存在，请更换");
        }
        sendMessageService.sendEmail(req.getEmail());
        return B.okBuild();
    }
}
