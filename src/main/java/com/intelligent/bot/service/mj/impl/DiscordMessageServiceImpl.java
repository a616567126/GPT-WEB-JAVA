package com.intelligent.bot.service.mj.impl;


import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.intelligent.bot.api.midjourney.support.Task;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.res.mj.GetTaskRes;
import com.intelligent.bot.model.res.mj.MessageRes;
import com.intelligent.bot.service.mj.DiscordMessageService;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.utils.sys.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscordMessageServiceImpl implements DiscordMessageService {

    @Resource
    TaskStoreService taskStoreService;

    @Override
    public GetTaskRes getMjMessages(String taskId) {
        GetTaskRes res = new GetTaskRes();
        Task task = taskStoreService.getTask(taskId);
        if(null == task || task.getStatus() == TaskStatus.FAILURE){
            throw new E("任务已失效");
        }
        res.setStatus(task.getStatus());
        res.setTaskId(taskId);
        if(task.getStatus() == TaskStatus.SUCCESS){
            res.setPercentage("100");
        }else {
            res.setPercentage("0");
            SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
            String body = HttpUtil.createGet(String.format(CommonConst.DISCORD_MESSAGE_URL, sysConfig.getMjChannelId()))
                    .header(Header.AUTHORIZATION, "Bot " + sysConfig.getMjBotToken())
                    .header(Header.USER_AGENT, "DiscordBot (https://yourwebsite.com, 1.0)")
                    .execute()
                    .body();
            List<MessageRes> messageResList = JSONArray.parseArray(body, MessageRes.class);
            for (MessageRes m : messageResList) {
                if(m.getContent().contains(taskId) && null != m.getAttachments() && m.getAttachments().size() > 0){
                    res.setProxyUrl(m.getAttachments().get(0).getProxyUrl());
                    res.setUrl(m.getAttachments().get(0).getUrl());
                    if(m.getContent().contains("%")){
                        res.setPercentage(m.getContent().split("> \\(")[1].split("\\)")[0].split("%")[0]);
                    }
                    break;
                }
            }
        }
        return res;
    }
}
