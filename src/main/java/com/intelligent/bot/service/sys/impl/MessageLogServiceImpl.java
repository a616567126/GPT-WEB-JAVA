package com.intelligent.bot.service.sys.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.MessageLogDao;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.spark.Text;
import com.intelligent.bot.service.sys.IMessageLogService;
import com.intelligent.bot.utils.sys.JwtUtil;
import lombok.NonNull;
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

    @Override
    public Long createMessageId(Long logId,String data,String msg,Integer type,String fileName) {
        List<Message> messageList= new ArrayList<>();
        JSONObject jsonObject = getJsonObject(data, msg, type,fileName);
        Message message = Message.of(JSONObject.toJSONString(jsonObject));
        messageList.add(message);
        if(null == logId) {
            MessageLog messageLog = new MessageLog();
            messageLog.setUserId(JwtUtil.getUserId());
            messageLog.setUseValue(JSONObject.toJSONString(messageList));
            this.saveOrUpdate(messageLog);
            return messageLog.getId();
        }else {
            MessageLog messageLog = this.getById(logId);
            List<Message> logMessage = JSONObject.parseArray(messageLog.getUseValue(), Message.class);
            logMessage.add(message);
            messageLog.setUseValue(JSONObject.toJSONString(logMessage));
            this.saveOrUpdate(messageLog);
        }
        return logId;
    }

    private static @NonNull JSONObject getJsonObject(String data, String msg, Integer type, String fileName) {
        JSONObject jsonObject = new JSONObject();
        if(type == 1){
            jsonObject.put("text","请帮我描述这个图片的内容，【重要】1.请不要用markdown语法输出 2.请仔细阅读之后把你描述的内容分析给我！");
            jsonObject.put("type","img");
            jsonObject.put("imgUrl", data);
            jsonObject.put("fileName", fileName);
        }
        if(type == 2){
            jsonObject.put("text","请帮我描述这个文件的内容，【重要】1.请不要用markdown语法输出 2.请仔细阅读之后把你描述的内容分析给我！");
            jsonObject.put("type","file");
            jsonObject.put("fileUrl", data);
            jsonObject.put("fileName", fileName);
        }
        if(type == 3){
            jsonObject.put("text", msg);
            jsonObject.put("type","message");
            jsonObject.put("conversationId",data);
        }
        return jsonObject;
    }
}
