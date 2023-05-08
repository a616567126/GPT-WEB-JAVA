package com.chat.java.controller.flagstudio;

import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.controller.sd.SdController;
import com.chat.java.exception.CustomException;
import com.chat.java.flow.service.CheckService;
import com.chat.java.model.SysConfig;
import com.chat.java.model.req.FlagStudioImgCreateReq;
import com.chat.java.model.req.FlagStudioTextCreateReq;
import com.chat.java.utils.JwtUtil;
import com.chat.java.utils.RedisUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
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
public class FlagStudioController {

    @Resource
    CheckService checkService;



    private static final String FLAG_STUDIO_TOKEN = "flag:studio";
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiOperation(value = "生成图片")
    public B<List<String>> createImage(@Validated @RequestBody FlagStudioTextCreateReq req) throws IOException {
        List<String> imgUrlList = new ArrayList<String>();
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        if(null == cacheObject.getIsOpenFlagStudio() || cacheObject.getIsOpenFlagStudio() == 0){
            throw new CustomException("暂未开启FlagStudio");
        }
        B result = checkService.checkUser("", JwtUtil.getUserId(), false, 2);
        if(result.getCode() != 20000){
            throw new CustomException(result.getMessage());
        }
        JSONObject params = new JSONObject();
        params.put("prompt",req.getPrompt());
        params.put("guidance_scale",req.getGuidanceScale());
        params.put("height",req.getHeight());
        params.put("negative_prompts",req.getNegativePrompts());
        params.put("sampler",req.getSampler());
        params.put("seed",req.getSeed());
        params.put("steps",req.getSteps());
        params.put("style",req.getStyle());
        params.put("upsample",req.getUpsample());
        params.put("width",req.getWidth());
        String body = HttpUtil.createPost(cacheObject.getFlagStudioUrl()+"/v1/text2img")
                .header(Header.CONTENT_TYPE, "application/json")
                .header(Header.ACCEPT, "application/json")
                .header("token", getToken())
                .body(JSONObject.toJSONString(params))
                .execute()
                .body();
        JSONObject bodyJson = JSONObject.parseObject(body);
        Integer nsfw = bodyJson.getInteger("nsfw");
        if(nsfw == 1){
            throw new CustomException("图片违规");
        }else {
            imgUrlList.add(cacheObject.getImgReturnUrl()+ SdController.base64ToImage(bodyJson.getString("data")));
        }
        return B.okBuild(imgUrlList);
    }

    @RequestMapping(value = "/getUploadLink", method = RequestMethod.POST)
    @ApiOperation(value = "获取上传图片的地址")
    public B<JSONObject> getUploadLink()  {
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        String body = HttpUtil.createGet(cacheObject.getFlagStudioUrl()+"/v1/getUploadLink")
                .header("token", getToken())
                .execute()
                .body();
        log.info(body);
        JSONObject bodyJson = JSONObject.parseObject(body);
        if(bodyJson.getInteger("code") != 200){
            throw new CustomException("获取地址上传地址失败");
        }
        return B.okBuild( bodyJson.getJSONObject("data"));
    }

    @RequestMapping(value = "img/create", method = RequestMethod.POST)
    @ApiOperation(value = "图生图片")
    public B<List<String>> imgCreateImage(@Validated @RequestBody FlagStudioImgCreateReq req) throws IOException {
        List<String> imgUrlList = new ArrayList<String>();
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        if(null == cacheObject.getIsOpenFlagStudio() || cacheObject.getIsOpenFlagStudio() == 0){
            throw new CustomException("暂未开启FlagStudio");
        }
        B result = checkService.checkUser("", JwtUtil.getUserId(), false, 2);
        if(result.getCode() != 20000){
            throw new CustomException(result.getMessage());
        }
        JSONObject params = new JSONObject();
        params.put("prompt",req.getPrompt());
        params.put("filename",req.getFilename());
        params.put("guidance_scale",req.getGuidanceScale());
        params.put("height",req.getHeight());
        params.put("negative_prompts",req.getNegativePrompts());
        params.put("sampler",req.getSampler());
        params.put("seed",req.getSeed());
        params.put("steps",req.getSteps());
        params.put("style",req.getStyle());
        params.put("upsample",req.getUpsample());
        params.put("width",req.getWidth());
        params.put("controlnet_task",req.getControlnetTask());
        String body = HttpUtil.createPost(cacheObject.getFlagStudioUrl()+"/v1/img2img")
                .header(Header.CONTENT_TYPE, "application/json")
                .header(Header.ACCEPT, "application/json")
                .header("token", getToken())
                .body(JSONObject.toJSONString(params))
                .execute()
                .body();
        JSONObject bodyJson = JSONObject.parseObject(body);
        Integer nsfw = bodyJson.getInteger("nsfw");
        if(nsfw == 1){
            throw new CustomException("图片违规");
        }else {
            imgUrlList.add(cacheObject.getImgReturnUrl()+SdController.base64ToImage(bodyJson.getString("data")));
        }
        return B.okBuild(imgUrlList);
    }

    public static String getToken(){
        String token = RedisUtil.getCacheObject(FLAG_STUDIO_TOKEN);
        if(StringUtils.isEmpty(token)){
            SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
            if(null == cacheObject.getIsOpenFlagStudio()
                    || cacheObject.getIsOpenFlagStudio() == 0
                    || null == cacheObject.getFlagStudioKey()){
                throw new CustomException("暂时无法获取Token");
            }
            String body = HttpUtil.createGet(cacheObject.getFlagStudioUrl() + "/auth/getToken")
                    .header(Header.ACCEPT, "application/json")
                    .form("apikey",cacheObject.getFlagStudioKey())
                    .execute()
                    .body();
            if(!body.contains("200")){
                throw new CustomException("token获取失败");
            }else {
                token = JSONObject.parseObject(body).getJSONObject("data").getString("token");
                RedisUtil.setCacheObject(FLAG_STUDIO_TOKEN,token,29L, TimeUnit.DAYS);
            }
        }
        return token;
    }

}
