package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.model.*;
import com.chat.java.model.ali.AlipayNotifyParam;
import com.chat.java.model.ali.req.AliPayCreateReq;
import com.chat.java.model.res.CreateOrderRes;
import com.chat.java.model.res.QueryOrderRes;
import com.chat.java.base.B;
import com.chat.java.config.ali.AliPayConfig;
import com.chat.java.dao.OrderDao;
import com.chat.java.exception.CustomException;
import com.chat.java.model.wx.req.WxPayCreateReq;
import com.chat.java.model.wx.res.NativeCallBackRes;
import com.chat.java.model.wx.res.WxPayCreateRes;
import com.chat.java.service.IOrderService;
import com.chat.java.service.IProductService;
import com.chat.java.service.IRefuelingKitService;
import com.chat.java.service.IUserService;
import com.chat.java.model.*;
import com.chat.java.model.req.CreateOrderReq;
import com.chat.java.model.req.OrderCallBackReq;
import com.chat.java.model.req.QueryOrderReq;
import com.chat.java.model.req.ReturnUrlReq;
import com.chat.java.model.res.ReturnUrlRes;
import com.chat.java.service.*;
import com.chat.java.utils.HttpUtils;
import com.chat.java.utils.JwtUtil;
import com.chat.java.utils.RedisUtil;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chat.java.utils.WxPayUtil;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Service("orderService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements IOrderService {

    @Resource
    private IProductService productService;

    @Resource
    private IUserService userService;

    @Resource
    private IRefuelingKitService refuelingKitService;

    @Override
    public synchronized  B<CreateOrderRes> createOrder(CreateOrderReq req) {
        Product product = productService.getById(req.getProductId());
        if (null == product) {
            return B.finalBuild("商品异常");
        }
        if(product.getStock() < req.getPayNumber()){
            return B.finalBuild("库存不足");
        }
        Order order = BeanUtil.copyProperties(req, Order.class);
        order.setUserId(JwtUtil.getUserId());
        order.setPrice(product.getPrice().multiply(new BigDecimal(req.getPayNumber())));
        order.setPayType(req.getType());
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
//        PayConfig payConfig = payConfigService.lambdaQuery().list().get(0);
        CreateOrderRes res = new CreateOrderRes();
        res.setOutTradeNo(order.getId().toString());
        res.setPid(payConfig.getPid());
        res.setKey(payConfig.getSecretKey());
        res.setMoney(order.getPrice().toString());
        res.setName(product.getName());
        res.setUrl(payConfig.getSubmitUrl());
        res.setNotifyUrl(payConfig.getNotifyUrl()+"/order/callback");
        res.setReturnUrl(payConfig.getReturnUrl());
        res.setType(req.getType());
        res.setSign(createSign(res));
        res.setKey(null);
        return B.okBuild(res);
    }

    @Override
    public B returnUrl(ReturnUrlReq req) {
        Order order = this.getById(req.getOrderId());
        if(order.getState() == 1){
            return B.finalBuild("订单已完成");
        }
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
//        PayConfig payConfig = payConfigService.lambdaQuery().list().get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("act","order");
        map.put("pid",payConfig.getPid());
        map.put("key",payConfig.getSecretKey());
        map.put("out_trade_no",req.getOrderId());
        String orderPayInfo = HttpUtil.get(payConfig.getApiUrl(), map);
        ReturnUrlRes returnUrlRes = JSONObject.parseObject(orderPayInfo, ReturnUrlRes.class);
        if (returnUrlRes.getCode() != 1) {
            return B.finalBuild("订单支付异常");
        }
        order.setPayType(returnUrlRes.getType());
        order.setTradeNo(returnUrlRes.getTrade_no());
        BigDecimal money = returnUrlRes.getMoney();
        if (money.compareTo(order.getPrice()) != 0) {
            log.info("支付失败,支付金额异常，支付金额：{},订单金额：{}", money, order.getPrice());
            order.setMsg("支付失败,支付金额异常，支付金额：" + money + "订单金额" + order.getPrice());
            order.setState(2);
        }
        if (returnUrlRes.getStatus() == 1) {
            order.setState(1);
            order.setMsg("支付成功");
        } else {
            log.info("支付失败,支付状态：{}", returnUrlRes.getStatus());
            order.setMsg("支付失败,支付状态：" + returnUrlRes.getStatus());
            order.setState(2);
        }
        if(null != returnUrlRes.getEndtime()){
            LocalDateTime endTime = LocalDateTime.parse(returnUrlRes.getEndtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            order.setOperateTime(endTime);
        }
       if(null != returnUrlRes.getAddtime()){
           LocalDateTime addTime = LocalDateTime.parse(returnUrlRes.getAddtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
           order.setCreateTime(addTime);
       }
        this.saveOrUpdate(order);
        if (order.getState() == 1) {
            Product product = productService.getById(order.getProductId());
            User user = userService.getById(order.getUserId());
            if (product.getType() == 0) {
                //次数
                user.setRemainingTimes(user.getRemainingTimes() + product.getNumberTimes());
            }
            if (product.getType() == 1) {
                //月卡
                if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {
                    user.setExpirationTime(user.getExpirationTime().plusDays(30L));
                } else {
                    user.setExpirationTime(LocalDateTime.now().plusDays(30L));
                }
                user.setCardDayMaxNumber(product.getMonthlyNumber());
                user.setType(1);
            }
            if (product.getType() == 2) {
                //加油包
                RefuelingKit kit = new RefuelingKit();
                kit.setProductId(product.getId());
                kit.setNumberTimes(product.getNumberTimes());
                kit.setUserId(order.getUserId());
                refuelingKitService.save(kit);
            }
            userService.updateById(user);
            if(order.getState() == 1){
                product.setStock(product.getStock() - order.getPayNumber());
                productService.saveOrUpdate(product);
            }
        }
        return B.okBuild();
    }

    @Override
    public String callback(OrderCallBackReq req) {
        log.info("支付开始回调，回调参数：{}",req.toString());
        Order order = this.getById(req.getOut_trade_no());
        if(order.getState() == 1){
            return "success";
        }
        order.setPayType(req.getType());
        order.setTradeNo(req.getTrade_no());
        BigDecimal money = new BigDecimal(req.getMoney());
        if (money.compareTo(order.getPrice()) != 0) {
            log.info("支付失败,支付金额异常，支付金额：{},订单金额：{}", money, order.getPrice());
            order.setMsg("支付失败,支付金额异常，支付金额：" + money + "订单金额" + order.getPrice());
            order.setState(2);
        }
        if (req.getTrade_status().equals("TRADE_SUCCESS")) {
            order.setState(1);
            order.setMsg("支付成功");
        } else {
            log.info("支付失败,支付状态：{}", req.getTrade_status());
            order.setMsg("支付失败,支付状态：" + req.getTrade_status());
            order.setState(2);
        }
        order.setOperateTime(LocalDateTime.now());
        this.saveOrUpdate(order);
        if (order.getState() == 1) {
            Product product = productService.getById(order.getProductId());
            User user = userService.getById(order.getUserId());
            if (product.getType() == 0) {
                //次数
                user.setRemainingTimes(user.getRemainingTimes() + product.getNumberTimes());
            }
            if (product.getType() == 1) {
                //月卡
                if(null == user.getExpirationTime()){
                    user.setExpirationTime(LocalDateTime.now().plusDays(30L * order.getPayNumber()));
                } else if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {
                    user.setExpirationTime(user.getExpirationTime().plusDays(30L * order.getPayNumber()));
                } else {
                    user.setExpirationTime(LocalDateTime.now().plusDays(30L * order.getPayNumber()));
                }
                user.setCardDayMaxNumber(product.getMonthlyNumber());
                user.setType(1);
            }
            if (product.getType() == 2) {
                //加油包
                RefuelingKit kit = new RefuelingKit();
                kit.setProductId(product.getId());
                kit.setNumberTimes(product.getNumberTimes());
                kit.setUserId(order.getUserId());
                refuelingKitService.save(kit);
            }
            userService.saveOrUpdate(user);
            if(order.getState() == 1){
                product.setStock(product.getStock() - order.getPayNumber());
                productService.saveOrUpdate(product);
            }
        }
        return "success";
    }

    @Override
    public B query(QueryOrderReq req) {
        Page<QueryOrderRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        Page<QueryOrderRes> queryOrderResPage = this.baseMapper.queryOrder(page, req);
        return B.okBuild(queryOrderResPage);
    }

    @Override
    public B<String> aliCreateOrder(AliPayCreateReq req) throws Exception {
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        if(payConfig.getPayType() < 2){
            return B.finalBuild("支付宝支付通道关闭");
        }
        Product product = productService.getById(req.getProductId());
        if (null == product) {
            return B.finalBuild("商品异常");
        }
        if(product.getStock() < req.getPayNumber()){
            return B.finalBuild("库存不足");
        }
        Order order = BeanUtil.copyProperties(req, Order.class);
        order.setUserId(JwtUtil.getUserId());
        order.setPrice(product.getPrice().multiply(new BigDecimal(req.getPayNumber())));
        order.setPayType("支付宝");
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        // 生成系统订单号
        String outTradeNo = order.getId().toString();
        AlipayTradePagePayResponse response = Factory.Payment.Page()
                .pay("商品"+product.getName()+"数量"+order.getPayNumber(), outTradeNo, order.getPrice().toString(),payConfig.getAliReturnUrl());
        if (!ResponseChecker.success(response)) {
            throw new CustomException("预订单生成失败");
        }
        return B.okBuild(response.getBody().replace("\"","").replace("\n",""));
    }

    @Override
    public String aliCallBack(HttpServletRequest request) throws Exception {
        Map<String, String> stringStringMap = AliPayConfig.convertRequestParamsToMap(request);
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        if (Factory.Payment.Common().verifyNotify(stringStringMap)){
            if(!stringStringMap.get("app_id").equals(payConfig.getAliAppId())){
                log.info("appId不一致");
                return "failure";
            }
            AlipayNotifyParam param = AliPayConfig.buildAlipayNotifyParam(stringStringMap);
            // 支付成功
            Order order = this.getById(param.getOutTradeNo());
            if(null == order){
                log.info("订单不存在");
                return "failure";
            }else {
                if(order.getPrice().compareTo(param.getTotalAmount()) != 0){
                    log.info("订单金额异常");
                    return "failure";
                }
                order.setTradeNo(param.getTradeNo());
                order.setOperateTime(LocalDateTime.now());
                order.setState(1);
                this.saveOrUpdate(order);
            }
            return "success";
        }else {
            return "failure";
        }
    }

    @Override
    public B<WxPayCreateRes> wxCreateOrder(WxPayCreateReq req) throws Exception {
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        if(payConfig.getPayType() != 1 &&  payConfig.getPayType() != 3 ){
            throw new CustomException("微信支付通道关闭");
        }
        Product product = productService.getById(req.getProductId());
        if (null == product) {
            throw new CustomException("商品异常");
        }
        if(product.getStock() < req.getPayNumber()){
            throw new CustomException("库存不足");
        }
        WxPayCreateRes res = new WxPayCreateRes();
        Order order = BeanUtil.copyProperties(req, Order.class);
        order.setUserId(JwtUtil.getUserId());
        order.setPrice(product.getPrice().multiply(new BigDecimal(req.getPayNumber())));
        order.setPayType("微信");
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        //生成请求参数
        String bodyParam = WxPayUtil.buildNativePayJson(order.getPrice(),
                order.getId().toString(),
                product.getName() + "*" + order.getPayNumber());
        HttpUrl httpurl = HttpUrl.parse(payConfig.getWxNativeUrl());
        String sign = WxPayUtil.getToken("POST", httpurl, bodyParam);
        String body = HttpUtil.createPost(payConfig.getWxNativeUrl())
                .header(Header.AUTHORIZATION, sign)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(bodyParam)
                .execute()
                .body();
        if(!body.contains("code_url")){
            throw new CustomException("微信预订单生成失败");
        }
        res.setCodeUrl(JSONObject.parseObject(body).getString("code_url"));
        res.setPrice(order.getPrice());
        return B.okBuild(res);
    }

    @Override
    public NativeCallBackRes wxCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        cn.hutool.json.JSONObject requestData = HttpUtils.readData(request);
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        String key = payConfig.getWxV3Secret();
        cn.hutool.json.JSONObject resource = requestData.getJSONObject("resource");
        String associatedData = resource.getStr("associated_data");
        String nonce = resource.getStr("nonce");
        String content = resource.getStr("ciphertext");
        byte[] decode = Base64.getDecoder().decode(content);
        cipher.init(2, new SecretKeySpec(key.getBytes(), "AES"), new GCMParameterSpec(128, nonce.getBytes()));
        if (StringUtils.isNotEmpty(associatedData)) {
            cipher.updateAAD(associatedData.getBytes());
        }
        JSONObject ciphertext = JSONObject.parseObject(new String(cipher.doFinal(decode), StandardCharsets.UTF_8));
        String tradeState = ciphertext.getString("trade_state");
        if(tradeState.equals("SUCCESS")){
            BigDecimal total = ciphertext.getJSONObject("amount").getBigDecimal("total");
            String outTradeNo = ciphertext.getString("out_trade_no");
            String transactionId = ciphertext.getString("transaction_id");
            JSONArray promotionDetail = ciphertext.getJSONArray("promotion_detail");
            BigDecimal promotionPrice = new BigDecimal("0");
            if(null != promotionDetail && promotionDetail.size() > 0){
                for (int i = 0; i < promotionDetail.size(); i++) {
                    JSONObject promotion = promotionDetail.getJSONObject(i);
                    promotionPrice = promotionPrice.add(promotion.getBigDecimal("amount"));
                }
            }
            Order order = this.getById(Long.valueOf(outTradeNo));
            if(null != order && order.getState() == 0){
                if(order.getPrice().compareTo(total.add(promotionPrice)) != 0){
                    order.setState(1);
                    order.setTradeNo(transactionId);
                    order.setOperateTime(LocalDateTime.now());
                    this.saveOrUpdate(order);
                }
            }
        }
        response.setStatus(200);
        return new NativeCallBackRes();
    }

    public static String createSign(CreateOrderRes res){
        Map<String,String> sign = new HashMap<>();
        sign.put("pid",res.getPid().toString());
        sign.put("type",res.getType());
        sign.put("out_trade_no",res.getOutTradeNo());
        sign.put("notify_url",res.getNotifyUrl());
        sign.put("return_url",res.getReturnUrl());
        sign.put("name",res.getName());
        sign.put("money",res.getMoney());
        sign = sortByKey(sign);
        //遍历map 转成字符串
        String signStr = "";
        for(Map.Entry<String,String> m :sign.entrySet()){
            signStr += m.getKey() + "=" +m.getValue()+"&";
        }
        //去掉最后一个 &
        signStr = signStr.substring(0,signStr.length()-1);
        //最后拼接上KEY
        signStr += res.getKey();
        //转为MD5
        signStr = DigestUtils.md5DigestAsHex(signStr.getBytes());
        return signStr;
    }
    public static <K extends Comparable<? super K>, V > Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
