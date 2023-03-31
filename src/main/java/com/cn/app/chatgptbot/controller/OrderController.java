package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.CreateOrderReq;
import com.cn.app.chatgptbot.model.req.OrderCallBackReq;
import com.cn.app.chatgptbot.model.req.QueryOrderReq;
import com.cn.app.chatgptbot.model.req.ReturnUrlReq;
import com.cn.app.chatgptbot.model.res.CreateOrderRes;
import com.cn.app.chatgptbot.service.IGptKeyService;
import com.cn.app.chatgptbot.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品表(gptKey)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
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
}
