package com.cn.app.chatgptbot.config.ali;

import com.alibaba.fastjson.JSON;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.cn.app.chatgptbot.model.PayConfig;
import com.cn.app.chatgptbot.model.ali.AlipayNotifyParam;
import com.cn.app.chatgptbot.service.IPayConfigService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ClassName:AliPayConfig
 * Package:com.cn.app.chatgptbot.config.ali
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/4/9 - 09:10
 * @Version: v1.0
 */
@Component
@Log4j2
public class AliPayConfig implements ApplicationRunner {

    @Resource
    private IPayConfigService payConfigService;

    // 签名类型，Alipay Easy SDK只推荐使用RSA2，估此处固定填写RSA2
    public static final String SIGN_TYPE = "RSA2";
    // 请求使用的编码格式，如utf-8,gbk,gb2312等
    public static final String CHARSET = "utf-8";

    public static final String PROTOCOL = "https";



    @Override
    public void run(ApplicationArguments args){
        PayConfig payConfig = payConfigService.getById(1L);
        if(payConfig.getPayType() > 1){
            Factory.setOptions(getOptions(payConfig));
            log.info("********支付宝SDK初始化完成!******");
        }
    }
    public Config getOptions(PayConfig payConfig) {
        Config config = new Config();
        config.protocol = PROTOCOL;
        config.gatewayHost = payConfig.getAliGatewayUrl();
        config.signType = SIGN_TYPE;
        //支付宝
        config.appId = payConfig.getAliAppId();
        config.merchantPrivateKey = payConfig.getAliPrivateKey();
        config.alipayPublicKey = payConfig.getAliPublicKey();
        config.notifyUrl = payConfig.getAliNotifyUrl();
        return config;
    }

    /**
     * @Description 将request中的参数转为Map
     * @Param request 回调请求
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public static Map<String, String> convertRequestParamsToMap(HttpServletRequest request){
        HashMap<String, String> retMap = new HashMap<>();
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entrySet){
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;
            if (valLen == 1){
                retMap.put(name, values[0]);
            }else if (valLen > 1){
                StringBuilder sb = new StringBuilder();
                for (String val : values){
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            }else {
                retMap.put(name, "");
            }
        }
        return retMap;
    }


    /**
     * @Description 生成请求实体类
     * @Param params 响应体参数转map
     * @return marchsoft.modules.spiritdeerpush.common.utils.pay.alipay.AlipayNotifyParam
     */
    public static AlipayNotifyParam buildAlipayNotifyParam(Map<String, String> params) {
        String json = JSON.toJSONString(params);
        return JSON.parseObject(json, AlipayNotifyParam.class);
    }

}
