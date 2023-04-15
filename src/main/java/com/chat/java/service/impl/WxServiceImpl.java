package com.chat.java.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.java.model.PayConfig;
import com.chat.java.model.User;
import com.chat.java.model.WxLog;
import com.chat.java.model.wx.Image;
import com.chat.java.model.wx.ImageMessage;
import com.chat.java.service.IWxLogService;
import com.chat.java.service.WxService;
import com.chat.java.utils.RedisUtil;
import com.chat.java.model.wx.TextMessage;
import com.chat.java.service.IUserService;
import com.chat.java.utils.DateUtil;
import com.chat.java.utils.WechatMessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;


@Service("WxService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class WxServiceImpl implements WxService {

    @Resource
    IUserService userService;
    @Resource
    IWxLogService wxLogService;

    private static final String upperStr="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowerStr="abcdefghijklmnopqrstuvwxyz";
    private static final String numStr="1234567890";



    @Override
    public String callbackEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        String url = payConfig.getReturnUrl().split("#")[0];
        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent = "欢迎关注图灵程序员\n" +
                "项目开源地址：\n" +
                "https://github.com/a616567126/GPT-WEB-JAVA\n\n" +
                "demo演示地址：\n" +
                url + "\n" +
                "方便的时候可以给作者点个star\n\n" +
                "如果有开发需求或者问题咨询可留下联系方式作者会及时回复\n\n" +
                "****************菜单*************\n\n" +
                "输 入 '查询' 即可查询月卡到期日期剩余次数，账号创建时间\n\n" +
                "输 入 '绑定-手机号' 即可与当前微信用户绑定例如(绑定-13344445556)\n\n" +
                "输 入 '开通-手机号' 即可开通账号例如(开通-13344445556)同一个微信号只能开通一个账号默认使用次数5次，默认密码123456\n\n" +
                "输 入 '加群' 即可扫码进入群聊,若无法扫码加群请加微信：ssp941003\n\n" +
                "输 入 '查看密码' 即可查看当前绑定账号密码\n\n" +
                "输 入 '修改密码-新密码' 即可修改当前用户密码例如(修改密码-123456),长度不能超过16位\n\n" +
                "输 入 '重置密码' 即可重置一个随机密码\n\n" +
                "输 入 '菜单' 进入菜单模式";
        // 调用parseXml方法解析请求消息
        Map<String, String> requestMap = WechatMessageUtil.parseXml(request);
        // 发送方帐号
        String fromUserName = requestMap.get("FromUserName");
        // 开发者微信号
        String toUserName = requestMap.get("ToUserName");
        // 消息类型
        String msgType = requestMap.get("MsgType");
        // 回复文本消息
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(fromUserName);
        textMessage.setFromUserName(toUserName);
        textMessage.setCreateTime(DateUtil.getCurrentDateTime().getTime());
        textMessage.setMsgType(WechatMessageUtil.RESP_MESSAGE_TYPE_TEXT);
        if (msgType.equals(WechatMessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
            // 事件类型
            log.info("关注事件");
            QueryWrapper<User> query = new QueryWrapper<>();
            query.eq("from_user_name", fromUserName);
            User user = userService.getOne(query);
            if (null != user) {
                if (user.getIsEvent() == 1) {
                    user.setIsEvent(0);
                } else {
                    user.setIsEvent(1);
                }
                userService.saveOrUpdate(user);
            }
        } else if (!msgType.equals(WechatMessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
            respContent = "暂不支持该消息类型";
            log.info("其他消息");
        } else {
            log.info("文本消息");
            String content = requestMap.get("Content");
            WxLog wxLog = new WxLog();
            wxLog.setContent(content);
            wxLog.setFromUserName(fromUserName);
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
                        query.eq("from_user_name", fromUserName);
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
                                user.setFromUserName(fromUserName);
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
                        query.eq("from_user_name", fromUserName);
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
                                String password =  RandomUtil.randomString(upperStr + lowerStr + numStr,RandomUtil.randomInt(6, 10));
                                user.setName(split[1]);
                                user.setMobile(split[1]);
                                user.setPassword(password);
                                user.setFromUserName(fromUserName);
                                user.setIsEvent(1);
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
                query.eq("from_user_name", fromUserName);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "当微信用户前暂未绑定账号";
                } else {
                    respContent = "月卡过期时间：" + (null == user.getExpirationTime() ? "暂未开通月卡" : user.getExpirationTime())
                            + "\n剩余次数：" + user.getRemainingTimes();
                }
            } else if (content.equals("菜单")) {
                respContent = "****************菜单**********************\n\n" +
                        "输 入 '查询' 即可查询月卡到期日期剩余次数，账号创建时间\n\n" +
                        "输 入 '绑定-手机号' 即可与当前微信用户绑定例如(绑定-13344445556)\n\n" +
                        "输 入 '开通-手机号' 即可开通账号例如(开通-13344445556)同一个微信号只能开通一个账号默认使用次数5次，默认密码123456\n\n" +
                        "输 入 '加群' 即可扫码进入群聊,若无法扫码加群请加微信：ssp941003\n\n" +
                        "输 入 '查看密码' 即可查看当前绑定账号密码\n\n" +
                        "输 入 '修改密码-新密码' 即可修改当前用户密码例如(修改密码-123456),长度不能超过16位\n\n" +
                        "输 入 '重置密码' 即可重置一个随机密码\n\n" +
                        "输 入 '菜单' 进入菜单模式";
            } else if (content.equals("加群")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUserName);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "请先开通或绑定账号";
                } else {
                    ImageMessage imageMessage = new ImageMessage();
                    imageMessage.setToUserName(fromUserName);
                    imageMessage.setFromUserName(toUserName);
                    imageMessage.setCreateTime(DateUtil.getCurrentDateTime().getTime());
                    imageMessage.setMsgType(WechatMessageUtil.RESP_MESSAGE_TYPE_IMAGE);
                    Image image = new Image();
                    String mediaId = RedisUtil.getCacheObject("MediaId");
                    image.setMediaId(mediaId);
                    imageMessage.setImage(image);
                    respXml = WechatMessageUtil.messageToXml(imageMessage);
                    return respXml;
                }
            }else if (content.equals("查看密码")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUserName);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "请先开通或绑定账号";
                } else {
                    respContent = "当前绑定账户密码为："+user.getPassword();
                }
            }else if (content.contains("修改密码")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUserName);
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
                            user.setPassword(split[1]);
                            respContent = "新密码为："+split[1];
                            userService.saveOrUpdate(user);
                        }
                    }
                }
            }else if (content.equals("重置密码")) {
                QueryWrapper<User> query = new QueryWrapper<>();
                query.eq("from_user_name", fromUserName);
                User user = userService.getOne(query);
                if (null == user) {
                    respContent = "请先开通或绑定账号";
                } else {
                    String password =  RandomUtil.randomString(upperStr + lowerStr + numStr,RandomUtil.randomInt(6, 10));
                    user.setPassword(password);
                    respContent = "重置密码完成，新密码："+password;
                    userService.saveOrUpdate(user);
                }
            }


        }
        // 设置文本消息的内容
        textMessage.setContent(respContent);
        // 将文本消息对象转换成xml
        respXml = WechatMessageUtil.messageToXml(textMessage);
        return respXml;
    }
}
