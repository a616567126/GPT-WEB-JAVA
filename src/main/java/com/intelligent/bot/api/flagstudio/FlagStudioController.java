package com.intelligent.bot.api.flagstudio;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.fs.FlagStudioTextCreateReq;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.utils.sys.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/flag/studio")
@Log4j2
@Transactional(rollbackFor = E.class)
public class FlagStudioController {

    @Resource
    CheckService checkService;
    @Resource
    BaiDuService baiDuService;
    @Resource
    AsyncService asyncService;


    @RequestMapping(value = "/create",name = "生成图片",method = RequestMethod.POST)
    public synchronized B<MessageLogSave> createImage(@Validated @RequestBody FlagStudioTextCreateReq req) throws IOException {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == cacheObject.getIsOpenFlagStudio() || cacheObject.getIsOpenFlagStudio() == 0){
            throw new E("暂未开启FlagStudio");
        }
        if(!baiDuService.textToExamine(req.getPrompt())){
            throw new E("生成内容不合规");
        }
        List<String> imgUrlList = new ArrayList<String>();
        List<String> returnImgUrlList = new ArrayList<String>();
        String startTime = DateUtil.getLocalDateTimeNow();
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(CommonConst.FS_NUMBER)
                .sendType(SendType.FS.getType())
                .useValue(JSONObject.toJSONString(MessageLogSave.builder()
                        .prompt(req.getPrompt())
                        .type(SendType.FS.getRemark())
                        .startTime(startTime)
                        .imgList(imgUrlList).build()))
                .userId(JwtUtil.getUserId()).build(),null);
        String prompt = this.baiDuService.translateToEnglish(req.getPrompt());
        req.setPrompt(prompt);
        String body = HttpUtil.createPost(cacheObject.getFlagStudioUrl()+"/v1/text2img")
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.ACCEPT, ContentType.JSON.getValue())
                .header("token", getToken())
                .body(StringUtil.toUnderlineCase(req))
                .execute()
                .body();
        JSONObject bodyJson = JSONObject.parseObject(body);
        Integer nsfw = bodyJson.getInteger("nsfw");
        if(nsfw == 1){
            //将用户使用次数返回
            asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.FS_NUMBER);
            throw new E("图片违规");
        }else {
            String localImgUrl = FileUtil.base64ToImage(bodyJson.getString("data"));
            imgUrlList.add(localImgUrl);
            returnImgUrlList.add(cacheObject.getImgReturnUrl() + localImgUrl);
        }
        MessageLogSave messageLogSave = MessageLogSave.builder()
                .prompt(req.getPrompt())
                .type(SendType.FS.getRemark())
                .startTime(startTime)
                .imgList(imgUrlList).build();
        asyncService.updateLog(logId,messageLogSave);
        MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
        returnMessage.setImgList(returnImgUrlList);
        return B.okBuild(returnMessage);
    }


    public static String getToken(){
        String token = RedisUtil.getCacheObject(CommonConst.FLAG_STUDIO_TOKEN);
        if(StringUtils.isEmpty(token)){
            SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
            if(null == cacheObject.getIsOpenFlagStudio()
                    || cacheObject.getIsOpenFlagStudio() == 0
                    || null == cacheObject.getFlagStudioKey()){
                throw new E("暂时无法获取Token");
            }
            String body = HttpUtil.createGet(cacheObject.getFlagStudioUrl() + "/auth/getToken")
                    .header(Header.ACCEPT, ContentType.JSON.getValue())
                    .form("apikey",cacheObject.getFlagStudioKey())
                    .execute()
                    .body();
            if(!body.contains("200")){
                throw new E("token获取失败");
            }else {
                token = JSONObject.parseObject(body).getJSONObject("data").getString("token");
                RedisUtil.setCacheObject(CommonConst.FLAG_STUDIO_TOKEN,token,29L, TimeUnit.DAYS);
            }
        }
        return token;
    }

}
