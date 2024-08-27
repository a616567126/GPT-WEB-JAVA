package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.spark.Text;

import java.util.List;


public interface IMessageLogService extends IService<MessageLog> {


    List<Message> createMessageLogList(Long logId,String problem);

    List<Text> createTextLogList(Long logId, String problem);

    /**
     * @param logId 消息id
     * @param data 文件地址 (tts,知识库问答不需要)
     * @param msg 提问内容 图片识别、文件识别为空
     * @param type 提问类型 1.图片识别，2.文件识别，3.知识库、tts。
     * @param fileName 文件名  (tts,知识库问答不需要)
     * @return 消息Id
     */
    Long createMessageId(Long logId,String data,String msg,Integer type,String fileName);


}
