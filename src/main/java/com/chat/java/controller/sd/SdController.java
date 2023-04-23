package com.chat.java.controller.sd;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.exception.CustomException;
import com.chat.java.model.SysConfig;
import com.chat.java.model.req.SdCreateReq;
import com.chat.java.utils.RedisUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sd")
@Log4j2
public class SdController {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiOperation(value = "生成图片")
    public B<List<String>> createImage(@Validated @RequestBody SdCreateReq req) {
        List<String> imgUrlList = new ArrayList<String>();
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        if(null == cacheObject.getIsOpenSd() || cacheObject.getIsOpenSd() == 0){
            throw new CustomException("暂未开启sd");
        }
        JSONObject params = new JSONObject();
        params.put("prompt",req.getPrompt());
        params.put("negative_prompt",req.getPrompt());
        params.put("width",req.getWidth());
        params.put("height",req.getHeight());
        params.put("steps",req.getSteps());
        params.put("batch_size",req.getBatchSize());
        params.put("cfg_scale",req.getCfgScale());
        params.put("seed",req.getSeed());
        params.put("sampler_index",req.getSamplerIndex());
        params.put("eta",req.getEta());
        log.info("sd请求地址：{}",cacheObject.getSdUrl()+"/sdapi/v1/txt2img");
        String body = HttpUtil.createPost(cacheObject.getSdUrl()+"/sdapi/v1/txt2img")
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSONObject.toJSONString(params))
                .execute()
                .body();
        List<String> images = JSONObject.parseArray(JSONObject.parseObject(body).getJSONArray("images").toJSONString(), String.class);
        images.forEach(i ->{
            try {
                imgUrlList.add(cacheObject.getImgReturnUrl()+base64ToImage(i));
            } catch (IOException e) {
                throw new CustomException(e.getMessage());
            }
        });

        return B.okBuild(imgUrlList);
    }

    public static String base64ToImage(String base64) throws IOException {
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        // JDK8以上
        Base64.Decoder decoder = Base64.getDecoder();
        // Base64解码
        byte[] bytes = decoder.decode(base64);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        String imgUploadUrl =  cacheObject.getImgUploadUrl();
        String dayFilePatch = DateUtil.format(new Date(), "yyyyMMdd");
        File destFile = new File(imgUploadUrl+dayFilePatch);
        if (!destFile.exists()) {
            //不存在就创建
            destFile.mkdir();
        }
        String newFileName ="/"+System.currentTimeMillis()+".jpg";
        // 生成jpeg图片
        OutputStream out = Files.newOutputStream(Paths.get(imgUploadUrl+dayFilePatch+newFileName));
        out.write(bytes);
        out.flush();
        out.close();
        return "/"+dayFilePatch+newFileName;
    }

    @RequestMapping(value = "/getOpenSdState", method = RequestMethod.POST)
    @ApiOperation(value = "获取sd开启状态")
    public B<Integer> getOpenSdState() {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        return B.okBuild(null == sysConfig.getIsOpenSd() ? 0 : sysConfig.getIsOpenSd());
    }

}
