package com.intelligent.bot.api.sd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.sd.SdCreateReq;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.utils.sys.DateUtil;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sd")
@Log4j2
@Transactional(rollbackFor = E.class)
public class SdController {


    @Resource
    CheckService checkService;
    @Resource
    AsyncService asyncService;

    @RequestMapping(value = "/create",name="sd画图", method = RequestMethod.POST)
    public synchronized B<MessageLogSave> createImage(@Validated @RequestBody SdCreateReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        List<String> imgUrlList = new ArrayList<String>();
        List<String> returnImgUrlList = new ArrayList<String>();
        String startTime = DateUtil.getLocalDateTimeNow();
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(CommonConst.SD_NUMBER)
                .sendType(SendType.SD.getType())
                .useValue(JSONObject.toJSONString(MessageLogSave.builder()
                        .prompt(req.getPrompt())
                        .type(SendType.SD.getRemark())
                        .startTime(startTime)
                        .imgList(imgUrlList).build()))
                .userId(JwtUtil.getUserId()).build());
        JSONObject params = new JSONObject();
        params.put("prompt",req.getPrompt());
        params.put("negative_prompt",req.getNegativePrompt());
        params.put("width",req.getWidth());
        params.put("height",req.getHeight());
        params.put("steps",req.getSteps());
        params.put("batch_size",req.getBatchSize());
        params.put("cfg_scale",req.getCfgScale());
        params.put("seed",req.getSeed());
        params.put("sampler_index",req.getSamplerIndex());
        params.put("restoreFaces",req.getRestoreFaces());
        JSONObject override_settings = new JSONObject();
        override_settings.put("sd_model_checkpoint",req.getSdModelCheckpoint());
        params.put("override_settings",override_settings);
        log.info("sd请求地址：{}",cacheObject.getSdUrl()+"/sdapi/v1/txt2img");
        String body = HttpUtil.createPost(cacheObject.getSdUrl()+"/sdapi/v1/txt2img")
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(JSONObject.toJSONString(params))
                .execute()
                .body();
        List<String> images = JSONObject.parseArray(JSONObject.parseObject(body).getJSONArray("images").toJSONString(), String.class);
        images.forEach(i ->{
            try {
                imgUrlList.add(FileUtil.base64ToImage(i));
                returnImgUrlList.add(cacheObject.getImgReturnUrl() + FileUtil.base64ToImage(i));
            } catch (IOException e) {
                //将用户使用次数返回
                asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.SD_NUMBER);
                throw new E(e.getMessage());
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
        return B.okBuild(returnMessage);
    }

    @RequestMapping(value = "/getModel",name="获取模型列表", method = RequestMethod.POST)
    public B<List<String>> getModel() {
        List<String> modelList = new ArrayList<String>();
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        String body = HttpUtil.createGet(cacheObject.getSdUrl()+"/sdapi/v1/sd-models").execute().body();
        JSONArray jsonArray = JSONObject.parseArray(body);
        for (int i = 0; i < jsonArray.size(); i++) {
            String title = jsonArray.getJSONObject(i).getString("title");
            modelList.add(title);
        }
        return B.okBuild(modelList);
    }

    @RequestMapping(value = "/getSamplers",name="获取采样方法列表", method = RequestMethod.POST)
    public B<List<String>> getSamplers() {
        List<String> samplers = new ArrayList<String>();
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        String body = HttpUtil.createGet(cacheObject.getSdUrl()+"/sdapi/v1/samplers").execute().body();
        JSONArray jsonArray = JSONObject.parseArray(body);
        for (int i = 0; i < jsonArray.size(); i++) {
            String title = jsonArray.getJSONObject(i).getString("name");
            samplers.add(title);
        }
        return B.okBuild(samplers);
    }

    @RequestMapping(value = "/getLora",name="获取lora列表", method = RequestMethod.POST)
    public B<List<String>> getLora() {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        List<String> loraList = FileUtil.readFiles(cacheObject.getSdLoraUrl());
        return B.okBuild(loraList);
    }

}
