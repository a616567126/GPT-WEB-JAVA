package com.intelligent.bot.utils.sys;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.PayConfig;
import com.intelligent.bot.model.SysConfig;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class WxPayUtil {

    private static final String signatureType  = "WECHATPAY2-SHA256-RSA2048";

    public static String getToken(String method, HttpUrl url, String body) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        String nonceStr = RandomUtil.randomString(32);
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));

        return signatureType + " mchid=\"" + payConfig.getWxMchid() + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + payConfig.getWxSerialNo() + "\","
                + "signature=\"" + signature + "\"";
    }

    /**
     * 获取私钥。
     *
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKey() throws IOException {
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        try {
            String privateKey = payConfig.getWxPrivateKey().replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }
    public static String sign(byte[] message) throws NoSuchAlgorithmException, IOException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(getPrivateKey());
        sign.update(message);
        return Base64.getEncoder().encodeToString(sign.sign());
    }
    public static String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }
        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }
    /**
     * 构建下单支付请求JSON字符串
     *
     * @return
     */
    public static String buildNativePayJson(BigDecimal total_amount, String out_trade_no, String description) {
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        JSONObject jsonObject = new JSONObject();
        // 应用ID
        jsonObject.put("appid", payConfig.getWxAppid());
        // 商户号
        jsonObject.put("mchid", payConfig.getWxMchid());
        // 商户订单号
        jsonObject.put("out_trade_no", out_trade_no);
        // 商品描述
        jsonObject.put("description", description);
        // 回调通知URL
        jsonObject.put("notify_url", cacheObject.getApiUrl() + CommonConst.WX_PAY_CALL_BACK);
        JSONObject amount = new JSONObject();
        // 单位：分
        amount.put("total", total_amount.multiply(new BigDecimal("100")).intValue());
        //货币类型
        amount.put("currency", "CNY");
        jsonObject.put("amount", amount);
        return jsonObject.toJSONString();
    }
}
