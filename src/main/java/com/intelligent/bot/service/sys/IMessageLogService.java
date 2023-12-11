package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.spark.Text;

import java.util.List;


public interface IMessageLogService extends IService<MessageLog> {


    List<Message> createMessageLogList(Long logId,String problem);

    List<Text> createTextLogList(Long logId, String problem);


}
