package com.intelligent.bot.service.sys;


import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.GptKey;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.utils.sys.DateUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Component
public class AsyncService {



    @Resource
    IGptKeyService gptKeyService;

    @Resource
    IMessageLogService useLogService;

    @Resource
    IUserService userService;

    @Resource
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
    public void updateMjTask(MjTask mjTask){
        mjTask.setFinishTime(System.currentTimeMillis());
        mjTaskService.updateById(mjTask);
    }

    @Async
    public void endOfAnswer(Long logId, String value){
        MessageLog messageLog = useLogService.getById(logId);
        List<Message> messageList = JSONObject.parseArray(messageLog.getUseValue(), Message.class);
        messageList.add(Message.ofAssistant(value.replaceAll(CommonConst.EMOJI, " ")));
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

}
