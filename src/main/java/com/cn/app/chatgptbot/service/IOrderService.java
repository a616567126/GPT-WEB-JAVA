package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.Order;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.CreateOrderReq;
import com.cn.app.chatgptbot.model.req.OrderCallBackReq;
import com.cn.app.chatgptbot.model.req.QueryOrderReq;
import com.cn.app.chatgptbot.model.req.ReturnUrlReq;
import com.cn.app.chatgptbot.model.res.CreateOrderRes;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户表(User)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IOrderService extends IService<Order> {


    B<CreateOrderRes> createOrder(CreateOrderReq req);

    B returnUrl(ReturnUrlReq req);

    String callback(OrderCallBackReq req);

    B query(QueryOrderReq req);





}
