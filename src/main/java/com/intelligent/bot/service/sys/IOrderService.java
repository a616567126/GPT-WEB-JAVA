package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.Order;
import com.intelligent.bot.model.req.sys.CreatePinOrderReq;
import com.intelligent.bot.model.req.sys.CreateYiOrderReq;
import com.intelligent.bot.model.req.sys.OrderYiCallBackReq;
import com.intelligent.bot.model.req.sys.OrderYiReturnReq;
import com.intelligent.bot.model.req.sys.admin.OrderQueryReq;
import com.intelligent.bot.model.res.sys.ClientOrderRes;
import com.intelligent.bot.model.res.sys.CreateOrderRes;
import com.intelligent.bot.model.res.sys.admin.OrderQueryRes;
import com.intelligent.bot.model.res.wx.NativeCallBackRes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface IOrderService extends IService<Order> {



    B<CreateOrderRes> createYiOrder(CreateYiOrderReq req);



    String yiCallBack(OrderYiCallBackReq req);

    NativeCallBackRes wxCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception;


    B<Void> yiReturnUrl(OrderYiReturnReq req);


    B<Void> cardPin(CreatePinOrderReq req);


    List<ClientOrderRes> userOrderList(Long userId);


    B<Page<OrderQueryRes>> query(OrderQueryReq req);



}
