package com.intelligent.bot.api.sys.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.req.sys.OrderYiReturnReq;
import com.intelligent.bot.model.req.sys.admin.OrderQueryReq;
import com.intelligent.bot.model.res.sys.admin.OrderQueryRes;
import com.intelligent.bot.service.sys.IOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/order")
public class SysOrderController {

    @Resource
    IOrderService orderService;

    @RequestMapping(value = "/query",name = "查询订单列表", method = RequestMethod.POST)
    public B<Page<OrderQueryRes>> query(@Validated @RequestBody OrderQueryReq req) {
        return orderService.query(req);
    }
    @RequestMapping(value = "/sys/yi/return/url",name = "易支付订单查询", method = RequestMethod.POST)
    public B<Void> yiReturnUrl(@Validated @RequestBody OrderYiReturnReq req) {
        return orderService.yiReturnUrl(req);
    }
}
