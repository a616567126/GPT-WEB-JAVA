package com.intelligent.bot.api.stablediffusion;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SdLora;
import com.intelligent.bot.model.SdModel;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.sd.SdCreateReq;
import com.intelligent.bot.model.res.sd.GetQueueRes;
import com.intelligent.bot.service.sys.ISdLoraService;
import com.intelligent.bot.service.sys.ISdModelService;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.QueueUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sd")
@Log4j2
@Transactional(rollbackFor = E.class)
public class SdController {

    @Resource
    QueueUtil queueUtil;
    @Resource
    ISdModelService sdModelService;
    @Resource
    ISdLoraService sdLoraService;

    @RequestMapping(value = "/create",name="sd画图", method = RequestMethod.POST)
    public synchronized B<GetQueueRes> createImage(@Validated @RequestBody SdCreateReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        req.setUserId(JwtUtil.getUserId());
        String value = JSONObject.toJSONString(req);
        int position = queueUtil.getPosition(value);
        if(position > -1){
            throw new E("您已发起相同请求，请等待画图完成");
        }
        queueUtil.addUserToQueue(value);
        return getQueue(req);
    }

    @RequestMapping(value = "/getModel",name="获取模型列表", method = RequestMethod.POST)
    public B<List<SdModel>> getModel() {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        List<SdModel> list = sdModelService.lambdaQuery().select(SdModel::getModelName,SdModel::getImgUrl).orderByDesc(SdModel::getId).list();
        list.forEach( l ->{
            l.setImgUrl(cacheObject.getImgReturnUrl() + l.getImgUrl());
        });
        return B.okBuild(list);
    }

    @RequestMapping(value = "/getSamplers",name="获取采样方法列表", method = RequestMethod.POST)
    public B<List<String>> getSamplers() {
        List<String> samplers = new ArrayList<String>();
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        String body = HttpUtil.createGet(cacheObject.getSdUrl() + CommonConst.SD_SAMPLERS).execute().body();
        JSONArray jsonArray = JSONObject.parseArray(body);
        for (int i = 0; i < jsonArray.size(); i++) {
            String title = jsonArray.getJSONObject(i).getString("name");
            samplers.add(title);
        }
        return B.okBuild(samplers);
    }

    @RequestMapping(value = "/getLora",name="获取lora列表", method = RequestMethod.POST)
    public B<List<SdLora>> getLora() {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new E("暂未开启sd");
        }
        List<SdLora> list = sdLoraService.lambdaQuery().select(SdLora::getLoraName,SdLora::getImgUrl).orderByDesc(SdLora::getId).list();
        list.forEach( l ->{
            l.setImgUrl(cacheObject.getImgReturnUrl() + l.getImgUrl());
        });
        return B.okBuild(list);
    }

    @RequestMapping(value = "/getQueue",name="获取队列信息", method = RequestMethod.POST)
    public B<GetQueueRes> getQueue(@Validated @RequestBody SdCreateReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        GetQueueRes res = new GetQueueRes();
        req.setUserId(JwtUtil.getUserId());
        long currentQueueLength = queueUtil.getCurrentQueueLength();
        int position = queueUtil.getPosition(JSONObject.toJSONString(req));
        if(currentQueueLength == 0 || ((position + 1) == 1)) {
            res.setState(2);
            String body = HttpUtil.createGet(cacheObject.getSdUrl() + CommonConst.SD_PROGRESS).execute().body();
            JSONObject bodyJson = JSONObject.parseObject(body);
            res.setImg(bodyJson.getString("current_image"));
            res.setProgress(bodyJson.getDouble("progress"));
        }else {
            res.setState(1);
            res.setQueueSize(currentQueueLength);
            res.setPosition(position);
        }
        return B.okBuild(res);
    }
}
