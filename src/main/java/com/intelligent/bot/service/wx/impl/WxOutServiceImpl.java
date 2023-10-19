package com.intelligent.bot.service.wx.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.*;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.mj.TaskService;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.service.sys.*;
import com.intelligent.bot.service.wx.WxOutService;
import com.intelligent.bot.utils.mj.MimeTypeUtils;
import com.intelligent.bot.utils.mj.SnowFlake;
import com.intelligent.bot.utils.sys.*;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(rollbackFor = E.class)
@Log4j2
public class WxOutServiceImpl implements WxOutService {

    @Resource
    IUserService userService;

    @Resource
    ICardPinService cardPinService;

    @Resource
    IOrderService orderService;

    @Resource
    BaiDuService baiDuService;

    @Resource
    CheckService checkService;

    @Resource
    TaskService taskService;

    @Resource
    TaskStoreService taskStoreService;

    @Resource
    AsyncService asyncService;

    @Resource
    IMjTaskService mjTaskService;

    @Resource
    WxMpService wxMpService;

    @Resource
    IWxLogService wxLogService;

    @Override
    public String bind(String content, String fromUser) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String respContent = "";
        String[] split = content.split("-");
        if (split.length == 1) {
            respContent = "❗\uFE0F输入内容格式不正确，请检查，或输入'菜单'查看格式'";
        } else {
            if (!Validator.isMobile(split[1])) {
                respContent = "❗\uFE0F请输入正确的手机号";
            } else {
                User checkuser = userService.getOne(null, split[1]);
                if (null != checkuser) {
                    respContent = "❗\uFE0F当前手机号已被绑定";
                } else {
                    User user = userService.getOne(fromUser, null);
                    if (null != user) {
                        if (null != user.getMobile()) {
                            respContent = "❗\uFE0F当前微信已绑定账号：" + user.getMobile();
                        }
                    } else {
                        user.setMobile(split[1]);
                        user.setIsEvent(1);
                        userService.saveOrUpdate(user);
                        respContent = "✅账号绑定成功!" +
                                "\n账号:" + user.getMobile() + "" +
                                "\ndemo地址：" + sysConfig.getClientUrl();
                    }
                }
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String opened(String content, String fromUser) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String respContent = "";
        String[] split = content.split("-");
        if (split.length == 1) {
            respContent = "❗\uFE0F输入内容格式不正确，请检查，或输入'菜单'查看格式'";
        } else {
            if (!Validator.isMobile(split[1])) {
                respContent = "❗\uFE0F请输入正确的手机号";
            } else {
                User checkUser = userService.getOne(null, split[1]);
                if (null != checkUser) {
                    respContent = "❗\uFE0F当前手机号已被绑定";
                } else {
                    User user = userService.getOne(fromUser, null);
                    if (null != user) {
                        if (null != user.getMobile()) {
                            respContent = "❗\uFE0F账号已开通，账号为：" + user.getMobile();
                        } else {
                            user.setMobile(split[1]);
                            userService.saveOrUpdate(user);
                        }
                    } else {
                        user = new User();
                        String password = PasswordUtil.getRandomPassword();
                        user.setName(split[1]);
                        user.setMobile(split[1]);
                        user.setPassword(SecureUtil.md5(password));
                        user.setFromUserName(fromUser);
                        user.setIsEvent(1);
                        user.setAvatar(CommonConst.AVATAR);
                        user.setRemainingTimes(sysConfig.getDefaultTimes());
                        user.setType(1);
                        userService.save(user);
                        respContent = "✅账号开通成功!" +
                                "\n账号:" + user.getMobile() + "" +
                                "\n密码:" + password +
                                "\ndemo地址：" + sysConfig.getClientUrl();
                    }
                }
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String query(String content, String fromUser) {
        String respContent;
        User user = userService.getOne(fromUser, null);
        if (null == user) {
            respContent = "❗\uFE0F当微信用户前暂未绑定账号";
        } else {
            respContent = "✅剩余次数：" + user.getRemainingTimes();
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String menu(String content, String fromUser) {
        String respContent = "❗❗注意：使用画画等命令时记得开通或绑定现有账号，否则无法使用命令\n\n" +
                "输 入 '查询' 即可查询剩余次数，账号创建时间\n\n" +
                "输 入 '绑定-手机号' 即可与当前微信用户绑定例如(绑定-13344445556)\n\n" +
                "输 入 '开通-手机号' 即可开通账号例如(开通-13344445556)同一个微信号只能开通一个账号默认使用次数5次，默认密码123456\n\n" +
                "输 入 '修改密码-新密码' 即可修改当前用户密码例如(修改密码-123456),长度不能超过16位\n\n" +
                "输 入 '重置密码' 即可重置一个随机密码\n\n" +
                "输 入 '/画画' 即可使用mj画图，例如'/画画 一只小狗'记得有空格,支持垫图，" +
                "需先发送图片或直接使用图片地址进行垫图 例如：/画画 一只小狗 # https://a.img.com，/画画 + 空格 + 咒语 + 空格 + '#' + 空格 +图片地址，多个度图片地址用空格隔开\n\n" +
                "输 入 '兑换-卡密' 即可兑换卡密例如'兑换-ABC1234'";
//                        "输 入 '迁移-手机号' 即可将当前微信用户迁移到手机号所在用户账号例如(迁移-13344444444)，并合并剩余次数，注意迁移账号后，微信关联用户则删除，请谨慎迁移，并核对手机号";
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String exchange(String content, String fromUser) {
        String respContent;
        User user = userService.getOne(fromUser, null);
        if (null == user) {
            respContent = "❗\uFE0F请先开通或绑定用户";
        } else {
            String[] split = content.split("-");
            if (split.length == 1) {
                respContent = "❗\uFE0F输入内容格式不正确，请检查，或输入'菜单'查看格式'";
            } else {
                Order order = new Order();
                List<CardPin> list = cardPinService.lambdaQuery().eq(CardPin::getCardPin, split[1]).list();
                if (null == list || list.size() == 0 || list.get(0).getState() == 1) {
                    respContent = "❗\uFE0F无效卡密";
                } else {
                    order.setMsg("兑换成功");
                    order.setState(1);
                    order.setUserId(user.getId());
                    order.setPayNumber(1);
                    order.setPayType("卡密兑换");
                    order.setTradeNo(split[1]);
                    this.orderService.save(order);
                    userService.lambdaUpdate()
                            .eq(User::getId, user.getId())
                            .setSql("remaining_times = remaining_times +" + list.get(0).getNumber())
                            .update();
                    cardPinService.lambdaUpdate()
                            .eq(CardPin::getCardPin, split[1])
                            .set(CardPin::getState, 1)
                            .set(CardPin::getUserId, user.getId())
                            .update();
                    respContent = "✅兑换成功，此次兑换次数：" + list.get(0).getNumber();
                }
            }

        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String draw(String content, String fromUser) {
        String respContent;
        String prompt = content.indexOf("#") == -1 ? content.substring(4) : content.substring(4, content.indexOf("#"));
        if (CharSequenceUtil.isBlank(prompt) || prompt.contains("nsfw") || !baiDuService.textToExamine(prompt)) {
            respContent = "❗\uFE0F生成内容不合规";
        } else {
            User user = userService.getOne(fromUser, null);
            if (null == user) {
                respContent = "❗\uFE0F请先开通或绑定用户";
            } else {
                try {
                    boolean checkWxUser = checkService.checkWxUser(user.getId(), CommonConst.MJ_NUMBER);
                    if (checkWxUser) {
                        Task task = newTask(user.getId());
                        task.setAction(TaskAction.IMAGINE);
                        task.setPrompt(prompt);
                        String promptEn;
                        int paramStart = prompt.indexOf(" --");
                        if (paramStart > 0) {
                            promptEn = this.baiDuService.translateToEnglish(prompt.substring(0, paramStart)).trim() + prompt.substring(paramStart);
                        } else {
                            promptEn = this.baiDuService.translateToEnglish(prompt).trim();
                        }
                        task.setPromptEn(promptEn);
                        task.setFinalPrompt(promptEn);
                        task.setSubType(2);
                        List<String> imageUrlList = new ArrayList<String>();
                        if (content.contains("#")) {
                            String[] contentValue = content.split("#");
                            if (contentValue.length == 2) {
                                String[] imgUrls = contentValue[1].split(" ");
                                for (String imageUrl : imgUrls) {
                                    if (imageUrl.startsWith("http")) {
                                        imageUrlList.add(imageUrl);
                                    }
                                }
                            }
                        }
                        task.setNonce(SnowFlake.INSTANCE.nextId());
                        taskService.submitImagine(task, imageUrlList);
                        respContent = "✅咒语已提交，请等待出图\n" +
                                "若长时间无返回点击查看\n" +
                                "<a href=\"weixin://bizmsgmenu?msgmenucontent=/select-" + task.getId() + "&msgmenuid=1\">查看</a>\t";
                    } else {
                        respContent = "❗\uFE0F余额不足，请充值";
                    }
                } catch (Exception e) {
                    respContent = "❗\uFE0F命令有误，请重新输入";
                }
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String spellAnalysis(String content, String fromUser) {
        String respContent = "✅咒语解析已提交，等待结果返回";
        String[] contentArray = content.split(" ");
        if (contentArray.length < 2) {
            respContent = "❗\uFE0F命令有误，请重新输入";
        }
        User user = userService.getOne(fromUser, null);
        if (null == user) {
            respContent = "❗\uFE0F请先开通或绑定用户";
        } else {
            boolean checkWxUser = checkService.checkWxUser(user.getId(), CommonConst.MJ_DESCRIBE_NUMBER);
            if (checkWxUser) {
//                try {
                    String base64Str = FileUtil.imageUrlToBase64(contentArray[1]);
                    String imgHead = "data:image/jpeg;base64,";
                    IDataUrlSerializer serializer = new DataUrlSerializer();
                    DataUrl dataUrl;
                    try {
                        dataUrl = serializer.unserialize(imgHead + base64Str);
                    } catch (MalformedURLException e) {
                        return "❗\uFE0F图片地址解析失败，请重新提交";
                    }
                    Task task = newTask(user.getId());
                    task.setAction(TaskAction.DESCRIBE);
                    String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
                    task.setDescription("/describe " + taskFileName);
                    task.setSubType(2);
                    this.taskService.submitDescribe(task, dataUrl);
            } else {
                respContent = "❗\uFE0F余额不足，请充值";
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String paintingChanges(String content, String fromUser) {
        String respContent;
        try {
            User user = userService.getOne(fromUser, null);
            boolean checkWxUser = checkService.checkWxUser(user.getId(), CommonConst.MJ_NUMBER);
            String[] subContent = content.split("-");
            if (checkWxUser) {
                if (subContent.length < 3) {
                    respContent = "❗\uFE0F命令有误请检查";
                } else {
                    Long taskId = Long.valueOf(subContent[2]);
                    Task task = taskStoreService.get(taskId);
                    if (null == task || task.getStatus() != TaskStatus.SUCCESS || !Arrays.asList(TaskAction.IMAGINE, TaskAction.VARIATION).contains(task.getAction())) {
                        respContent = "❗\uFE0F关联任务异常";
                    } else {
                        TaskAction taskAction = content.contains("U") ? TaskAction.UPSCALE : TaskAction.VARIATION;
                        Integer index = Integer.valueOf(subContent[1]);
                        String description = "/up " + taskId;
                        if (TaskAction.REROLL.equals(taskAction)) {
                            description += " R";
                        } else {
                            description += " " + taskAction.name().charAt(0) + index;
                        }
                        TaskCondition condition = new TaskCondition().setDescription(description);
                        Task existTask = this.taskStoreService.findOne(condition);
                        if (null != existTask) {
                            respContent = "❗\uFE0F任务已存在";
                        } else {
                            Task mjTask = newTask(user.getId());
                            mjTask.setAction(taskAction);
                            mjTask.setPrompt(task.getPrompt());
                            mjTask.setPromptEn(task.getPromptEn());
                            mjTask.setFinalPrompt(task.getFinalPrompt());
                            mjTask.setRelatedTaskId(task.getId());
                            mjTask.setProgressMessageId(task.getMessageId());
                            mjTask.setDiscordInstanceId(task.getDiscordInstanceId());
                            mjTask.setDescription(description);
                            mjTask.setIndex(index);
                            mjTask.setSubType(2);
                            respContent = "✅提交成功，请等待出图\n " +
                                    "若长时间无返回点击查看\n" +
                                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/select-" + mjTask.getId() + "&msgmenuid=1\">查看</a>\t";
                            if (TaskAction.UPSCALE.equals(taskAction)) {
                                this.taskService.submitUpscale(mjTask, task.getMessageId(), task.getMessageHash(), index, task.getFlags());
                            } else if (TaskAction.VARIATION.equals(taskAction)) {
                                this.taskService.submitVariation(mjTask, task.getMessageId(), task.getMessageHash(), index, task.getFlags());
                            } else {
                                respContent = "❗\uFE0F不支持的操作";
                            }
                        }
                    }
                }
            } else {
                respContent = "❗\uFE0F余额不足，请充值";
            }
        } catch (Exception e) {
            respContent = "❗\uFE0F命令有误，请重新输入";
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String queryProgress(String content, String fromUser) throws WxErrorException {
        String respContent;
        String[] subContent = content.split("-");
        if (subContent.length < 2) {
            respContent = "❗\uFE0F命令有误请检查";
        } else {
            Long taskId = Long.valueOf(subContent[1]);
            Task task = mjTaskService.getById(taskId);
            if (null == task) {
                respContent = "❗\uFE0F任务异常";
            } else if (null != task.getFailReason()) {
                respContent = "❗\uFE0F任务异常：" + task.getFailReason();
            } else {
                if (task.getAction().equals(TaskAction.UPSCALE) && task.getStatus().equals(TaskStatus.SUCCESS)) {
                    respContent = "\uD83C\uDFA8绘图完成\n" +
                            "\uD83E\uDD73本次消耗次数：" + CommonConst.MJ_NUMBER;
                } else {
                    if (task.getStatus().equals(TaskStatus.SUCCESS)) {
                        asyncService.sendMjWxMessage(task);
                        respContent = "查询中请稍等";
                    } else {
                        respContent = "\uD83C\uDFA8画图中请等待...\n" +
                                "\uD83D\uDD0E当前进度：" + task.getProgress();
                    }
                }
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String migrate(String content, String fromUser) {
        String respContent;
        User user = userService.getOne(fromUser, null);
        if (null == user) {
            respContent = "❗\uFE0F微信暂未开通账号";
        } else {
            if (null != user.getMobile()) {
                respContent = "❗\uFE0F请勿重复迁移";
            } else {
                String[] split = content.split("-");
                if (split.length == 1) {
                    respContent = "❗\uFE0F输入内容格式不正确，请检查";
                } else {
                    if (!Validator.isMobile(split[1])) {
                        respContent = "❗\uFE0F请输入正确的手机号";
                    } else {
                        User mobuleUser = userService.getOne(null, split[1]);
                        if (null == mobuleUser) {
                            respContent = "❗\uFE0F手机号关联账号不存在";
                        } else {
                            mobuleUser.setFromUserName(user.getFromUserName());
                            mobuleUser.setRemainingTimes(mobuleUser.getRemainingTimes() + user.getRemainingTimes());
                            userService.saveOrUpdate(mobuleUser);
                            respContent = "✅账号迁移成功，只合并次数，消息，对话记录以手机号关联账号为准，微信原用户则删除";
                            userService.removeById(user.getId());
                        }
                    }
                }
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String changePassword(String content, String fromUser) {
        String respContent;
        User user = userService.getOne(fromUser, null);
        if (null == user) {
            respContent = "❗\uFE0F请先开通或绑定账号";
        } else {
            String[] split = content.split("-");
            if (split.length == 1) {
                respContent = "❗\uFE0F输入内容格式不正确，请检查，或输入'菜单'查看格式'";
            } else {
                if (split[1].length() > 16) {
                    respContent = "❗\uFE0F请输入小于等于16位密码";
                } else {
                    user.setPassword(SecureUtil.md5(split[1]));
                    respContent = "✅新密码为：" + split[1];
                    userService.saveOrUpdate(user);
                }
            }
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String resetPassword(String content, String fromUser) {
        String respContent;
        User user = userService.getOne(fromUser, null);
        if (null == user) {
            respContent = "❗\uFE0F请先开通或绑定账号";
        } else {
            String password = PasswordUtil.getRandomPassword();
            user.setPassword(SecureUtil.md5(password));
            userService.saveOrUpdate(user);
            respContent = "✅重置密码完成，新密码：" + password;
        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public String function(String content, String fromUser) {
        String respContent = "\uD83D\uDCF1客户端功能：1-GPT3.5/4.0流式上下文对话，2-GPT画图，3-SD画图（联系作者开启功能），4-newBing流式对话，5-MJ画图\n\n" +
                "\uD83D\uDCE4微信公众号：1-扫码登录（暂未开放），2-MJ画图\n\n" +
                "\uD83D\uDC5B支付能力：1-易支付（微信，qq，支付宝），2-卡密支付\n\n" +
                "\uD83E\uDEAB注册能力：1-账号密码，2-邮箱，3-公众号\n\n" +
                "\uD83E\uDDD1\uD83C\uDFFB\u200D\uD83D\uDCBB管理端功能：1-用户管理，2-商品管理，3-订单管理，4-GPTKEY管理，5-卡密管理，6-邮箱管理，7-系统配置，8-操作日志\n\n" +
                "\uD83E\uDD77作者承接App，公众号，小程序，网站，物联网，定制软件，需要可添加作者微信：ssp941003\n\n";
        saveWxLog(content, fromUser);
        return respContent;
    }

    @Override
    public void cardPin(String fromUser) throws WxErrorException {
        String content = "卡密列表\n" +
                "<a href=\"weixin://bizmsgmenu?msgmenucontent=/recharge-" + 1 + "&msgmenuid=2\">5#100点</a>\t" +
                "<a href=\"weixin://bizmsgmenu?msgmenucontent=/recharge-" + 2 + "&msgmenuid=2\">10#220点</a>\t" +
                "<a href=\"weixin://bizmsgmenu?msgmenucontent=/recharge-" + 3 + "&msgmenuid=2\">50#1500点</a>";
        WxMpKefuMessage wxMpKefuMessage = WxMpKefuMessage.TEXT().toUser(fromUser).content(content).build();
        wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);
        saveWxLog("卡密", fromUser);
    }

    @Override
    public String recharge(String content, String fromUser) throws Exception {
        String respContent;
        String[] subContent = content.split("-");
        if (subContent.length < 2) {
            respContent = "❗\uFE0F命令有误请检查";
        } else {
            User user = userService.getOne(fromUser, null);
            if (null == user) {
                respContent = "❗\uFE0F请先开通或绑定账号";
            } else {
                int price = 5;
                int type = Integer.valueOf(subContent[1]);
                if (type == 2) {
                    price = 10;
                }
                if (type == 3) {
                    price = 50;
                }
                Order order = new Order();
                order.setId(IDUtil.getNextId());
                order.setUserId(user.getId());
                order.setPrice(new BigDecimal(price));
                order.setPayNumber(1);
                order.setState(0);
                order.setPayType("wxpay");
                order.setTradeNo(subContent[1]);
                order.setMsg("微信公众号获取卡密");
                String bodyParam = WxPayUtil.buildNativePayJson(order.getPrice(),
                        order.getId().toString(),
                        "获取卡密#" + price);
                HttpUrl httpurl = HttpUrl.parse(CommonConst.WX_NATIVE_URL);
                String sign = WxPayUtil.getToken("POST", httpurl, bodyParam);
                String body = HttpUtil.createPost(CommonConst.WX_NATIVE_URL)
                        .header(Header.AUTHORIZATION, sign)
                        .header(Header.CONTENT_TYPE, "application/json")
                        .body(bodyParam)
                        .execute()
                        .body();
                if (!body.contains("code_url")) {
                    respContent = "❗\uFE0F微信预订单生成失败";
                } else {
                    File file = FileUtil.createQrCode(JSONObject.parseObject(body).getString("code_url"));
                    WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
                    WxMpKefuMessage wxMpKefuMessage = WxMpKefuMessage.IMAGE().toUser(user.getFromUserName()).mediaId(wxMediaUploadResult.getMediaId()).build();
                    wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);
                    file.delete();
                    respContent = "✅订单创建完成，将此二维码发送到其他终端上使用微信扫码进行获取卡密";
                    orderService.saveOrUpdate(order);
                }
            }

        }
        saveWxLog(content, fromUser);
        return respContent;
    }

    private Task newTask(Long userId) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        Task task = new Task();
        task.setId(IDUtil.getNextId());
        task.setSubmitTime(System.currentTimeMillis());
        task.setNotifyHook(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL);
        task.setNonce(SnowFlake.INSTANCE.nextId());
        task.setUserId(userId);
        return task;
    }

    public void saveWxLog(String content, String fromUser) {
        WxLog wxLog = new WxLog();
        wxLog.setContent(content);
        wxLog.setFromUserName(fromUser);
        wxLog.setCreateTime(LocalDateTime.now());
        wxLogService.save(wxLog);
    }
}
