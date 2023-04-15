package com.chat.java.controller;

import com.chat.java.model.ali.req.AliPayCreateReq;
import com.chat.java.model.res.CreateOrderRes;
import com.chat.java.base.B;
import com.chat.java.model.req.CreateOrderReq;
import com.chat.java.model.req.OrderCallBackReq;
import com.chat.java.model.req.QueryOrderReq;
import com.chat.java.model.req.ReturnUrlReq;
import com.chat.java.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Api(tags = {"订单管理"})
public class OrderController {


    /**
     * gptKeyService
     */
    final IOrderService orderService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiOperation(value = "创建预订单")
    public B<CreateOrderRes> createOrder(@Validated @RequestBody CreateOrderReq req) {
        return orderService.createOrder(req);
    }


    @RequestMapping(value = "/return/url", method = RequestMethod.POST)
    @ApiOperation(value = "支付订单查询")
    public B returnUrl(@Validated @RequestBody ReturnUrlReq req) {
        return orderService.returnUrl(req);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    @ApiOperation(value = "支付回调")
    public String callback(OrderCallBackReq req) {
        return orderService.callback(req);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value = "查询订单列表")
    public B query(@Validated @RequestBody QueryOrderReq req) {
        return orderService.query(req);
    }

    @RequestMapping(value = "/ali/create", method = RequestMethod.POST)
    @ApiOperation(value = "支付宝创建预订单")
    public synchronized  B<String> aliCreateOrder(@Validated @RequestBody AliPayCreateReq req) throws Exception {
        return orderService.aliCreateOrder(req);
    }

    @RequestMapping(value = "/ali/callBack")
    @ApiOperation(value = "支付宝支付回调")
    public synchronized String aliCallBack(HttpServletRequest request) throws Exception {
        return orderService.aliCallBack(request);
    }
}
