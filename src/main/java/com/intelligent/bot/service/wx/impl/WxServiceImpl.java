package com.intelligent.bot.service.wx.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.intelligent.bot.api.sys.AuthController;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.res.sys.UserAuthRes;
import com.intelligent.bot.service.sys.IUserService;
import com.intelligent.bot.service.wx.WxOutService;
import com.intelligent.bot.service.wx.WxService;
import com.intelligent.bot.utils.sys.PasswordUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import com.intelligent.bot.utils.sys.SendMessageUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Service("WxService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class WxServiceImpl implements WxService {

    @Resource
    IUserService userService;
    @Resource
    WxOutService wxOutService;


    @Override
    @Transactional(rollbackFor = E.class)
    public String callbackEvent(HttpServletRequest request) throws Exception {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String url = sysConfig.getClientUrl();
        // 默认返回的文本消息内容
        String respContent = "\uD83E\uDD17欢迎关注Ai图灵程序员\n" +
                "\uD83D\uDD17项目开源地址：\n" +
                "https://github.com/a616567126/GPT-WEB-JAVA\n" +
                "\uD83D\uDD0Fdemo演示地址：\n" +
                url + "\n" +
                "输 入 '开通-你的真实手机号' 即可开通账号，例如（开通-13333333333）\n" +
                "输 入 '功能' 即可查询本系统全部功能\n" +
                "输 入 '加群' 即可扫码添加作者微信,备注github\n" +
                "输 入 '卡密' 获取卡密兑换方式\n" +
                "输 入 '菜单' 即可查询公众号菜单功能(开通、绑定、画图、卡密兑换、重置密码、修改密码、查询余额)";
        // 调用parseXml方法解析请求消息
        WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());
        // 发送方帐号
        String fromUser = message.getFromUser();
        // 开发者微信号
        String touser = message.getToUser();
        // 消息类型
        String msgType = message.getMsgType();
        Long tempUserId = StringUtils.isNoneBlank(message.getEventKey()) ?
                message.getEventKey().contains("qrscene")
                        ? Long.valueOf(message.getEventKey().split("_")[1])
                        : Long.valueOf(message.getEventKey())
                : 0L;
        // 回复文本消息
        if (msgType.equals(CommonConst.REQ_MESSAGE_TYPE_EVENT)) {
            // 事件类型
            log.info("关注事件");
            User user = userService.getOne(fromUser, null);
            if (null != user) {
                if(tempUserId > 0){
                    user.setIsEvent(1);
                    wxUserLogin(user, request, tempUserId);
                    respContent = "✅扫码成功，正在登录...\n\n";
                }else {
                    if(tempUserId == 0 && user.getIsEvent() == 1){
                        user.setIsEvent(0);
                    }
                }
                userService.saveOrUpdate(user);
            }else {
                if (tempUserId > 0) {
                    user = new User();
                    String password = PasswordUtil.getRandomPassword();
                    user.setName("用户" + fromUser);
                    user.setPassword(SecureUtil.md5(password));
                    user.setAvatar(CommonConst.AVATAR);
                    user.setFromUserName(fromUser);
                    user.setIsEvent(1);
                    user.setRemainingTimes(sysConfig.getDefaultTimes());
                    user.setType(1);
                    userService.save(user);
                    wxUserLogin(user, request, tempUserId);
                    respContent = "✅开通成功，正在登录...\n\n";
                }
            }

        } else if (!msgType.equals(CommonConst.REQ_MESSAGE_TYPE_TEXT)) {
            if (msgType.equals(CommonConst.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent =
                        "图片地址：\n\n"
                                + message.getPicUrl() + "\n\n"
                                + "若想使用此图片垫图画画 使用以下命令：\n\n"
                                + "/画画 一只猫 # " + message.getPicUrl() + "\n\n"
                                + "若想使用此图片解析咒语 使用以下命令：\n\n"
                                + "/咒语解析 " + message.getPicUrl() + "\n\n"
                                + "注意空格及顺序 /咒语解析 + 空格 图片地址";
            } else {
                respContent = "❗\uFE0F暂不支持该消息类型";
                log.info("其他消息");
            }
        } else {
            log.info("文本消息");
            String content = message.getContent();
            if (content.contains("绑定-")) {
                respContent = wxOutService.bind(content,fromUser);
            }
            if (content.contains("开通")) {
                respContent = wxOutService.opened(content,fromUser);
            }
            if (content.equals("查询")) {
                respContent = wxOutService.query(content,fromUser);
            }
            if (content.equals("菜单")) {
                respContent = wxOutService.menu(content,fromUser);
            }
            if (content.equals("加群")) {
                WxMpXmlOutMessage texts = WxMpXmlOutTextMessage
                        .IMAGE()
                        .toUser(fromUser)
                        .fromUser(touser)
                        .mediaId(CommonConst.MEDIA_ID)
                        .build();
                return texts.toXml();
            }
            if (content.startsWith("兑换")) {
                respContent = wxOutService.exchange(content,fromUser);
            }
            if (content.startsWith("/画画")) {
                respContent = wxOutService.draw(content,fromUser);
            }
            if (content.startsWith("/U") || content.startsWith("/V")) {
                respContent = wxOutService.paintingChanges(content,fromUser);
            }
            if (content.startsWith("/咒语解析")) {
                respContent = wxOutService.spellAnalysis(content,fromUser);
            }
            if (content.startsWith("/select")) {
                respContent = wxOutService.queryProgress(content,fromUser);
            }
//          if (content.contains("迁移")) {
//            respContent = wxOutService.migrate(content,fromUser);
//           }
            if (content.contains("修改密码")) {
                respContent = wxOutService.changePassword(content,fromUser);
            }
            if (content.equals("重置密码")) {
                respContent = wxOutService.resetPassword(content,fromUser);
            }
            if (content.equals("功能")) {
                respContent = wxOutService.function(content,fromUser);
            }
            if (content.equals("卡密")) {
                wxOutService.cardPin(fromUser);
                return null;
            }
            if (content.startsWith("/recharge")) {
                respContent = wxOutService.recharge(content,fromUser);
            }
        }
        WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                .TEXT()
                .toUser(fromUser)
                .fromUser(touser)
                .content(respContent)
                .build();
        return texts.toXml();
    }

    public void wxUserLogin(User user, HttpServletRequest request, Long tempUserId) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        nweUser.setIpAddress(ServletUtil.getClientIP(request));
        userService.updateById(nweUser);
        if (!user.getAvatar().contains("http")) {
            user.setAvatar(sysConfig.getImgReturnUrl() + user.getAvatar());
        }
        UserAuthRes loginResult = AuthController.createLoginResult(user);
        SendMessageUtil.sendMessage(tempUserId, loginResult);
        RedisUtil.deleteObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + tempUserId);
    }

}
