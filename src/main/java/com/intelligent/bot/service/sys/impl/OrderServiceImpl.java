package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.dao.OrderDao;
import com.intelligent.bot.model.*;
import com.intelligent.bot.model.req.sys.CreatePinOrderReq;
import com.intelligent.bot.model.req.sys.CreateYiOrderReq;
import com.intelligent.bot.model.req.sys.OrderYiCallBackReq;
import com.intelligent.bot.model.req.sys.OrderYiReturnReq;
import com.intelligent.bot.model.req.sys.admin.OrderQueryReq;
import com.intelligent.bot.model.res.sys.ClientOrderRes;
import com.intelligent.bot.model.res.sys.CreateOrderRes;
import com.intelligent.bot.model.res.sys.ReturnUrlRes;
import com.intelligent.bot.model.res.sys.admin.OrderQueryRes;
import com.intelligent.bot.model.res.wx.NativeCallBackRes;
import com.intelligent.bot.service.sys.ICardPinService;
import com.intelligent.bot.service.sys.IOrderService;
import com.intelligent.bot.service.sys.IProductService;
import com.intelligent.bot.service.sys.IUserService;
import com.intelligent.bot.utils.sys.HttpUtils;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service("orderService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements IOrderService {

    @Resource
    private IProductService productService;

    @Resource
    private IUserService userService;

    @Resource
    private ICardPinService cardPinService;

    @Resource
    WxMpService wxMpService;



    @Override
    public synchronized  B<CreateOrderRes> createYiOrder(CreateYiOrderReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
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
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        CreateOrderRes res = new CreateOrderRes();
        res.setOutTradeNo(order.getId().toString());
        res.setPid(payConfig.getPid());
        res.setKey(payConfig.getSecretKey());
        res.setMoney(order.getPrice().toString());
        res.setName(product.getName());
        res.setUrl(payConfig.getSubmitUrl());
        res.setNotifyUrl(cacheObject.getApiUrl() + CommonConst.YI_PAY_CALL_BACK);
        res.setReturnUrl(cacheObject.getClientUrl() + CommonConst.Yi_PAY_RETURN_RUL);
        res.setType(order.getPayType());
        res.setSign(createSign(res));
        res.setKey(null);
        return B.okBuild(res);
    }

    @Override
    public String yiCallBack(OrderYiCallBackReq req) {
        log.info("支付开始回调，回调参数：{}",req.toString());
        Order order = this.getById(req.getOut_trade_no());
        if(order.getState() == 1){
            return "success";
        }
        order.setPayType(req.getType());
        order.setTradeNo(req.getTrade_no());
        BigDecimal money = new BigDecimal(req.getMoney());
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        if(!payConfig.getPid().equals(req.getPid())){
            order.setMsg("支付失败,回调商户id异常");
            order.setState(2);
        }
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
        okOrder(order);
        return "success";
    }

    @Override
    public NativeCallBackRes wxCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        cn.hutool.json.JSONObject requestData = HttpUtils.readData(request);
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
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
        String outTradeNo = ciphertext.getString("out_trade_no");
        Order order = this.getById(Long.valueOf(outTradeNo));
        User user = userService.getById(order.getUserId());
        String messageContent = "卡密获取失败";
        if(tradeState.equals("SUCCESS")) {
            Integer number = 100;
            if (order.getTradeNo().equals("2")) {
                number = 220;
            }
            if (order.getTradeNo().equals("3")) {
                number = 1500;
            }
            String cardPin = cardPinService.add(number);
            messageContent = "卡密获取成功，点击下方兑换\t" +
                    "<a href=\"weixin://bizmsgmenu?msgmenucontent=兑换-" + cardPin + "&msgmenuid=2\">兑换</a>";
            BigDecimal total = ciphertext.getJSONObject("amount").getBigDecimal("total");
            String transactionId = ciphertext.getString("transaction_id");
            JSONArray promotionDetail = ciphertext.getJSONArray("promotion_detail");
            BigDecimal promotionPrice = new BigDecimal("0");
            if (null != promotionDetail && promotionDetail.size() > 0) {
                for (int i = 0; i < promotionDetail.size(); i++) {
                    JSONObject promotion = promotionDetail.getJSONObject(i);
                    promotionPrice = promotionPrice.add(promotion.getBigDecimal("amount"));
                }
            }
            if (null != order && order.getState() == 0) {
                if (order.getPrice().compareTo(total.add(promotionPrice)) != 0) {
                    order.setState(1);
                    order.setTradeNo(transactionId);
                    order.setOperateTime(LocalDateTime.now());
                    order.setMsg(order.getMsg() + "，卡密：" + cardPin);
                    this.saveOrUpdate(order);
                }
            }
        }
        WxMpKefuMessage wxMpKefuMessage = WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(messageContent).build();
        wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);
        response.setStatus(200);
        return new NativeCallBackRes();
    }

    @Override
    public B<Void> yiReturnUrl(OrderYiReturnReq req) {
        Order order = this.getById(req.getOrderId());
        if(order.getState() == 1){
            throw new E("订单已完成");
        }
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        if(null == payConfig.getPayType() || payConfig.getPayType() != 0){
            throw new E("当前支付方式暂未开启");
        }
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
        okOrder(order);
        return B.okBuild();
    }

    @Override
    public B<Void> cardPin(CreatePinOrderReq req) {
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        if(null == payConfig.getPayType() || payConfig.getPayType() != 1){
            throw new E("当前支付方式暂未开启");
        }
        int i = cardPinService.checkUseBatchCardPin(JwtUtil.getUserId(), req.getCardPin());
        if(i > 0){
            throw new E("同一批次只能兑换一次");
        }
        Order order = new Order();
        List<CardPin> list = cardPinService.lambdaQuery().eq(CardPin::getCardPin, req.getCardPin()).list();
        if(null == list || list.size() == 0 || list.get(0).getState() == 1){
            order.setMsg("无效卡密");
            order.setState(2);
        }else {
            order.setMsg("兑换成功");
            order.setState(1);
        }
        order.setUserId(JwtUtil.getUserId());
        order.setPayNumber(1);
        order.setPayType("卡密兑换");
        order.setTradeNo(req.getCardPin());
        this.save(order);
        if(order.getState()== 1){
            userService.lambdaUpdate()
                    .eq(User::getId,JwtUtil.getUserId())
                    .setSql("remaining_times = remaining_times +" + list.get(0).getNumber())
                    .update();
            cardPinService.lambdaUpdate()
                    .eq(CardPin::getCardPin,req.getCardPin())
                    .set(CardPin::getState,1)
                    .set(CardPin::getUserId,JwtUtil.getUserId())
                    .update();
        }
        return order.getState() == 1 ? B.okBuild() : B.finalBuild("兑换失败，无效卡密");
    }

    @Override
    public List<ClientOrderRes> userOrderList(Long userId) {
        return this.baseMapper.userOrderList(userId);
    }


    @Override
    public B<Page<OrderQueryRes>> query(OrderQueryReq req) {
        Page<OrderQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryOrder(page,req));
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
    private void okOrder(Order order){
        if(order.getState() == 1){
            Product product = productService.getById(order.getProductId());
            User user = userService.getById(order.getUserId());
            //次数
            user.setRemainingTimes(user.getRemainingTimes() + product.getNumberTimes() * order.getPayNumber());
            userService.updateById(user);
            product.setStock(product.getStock() - order.getPayNumber());
            productService.saveOrUpdate(product);
        }
    }
}
