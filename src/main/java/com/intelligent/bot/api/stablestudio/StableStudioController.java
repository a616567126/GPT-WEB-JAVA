package com.intelligent.bot.api.stablestudio;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.stablestudio.TextToImageReq;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.model.res.stablestudio.EngineRes;
import com.intelligent.bot.model.res.stablestudio.TextToImageRes;
import com.intelligent.bot.model.res.stablestudio.UserAccountRes;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.utils.gpt.Proxys;
import com.intelligent.bot.utils.sys.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/stable/studio")
@Log4j2
public class StableStudioController {

    @Resource
    CheckService checkService;
    @Resource
    AsyncService asyncService;

    @PostMapping(value = "/user/account",name = "查询账户信息")
    public B<UserAccountRes> getUserAccount() {
        SysConfig sysConfig = getStableStudioConfig();
        String body = HttpUtil.createGet(sysConfig.getStableStudioApi() + CommonConst.STABLE_STUDIO_USER_ACCOUNT)
                .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
                .execute()
                .body();
        checkError(body);
        return B.okBuild(JSONObject.parseObject(body,UserAccountRes.class));
    }

    @PostMapping(value = "/user/balance",name = "查询账户余额")
    public B<String> getUserBalance() {
        SysConfig sysConfig = getStableStudioConfig();
        String body = HttpUtil.createGet(sysConfig.getStableStudioApi() + CommonConst.STABLE_STUDIO_USER_BALANCE)
                .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
                .execute()
                .body();
        checkError(body);
        return B.okBuild(JSONObject.parseObject(body).getString("credits"));
    }

    @PostMapping(value = "/engines/List",name = "获取引擎列表")
    public B<List<EngineRes>> getEnginesList() {
        SysConfig sysConfig = getStableStudioConfig();
        String body = HttpUtil.createGet(sysConfig.getStableStudioApi() + CommonConst.STABLE_STUDIO_ENGINES_LIST)
                .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
                .execute()
                .body();
        if(!body.contains("description") || !body.contains("type")){
            checkError(body);
        }
        List<EngineRes> engineRes = JSONObject.parseArray(body, EngineRes.class);
        engineRes.removeIf((EngineRes e) -> !e.getType().equals("PICTURE"));
        return B.okBuild(engineRes);
    }

    @PostMapping(value = "/text/to/image",name = "文生图")
    public B<MessageLogSave> textToImage(@Validated @RequestBody TextToImageReq req) {
        SysConfig sysConfig = getStableStudioConfig();
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(CommonConst.STABLE_STUDIO_NUMBER)
                .sendType(SendType.STABLE_STUDIO.getType())
                .useValue(JSONObject.toJSONString(req.getTextPrompts()))
                .userId(JwtUtil.getUserId()).build(),null);
        List<String> imgUrlList = new ArrayList<String>();
        List<String> returnImgUrlList = new ArrayList<String>();
        String startTime = DateUtil.getLocalDateTimeNow();
        Proxy proxy = null ;
        if(null != sysConfig.getIsOpenProxy() && sysConfig.getIsOpenProxy() == 1){
            proxy = Proxys.http(sysConfig.getProxyIp(), sysConfig.getProxyPort());
        }
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        String body = HttpUtil.createPost(sysConfig.getStableStudioApi() + String.format(CommonConst.STABLE_STUDIO_TEXT_TO_IMAGE,req.getEngineId()))
                .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.ACCEPT, ContentType.JSON.getValue())
                .setProxy(proxy)
                .body(StringUtil.toUnderlineCase(req))
                .execute()
                .body();
        if(!body.contains("base64") || !body.contains("finishReason")){
            checkError(body);
            //将用户使用次数返回
            asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.SD_NUMBER);
            throw new E("生成失败");
        }
        List<TextToImageRes> textToImageList = JSONObject.parseArray(JSONObject.parseObject(body).getJSONArray("artifacts").toJSONString(), TextToImageRes.class);
        textToImageList.forEach( t ->{
            if(!t.getFinishReason().equals("ERROR")){
                try {
                    String localImgUrl = FileUtil.base64ToImage(t.getBase64());
                    imgUrlList.add(localImgUrl);
                    returnImgUrlList.add(sysConfig.getImgReturnUrl() + localImgUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        MessageLogSave messageLogSave = MessageLogSave.builder()
                .prompt(JSONObject.toJSONString(req.getTextPrompts()))
                .type(SendType.STABLE_STUDIO.getRemark())
                .startTime(startTime)
                .imgList(imgUrlList).build();
        asyncService.updateLog(logId,messageLogSave);
        MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
        returnMessage.setImgList(returnImgUrlList);
        return B.okBuild(returnMessage);
    }


    public SysConfig getStableStudioConfig(){
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenStableStudio() || cacheObject.getIsOpenStableStudio() == 0){
            throw new E("暂未开启StableStudio");
        }
        return cacheObject;
    }
    public void checkError(String body){
        JSONObject bodyJson = JSONObject.parseObject(body);
        if(body.contains("message")){
            String message = bodyJson.getString("message");
            if(message.equals("missing authorization header")){
                throw new E("api秘钥不正确");
            }else {
                log.error("stableStudio错误：{}",message);
                throw new E("发生了一些意外的服务器错误");
            }
        }
    }
}
