package com.intelligent.bot.service.sys;


import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.listener.spark.ChatListener;
import com.intelligent.bot.listener.spark.SparkDeskClient;
import com.intelligent.bot.model.*;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.model.spark.AIChatRequest;
import com.intelligent.bot.model.spark.AIChatResponse;
import com.intelligent.bot.model.spark.Text;
import com.intelligent.bot.model.spark.Usage;
import com.intelligent.bot.utils.sys.DateUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import com.intelligent.bot.utils.sys.SendMessageUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Component
@Log4j2
public class AsyncService {



    @Resource
    IGptKeyService gptKeyService;

    @Resource
    IMessageLogService useLogService;

    @Resource
    IUserService userService;

    @Resource
    @Lazy
    IMjTaskService mjTaskService;

    @Resource
    WxMpService wxMpService;

    protected StringBuffer lastMessage = new StringBuffer();


    @Async
    public void updateKeyNumber(String key){
        gptKeyService.lambdaUpdate()
                .eq(GptKey::getKey, key)
                .setSql("use_number = use_number + 1")
                .update();
    }

    @Async
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public void updateKeyState(String key){
//       gptKeyService.lambdaUpdate().eq(GptKey::getKey,key).set(GptKey::getState,1).update();
//        InitUtil.removeKey(key);
    }

    @Async
    public void updateLog(Long logId, MessageLogSave messageLogSave){
        messageLogSave.setEndTime(DateUtil.getLocalDateTimeNow());
        useLogService.lambdaUpdate().eq(MessageLog::getId,logId)
                .set(MessageLog::getUseValue,JSONObject.toJSONString(messageLogSave))
                .update();
    }

    @Async
    public void updateMjTask(Task task){
        task.setFinishTime(System.currentTimeMillis());
        mjTaskService.updateById(task);
    }

    @Async
    public void endOfAnswer(Long logId, String value){
        MessageLog messageLog = useLogService.getById(logId);
        List<Message> messageList = JSONObject.parseArray(messageLog.getUseValue(), Message.class);
        if(messageLog.getSendType().equals(SendType.BING.getType())){
            messageList.add(Message.ofAssistant(value.replaceAll(CommonConst.EMOJI, " ")));
        }else {
            messageList.add(Message.ofAssistant(value));
        }
        useLogService.lambdaUpdate().eq(MessageLog::getId,logId)
                .set(MessageLog::getUseValue,JSONObject.toJSONString(messageList))
                .update();
    }

    @Async
    public void updateRemainingTimes(Long userId,Integer number){
        userService.lambdaUpdate()
                .eq(User::getId,userId)
                .setSql("remaining_times = remaining_times + "+number)
                .update();
    }

    @Async
    public void deleteImages(List<String> images){
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        images.forEach( i ->{
            String filePatch = sysConfig.getImgUploadUrl() + i;
            log.info("删除文件路径：{}",filePatch);
            FileUtil.del(filePatch);
        });
    }
    @Async
    public void sendMjWxMessage(Task mjTask) throws WxErrorException {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        User user = userService.getById(mjTask.getUserId());
        String content;
        if(mjTask.getAction().equals(TaskAction.UPSCALE)){
            content = "\uD83C\uDFA8绘图完成\n"+
                    "\uD83E\uDD73本次消耗次数："+CommonConst.MJ_NUMBER;
        }else if(mjTask.getAction().equals(TaskAction.DESCRIBE)){
            content = mjTask.getPrompt();
        }else {
            content = "\uD83C\uDFA8绘图完成\n" +
                    "\uD83E\uDD73本次消耗次数："+CommonConst.MJ_NUMBER+"\n"+
                    "\uD83D\uDCAC咒语："+mjTask.getPrompt()+"\n"+
                    "\uD83D\uDDEF译文："+mjTask.getPromptEn()+"\n"+
                    "\uD83D\uDD0D\uFE0E放大命令：\n"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-1-"+mjTask.getId()+"&msgmenuid=1\">放大1</a>\t"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-2-"+mjTask.getId()+"&msgmenuid=1\">放大2</a>\t"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-3-"+mjTask.getId()+"&msgmenuid=1\">放大3</a>\t"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-4-"+mjTask.getId()+"&msgmenuid=1\">放大4</a>\n"+
                    "\uD83D\uDCAB变换命令：\n"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-1-"+mjTask.getId()+"&msgmenuid=1\">变换1</a>\t"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-2-"+mjTask.getId()+"&msgmenuid=1\">变换2</a>\t"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-3-"+mjTask.getId()+"&msgmenuid=1\">变换3</a>\t"+
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-4-"+mjTask.getId()+"&msgmenuid=1\">变换4</a>";
        }
        WxMpKefuMessage message;
        if(!mjTask.getAction().equals(TaskAction.DESCRIBE)){
            File file = new File(sysConfig.getImgUploadUrl() + mjTask.getImageUrl());
            WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
            message = WxMpKefuMessage.IMAGE().toUser(user.getFromUserName()).mediaId(wxMediaUploadResult.getMediaId()).build();
            wxMpService.getKefuService().sendKefuMessage(message);
        }
        message=WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(content).build();
        wxMpService.getKefuService().sendKefuMessage(message);
    }

    @Async
    public void sparkChat(SparkDeskClient sparkDeskClient, AIChatRequest aiChatRequest, Long logId, Long userId){
        sparkDeskClient.chat(new ChatListener(aiChatRequest) {
            @SneakyThrows
            @Override
            public void onChatError(AIChatResponse aiChatResponse) {
                log.warn(String.valueOf(aiChatResponse));
            }

            @Override
            public void onChatOutput(AIChatResponse aiChatResponse) {
                String content = aiChatResponse.getPayload().getChoices().getText().get(0).getContent();
                lastMessage.append(content);
                SendMessageUtil.sendMessage(userId, Text.builder().role(Text.Role.ASSISTANT.getName()).index(0).content(content).build());
                log.info("spark回答中:{}",content);
            }

            @Override
            public void onChatEnd() {
                log.info("spark当前会话结束了");
                endOfAnswer(logId,lastMessage.toString());
                SendMessageUtil.sendMessage(userId,  Text.builder().role(Text.Role.ASSISTANT.getName()).index(0).content("[DONE]").build());
                lastMessage = new StringBuffer();
            }

            @Override
            public void onChatToken(Usage usage) {
                log.info("token 信息：{}",usage);
            }
        });

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
