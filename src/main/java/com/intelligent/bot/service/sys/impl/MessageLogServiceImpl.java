package com.intelligent.bot.service.sys.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.MessageLogDao;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.spark.Text;
import com.intelligent.bot.service.sys.IMessageLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service("UseLogService")
@Transactional(rollbackFor = Exception.class)
public class MessageLogServiceImpl extends ServiceImpl<MessageLogDao, MessageLog> implements IMessageLogService {

    @Override
    public List<Message> createMessageLogList(Long logId,String problem) {
        List<Message> messages = new ArrayList<>();
        if(null != logId){
            MessageLog messageLog = this.getById(logId);
            if(null != messageLog){
                messages = JSONObject.parseArray(messageLog.getUseValue(),Message.class);
            }
        }
        messages.add(Message.of(problem));
        return messages;
    }

    @Override
    public List<Text> createTextLogList(Long logId, String problem) {
        List<Text> text = new ArrayList<>();
        if(null != logId){
            MessageLog messageLog = this.getById(logId);
            if(null != messageLog){
                text = JSONObject.parseArray(messageLog.getUseValue(),Text.class);
            }
        }
        text.add(Text.builder().role(Text.Role.USER.getName()).content(problem).build());
        return text;
    }
}
