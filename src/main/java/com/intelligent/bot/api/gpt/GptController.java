package com.intelligent.bot.api.gpt;

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
import com.intelligent.bot.listener.gpt.ConsoleStreamListener;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.gpt.ImageMessage;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.req.gpt.GptAlphaReq;
import com.intelligent.bot.model.req.gpt.GptStreamReq;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.gpt.ChatGPTStream;
import com.intelligent.bot.service.sys.*;
import com.intelligent.bot.utils.gpt.Proxys;
import com.intelligent.bot.utils.sys.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/gpt")

@Log4j2
public final class GptController {
    @Resource
    CheckService checkService;

    @Resource
    AsyncService asyncService;

    @Resource
    IUserService userService;

    @Resource
    IMessageLogService messageLogService;

    @Resource
    BaiDuService baiDuService;

    @Resource
    IGptKeyService gptKeyService;



    @PostMapping(value = "/chat",name = "流式对话")
    public B<Long> gptChat(@Validated @RequestBody GptStreamReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(cacheObject.getIsOpenGpt() == 0){
            throw new E("Ai对话已关闭");
        }
        if((cacheObject.getIsOpenGpt() == 1 && req.getType() != 3)
                || (cacheObject.getIsOpenGpt() == 2 && req.getType() < 4)
                || (cacheObject.getIsOpenGptVision() == 0 && req.getType() > 4)){
            throw new E("Ai模型已关闭");
        }
        if(StringUtils.isEmpty(req.getProblem())){
            throw new E("请输入有效的内容");
        }
        if(req.getType() < 5 && !baiDuService.textToExamine(req.getProblem())){
            throw new E("提问违反相关规定，请更换内容重新尝试");
        }
        //国内需要代理 国外不需要
        Proxy proxy = null ;
        if(null != cacheObject.getIsOpenProxy() && cacheObject.getIsOpenProxy() == 1){
            proxy = Proxys.http(cacheObject.getProxyIp(), cacheObject.getProxyPort());
        }
        String gptKey = InitUtil.getRandomKey(req.getType());
        if(req.getType() > 4){
            List<ImageMessage> imageMessages = new ArrayList<>();
            ImageMessage imageMessage = new ImageMessage();
            imageMessage.setText(JSONObject.parseArray(req.getProblem()).getJSONObject(0).getString("text"));
            imageMessage.setType("text");
            imageMessages.add(imageMessage);
            imageMessage = new ImageMessage();
            imageMessage.setType("image_url");
            imageMessage.setImage_url(Collections.singletonList(req.getFileUrl()));
            imageMessages.add(imageMessage);
            req.setProblem(JSONObject.toJSONString(imageMessages));
        }
        List<Message> messages = messageLogService.createMessageLogList(req.getLogId(),req.getProblem());
        if(StringUtils.isEmpty(req.getRole())){
            messages.add(Message.ofSystem(cacheObject.getDefaultRole()));
        }
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(req.getType() == 3 ? CommonConst.GPT_NUMBER : CommonConst.GPT_4_NUMBER)
                .sendType(req.getType() == 3 ? SendType.GPT.getType() : SendType.GPT_4.getType())
                .useValue(JSONObject.toJSONString(messages))
                .gptKey(gptKey)
                .userId(JwtUtil.getUserId()).build(),req.getLogId());
        String api = cacheObject.getGptUrl();
        if(req.getType() == 4){
            api = cacheObject.getGpt4Url();
        }
        if(req.getType() == 5){
            api = cacheObject.getGpt4VisionUrl();
        }
        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(600)
                .apiKey(gptKey)
                .proxy(proxy)
                .apiHost(api)
                .build()
                .init();
        ConsoleStreamListener listener = ConsoleStreamListener.builder()
                .userId(JwtUtil.getUserId())
                .userService(userService)
                .gptKeyService(gptKeyService)
                .asyncService(asyncService)
                .logId(logId)
                .build();
        listener.setOnComplate(msg -> {
            asyncService.endOfAnswer(logId,msg.toString());
        });
        chatGPTStream.streamChatCompletion(messages, listener,req.getType(),req);
        return B.okBuild(logId);
    }
    @PostMapping(value = "/official", name = "AI-画图")
    public B<MessageLogSave> gptAlpha(@Validated @RequestBody GptAlphaReq req) throws IOException {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(cacheObject.getIsOpenGptOfficial() == 0){
            throw new E("Ai画图已关闭");
        }
        final String randomKey = InitUtil.getRandomKey(4);
        List<String> imgUrlList = new ArrayList<>();
        List<String> returnImgUrlList = new ArrayList<>();
        String startTime = DateUtil.getLocalDateTimeNow();
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(CommonConst.GPT_OFFICIAL_NUMBER)
                .sendType(SendType.GPT_OFFICIAL.getType())
                .useValue(JSONObject.toJSONString(MessageLogSave.builder()
                        .prompt(req.getPrompt())
                        .type(SendType.GPT_OFFICIAL.getRemark())
                        .startTime(startTime)
                        .imgList(imgUrlList).build()))
                .gptKey(randomKey)
                .userId(JwtUtil.getUserId()).build(),null);
        Proxy proxy = null ;
        if(null != cacheObject.getIsOpenProxy() && cacheObject.getIsOpenProxy() == 1){
            proxy = Proxys.http(cacheObject.getProxyIp(), cacheObject.getProxyPort());
        }
        String resultBody = HttpUtil.createPost(cacheObject.getGptUrl() + CommonConst.CPT_IMAGES_URL)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.AUTHORIZATION, "Bearer " + randomKey)
                .setProxy(proxy)
                .body(JSONObject.toJSONString(req))
                .execute()
                .body();
        if(resultBody.contains("error")){
            //修改key状态
            asyncService.updateKeyState(randomKey);
            //将用户使用次数返回
            asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.GPT_OFFICIAL_NUMBER);
            throw new E("画图失败请稍后再试");
        }
        JSONArray imgArray = JSONObject.parseObject(resultBody).getJSONArray("data");
        for (int i = 0; i < imgArray.size(); i++) {
            String localImgUrl = FileUtil.base64ToImage(FileUtil.imageUrlToBase64(imgArray.getJSONObject(i).getString("url")));
            imgUrlList.add(localImgUrl);
            returnImgUrlList.add(cacheObject.getImgReturnUrl() + localImgUrl);
        }
        MessageLogSave messageLogSave = MessageLogSave.builder()
                .prompt(req.getPrompt())
                .type(SendType.GPT_OFFICIAL.getRemark())
                .startTime(startTime)
                .imgList(imgUrlList).build();
        asyncService.updateLog(logId,messageLogSave);
        MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
        returnMessage.setImgList(returnImgUrlList);
        return B.okBuild(returnMessage);
    }

}
