package com.chat.java.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.chat.java.model.SysConfig;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:MsmServicelUti
 * Package:com.cn.app.chatgptbot.utils
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/4/16 - 18:22
 * @Version: v1.0
 *
 */

public class MsmServiceUtil {

    /**
     * 发送验证码
     * @param phone     手机号
     * @return
     */
    public static boolean send( String phone,String code) throws ClientException {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        Map<String,Object> param = new HashMap<>();
        param.put("code", code);
        if(StringUtils.isEmpty(phone)) return false;
        //default 地域节点，默认就好  后面是 阿里云的 id和秘钥（这里记得去阿里云复制自己的id和秘钥哦）
        DefaultProfile profile = DefaultProfile.getProfile("default", sysConfig.getAliAccessKeyId(), sysConfig.getAliSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        //这里不能修改
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //申请阿里云 签名名称（暂时用阿里云测试的，自己还不能注册签名）
        request.putQueryParameter("SignName", sysConfig.getAliSignName());
        //申请阿里云 模板code（用的也是阿里云测试的）
        request.putQueryParameter("TemplateCode", sysConfig.getAliTemplateCode());
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        return response.getHttpResponse().isSuccess();
    }
}
