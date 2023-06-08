package com.intelligent.bot.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.sd.SdCreateReq;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.utils.sys.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class MyTask {

    @Resource
    CheckService checkService;
    @Resource
    QueueUtil queueUtil;

    @Resource
    AsyncService asyncService;

    @Resource
    BaiDuService baiDuService;



    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void run() {
        if(queueUtil.getCurrentQueueLength() > 0){
            SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
            List<String> promptList = queueUtil.getCurrentQueue();
            List<String> imgUrlList = new ArrayList<String>();
            List<String> returnImgUrlList = new ArrayList<String>();
            String startTime = DateUtil.getLocalDateTimeNow();
            SdCreateReq req = JSONObject.parseObject(promptList.get(0), SdCreateReq.class);
            Long logId = 0L;
            try {
                logId = checkService.checkUser(MessageLog.builder()
                        .useNumber(CommonConst.SD_NUMBER)
                        .sendType(SendType.SD.getType())
                        .useValue(JSONObject.toJSONString(MessageLogSave.builder()
                                .prompt(req.getPrompt())
                                .type(SendType.SD.getRemark())
                                .startTime(startTime)
                                .imgList(imgUrlList).build()))
                        .userId(req.getUserId()).build(),null);
                log.info("sd请求地址：{}",cacheObject.getSdUrl() + CommonConst.SD_TXT_2_IMG);
                SdCreateReq param = BeanUtil.copyProperties(req, SdCreateReq.class);
                param.setPrompt(baiDuService.translateToEnglish(req.getPrompt()).trim()
                        + (null != req.getLoraList() ? req.getLoraList().stream().map(String::valueOf) : ""));
                String body = HttpUtil.createPost(cacheObject.getSdUrl() + CommonConst.SD_TXT_2_IMG)
                        .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                        .body(StringUtil.toUnderlineCase(param))
                        .execute()
                        .body();
                try {
                    List<String> images = JSONObject.parseArray(JSONObject.parseObject(body).getJSONArray("images").toJSONString(), String.class);
                    images.forEach(i ->{
                        try {
                            String localImgUrl = FileUtil.base64ToImage(i);
                            imgUrlList.add(localImgUrl);
                            returnImgUrlList.add(cacheObject.getImgReturnUrl() + localImgUrl);
                        } catch (IOException e) {
                            //将用户使用次数返回
                            asyncService.updateRemainingTimes(req.getUserId(), CommonConst.SD_NUMBER);
                            queueUtil.remove(promptList.get(0));
                            SseEmitterServer.sendMessage(req.getUserId(),"图片生成失败，请稍后再试");
                        }
                    });
                    MessageLogSave messageLogSave = MessageLogSave.builder()
                            .prompt(req.getPrompt())
                            .type(SendType.SD.getRemark())
                            .startTime(startTime)
                            .imgList(imgUrlList).build();
                    asyncService.updateLog(logId,messageLogSave);
                    MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
                    returnMessage.setImgList(returnImgUrlList);
                    SseEmitterServer.sendMessage(req.getUserId(),returnMessage);
                    log.info("发送sd画图信息：userId：{}，sd画图内容：{}",req.getUserId(),returnMessage);
                    queueUtil.remove(promptList.get(0));
                }catch (Exception e){
                    log.info("队列异常，错误信息：{}",e.getMessage());
                    SseEmitterServer.sendMessage(req.getUserId(),-1);
                    queueUtil.remove(promptList.get(0));
                    asyncService.updateRemainingTimes(req.getUserId(), CommonConst.SD_NUMBER);
                }
            }
            catch (Exception e) {
                log.info("队列异常，错误信息：{}", e.getMessage());
                SseEmitterServer.sendMessage(req.getUserId(), -1);
                queueUtil.remove(promptList.get(0));
                asyncService.updateRemainingTimes(req.getUserId(), CommonConst.SD_NUMBER);
            }
        }
    }
}
