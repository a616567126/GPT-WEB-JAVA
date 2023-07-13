package com.intelligent.bot.api.sys;


import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.req.sys.CreatePinOrderReq;
import com.intelligent.bot.model.req.sys.CreateYiOrderReq;
import com.intelligent.bot.model.req.sys.OrderYiCallBackReq;
import com.intelligent.bot.model.res.sys.CreateOrderRes;
import com.intelligent.bot.model.res.wx.NativeCallBackRes;
import com.intelligent.bot.service.sys.IOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    IOrderService orderService;

    @RequestMapping(value = "/yi/create",name = "易支付订单创建", method = RequestMethod.POST)
    public B<CreateOrderRes> createYiOrder(@Validated @RequestBody CreateYiOrderReq req) {
        return orderService.createYiOrder(req);
    }
    @RequestMapping(value = "/yi/callback",name = "易支付支付回调",method = RequestMethod.GET)
    public String yiCallBack(OrderYiCallBackReq req) {
        return orderService.yiCallBack(req);
    }

    @RequestMapping(value = "/wx/callback",name = "微信支付回调",method = RequestMethod.POST)
    public synchronized NativeCallBackRes wxCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return orderService.wxCallBack(request,response);
    }

    @RequestMapping(value = "/card/pin",name = "卡密支付", method = RequestMethod.POST)
    public B<Void> cardPin(@Validated @RequestBody CreatePinOrderReq req) {
        return orderService.cardPin(req);
    }

}
