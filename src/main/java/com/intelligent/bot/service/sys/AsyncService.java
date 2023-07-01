package com.intelligent.bot.service.sys;


import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.*;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.utils.sys.DateUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


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

}
