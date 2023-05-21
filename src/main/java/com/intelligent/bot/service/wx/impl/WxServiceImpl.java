package com.intelligent.bot.service.wx.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.WxLog;
import com.intelligent.bot.service.sys.IUserService;
import com.intelligent.bot.service.sys.IWxLogService;
import com.intelligent.bot.service.wx.WxService;
import com.intelligent.bot.utils.sys.PasswordUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
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
    IWxLogService wxLogService;

    @Override
    public String callbackEvent(HttpServletRequest request) throws Exception {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String url = sysConfig.getClientUrl();
        // 默认返回的文本消息内容
        String respContent = "欢迎关注图灵程序员\n" +
                "项目开源地址：\n" +
                "https://github.com/a616567126/GPT-WEB-JAVA\n\n" +
                "demo演示地址：\n" +
                url + "\n" +
                "方便的时候可以给作者点个star\n\n" +
                "程序功能：1-GPT上下文对话（前端动画仿流式），2-GPT流式对话（socket），3-GPT画图，4-SD画图（联系作者开启功能），5-newBing流式对话（socket）\n\n" +
                "支付能力：1-易支付（微信，qq，支付宝），2-支付宝扫码支付，3-微信扫码支付\n\n" +
                "newBing、后台管理页面vip群免费领取，持续更新，入群方式联系作者\n\n" +
                "作者承接App，公众号，小程序，网站，物联网，定制软件，需要可添加作者微信：ssp941003\n\n" +
                "****************菜单*************\n\n" +
                "输 入 '查询' 即可查询月卡到期日期剩余次数，账号创建时间\n\n" +
                "输 入 '绑定-手机号' 即可与当前微信用户绑定例如(绑定-13344445556)\n\n" +
                "输 入 '开通-手机号' 即可开通账号例如(开通-13344445556)同一个微信号只能开通一个账号默认使用次数5次，默认密码123456\n\n" +
                "输 入 '加群' 即可扫码添加作者微信,备注github\n\n" +
                "输 入 '修改密码-新密码' 即可修改当前用户密码例如(修改密码-123456),长度不能超过16位\n\n" +
                "输 入 '重置密码' 即可重置一个随机密码\n\n" +
                "输 入 '菜单' 进入菜单模式";
        // 调用parseXml方法解析请求消息
        WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());
        // 发送方帐号
        String fromUser = message.getFromUser();
        // 开发者微信号
        String touser = message.getToUser();
        // 消息类型
        String msgType = message.getMsgType();
        // 回复文本消息
        if (msgType.equals(CommonConst.REQ_MESSAGE_TYPE_EVENT)) {
            // 事件类型
            log.info("关注事件");
            QueryWrapper<User> query = new QueryWrapper<>();
            query.eq("from_user_name", fromUser);
            User user = userService.getOne(query);
            if (null != user) {
                if (user.getIsEvent() == 1) {
                    user.setIsEvent(0);
                } else {
                    user.setIsEvent(1);
                }
                userService.saveOrUpdate(user);
            }
        } else if (!msgType.equals(CommonConst.REQ_MESSAGE_TYPE_TEXT)) {
            respContent = "暂不支持该消息类型";
            log.info("其他消息");
        } else {
            log.info("文本消息");
            String content = message.getContent();
            WxLog wxLog = new WxLog();
            wxLog.setContent(content);
            wxLog.setFromUserName(fromUser);
            wxLog.setCreateTime(LocalDateTime.now());
            wxLogService.save(wxLog);
            if (content.contains("绑定-")) {
                String[] split = content.split("-");
                if (split.length == 1) {
                    respContent = "输入内容格式不正确，请检查，或输入'菜单'查看格式'";
                } else {
                    if (!Validator.isMobile(split[1])) {
                        respContent = "请输入正确的手机号";
                    } else {
                        QueryWrapper<User> query = new QueryWrapper<>();
                        query.eq("from_user_name", fromUser);
                        User user = userService.getOne(query);
                        if (null != user) {
                            respContent = "当前微信已绑定账号：" + user.getMobile();
                        } else {
                            query = new QueryWrapper<>();
                            query.eq("mobile", split[1]);
                            user = userService.getOne(query);
                            if (null == user) {
                                respContent = "当前手机未注册，请输入'开通' 开通账号";
                            } else if (null != user.getFromUserName()) {
                                respContent = "当前手机号已绑定微信";
                            } else {
                                user.setFromUserName(fromUser);
                                user.setIsEvent(1);
                                userService.saveOrUpdate(user);
                                respContent = "账号绑定成功!" +
                                        "\n账号:" + user.getMobile() + "" +
                                        "\n密码:" + user.getPassword() +
                                        "\ndemo地址：" + url;
                            }
                        }
                    }
                }
            }
            if (content.contains("开通")) {
                String[] split = content.split("-");
                if (split.length == 1) {
                    respContent = "输入内容格式不正确，请检查，或输入'菜单'查看格式'";
                } else {
                    if (!Validator.isMobile(split[1])) {
                        respContent = "请输入正确的手机号";
                    } else {
                        QueryWrapper<User> query = new QueryWrapper<>();
                        query.eq("from_user_name", fromUser);
                        User user = userService.getOne(query);
                        if (null != user) {
                            respContent = "当前微信已绑定账号账号：" + user.getMobile();
                        } else {
                            query = new QueryWrapper<User>();
                            query.eq("mobile", split[1]);
                            user = userService.getOne(query);
                            if (null != user) {
                                respContent = "当前手机号已被绑定";
                            } else {
                                user = new User();
                                String password =  PasswordUtil.getRandomPassword();
                                user.setName(split[1]);
                                user.setMobile(split[1]);
                                user.setPassword(SecureUtil.md5(password));
                                user.setFromUserName(fromUser);
                                user.setIsEvent(1);
                                user.setRemainingTimes(sysConfig.getDefaultTimes());
                                userService.save(user);
                                respContent = "账号开通成功!" +
                                        "\n账号:" + user.getMobile() + "" +
                                        "\n密码:" +password+
                                        "\ndemo地址：" + url;
                            }
                        }
                    }
                }
            } else if (content.equals("查询")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUser);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "当微信用户前暂未绑定账号";
                } else {
                    respContent = "剩余次数：" + user.getRemainingTimes();
                }
            } else if (content.equals("菜单")) {
                respContent = "****************菜单**********************\n\n" +
                        "输 入 '查询' 即可查询月卡到期日期剩余次数，账号创建时间\n\n" +
                        "输 入 '绑定-手机号' 即可与当前微信用户绑定例如(绑定-13344445556)\n\n" +
                        "输 入 '开通-手机号' 即可开通账号例如(开通-13344445556)同一个微信号只能开通一个账号默认使用次数5次，默认密码123456\n\n" +
                        "输 入 '加群' 即可扫码添加作者微信,备注github\n\n" +
                        "输 入 '修改密码-新密码' 即可修改当前用户密码例如(修改密码-123456),长度不能超过16位\n\n" +
                        "输 入 '重置密码' 即可重置一个随机密码\n\n" +
                        "输 入 '菜单' 进入菜单模式";
            } else if (content.equals("加群")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUser);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "请先开通或绑定账号";
                } else {
                    WxMpXmlOutMessage texts = WxMpXmlOutTextMessage
                            .IMAGE()
                            .toUser(fromUser)
                            .fromUser(touser)
                            .mediaId(CommonConst.MEDIA_ID)
                            .build();
                    return texts.toXml();
                }
            }else if (content.contains("修改密码")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUser);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "请先开通或绑定账号";
                } else {
                    String[] split = content.split("-");
                    if (split.length == 1) {
                        respContent = "输入内容格式不正确，请检查，或输入'菜单'查看格式'";
                    } else {
                        if(split[1].length() > 16){
                            respContent = "请输入小于等于16位密码";
                        }else {
                            user.setPassword(SecureUtil.md5(split[1]));
                            respContent = "新密码为："+split[1];
                            userService.saveOrUpdate(user);
                        }
                    }
                }
            }else if (content.equals("重置密码")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUser);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "请先开通或绑定账号";
                } else {
                    String password =  PasswordUtil.getRandomPassword();
                    user.setPassword(SecureUtil.md5(password));
                    respContent = "重置密码完成，新密码："+password;
                    userService.saveOrUpdate(user);
                }
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
}
