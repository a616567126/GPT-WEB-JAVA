package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.dao.OrderDao;
import com.cn.app.chatgptbot.exception.CustomException;
import com.cn.app.chatgptbot.model.*;
import com.cn.app.chatgptbot.model.req.CreateOrderReq;
import com.cn.app.chatgptbot.model.req.OrderCallBackReq;
import com.cn.app.chatgptbot.model.req.QueryOrderReq;
import com.cn.app.chatgptbot.model.req.ReturnUrlReq;
import com.cn.app.chatgptbot.model.res.CreateOrderRes;
import com.cn.app.chatgptbot.model.res.QueryOrderRes;
import com.cn.app.chatgptbot.model.res.ReturnUrlRes;
import com.cn.app.chatgptbot.service.*;
import com.cn.app.chatgptbot.uitls.JwtUtil;
import com.cn.app.chatgptbot.uitls.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


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
        String orderPayInfo = HttpUtil.get("https://www.11zhifu.cn/api.php", map);
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

    public static void main(String[] args) {

        String url = "https://www.11zhifu.cn/";//支付地址
        String pid = "1091";//商户id
        String type = "wxpay";//支付类型
        String outTradeNo = "20160806151343353";//商户单号
        String notifyUrl = "https://401c5023.r3.cpolar.top/order/callback";//异步通知
        String returnUrl = "https://baidu.com";//跳转地址
        String name = "test";//商品名
        String money = "0.02";//价格
        String signType = "MD5";//签名类型
        String key = "1RxMSsb6WAngm49JTmw26zSG5GG9m27u";//商户密钥

        Map<String,String> sign = new HashMap<>();
        sign.put("pid",pid);
        sign.put("type",type);
        sign.put("out_trade_no",outTradeNo);
        sign.put("notify_url",notifyUrl);
        sign.put("return_url",returnUrl);
        sign.put("name",name);
        sign.put("money",money);
        sign = sortByKey(sign);
        //遍历map 转成字符串
        String signStr = "";

        for(Map.Entry<String,String> m :sign.entrySet()){
            signStr += m.getKey() + "=" +m.getValue()+"&";
        }
        //去掉最后一个 &
        signStr = signStr.substring(0,signStr.length()-1);

        //最后拼接上KEY
        signStr += key;
        //转为MD5
        signStr = DigestUtils.md5DigestAsHex(signStr.getBytes());

        sign.put("sign_type",signType);
        sign.put("sign",signStr);
        System.out.println("<form id='paying' action='"+url+"/submit.php' method='post'>");
        for(Map.Entry<String,String> m :sign.entrySet()){
            System.out.println("<input type='hidden' name='"+m.getKey()+"' value='"+m.getValue()+"'/>");
        }
        System.out.println("<input type='submit' value='正在跳转'>");
        System.out.println("</form>");
        System.out.println("<script>document.forms['paying'].submit();</script>");

    }
}
