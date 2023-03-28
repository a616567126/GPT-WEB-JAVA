package com.cn.app.chatgptbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.app.chatgptbot.model.Order;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.req.QueryOrderReq;
import com.cn.app.chatgptbot.model.res.AdminHomeOrder;
import com.cn.app.chatgptbot.model.res.AdminHomeOrderPrice;
import com.cn.app.chatgptbot.model.res.OrderUserRes;
import com.cn.app.chatgptbot.model.res.QueryOrderRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderDao extends BaseMapper<Order> {


    List<OrderUserRes> getUserOrderList(@Param("userId") Long userId);


    Page<QueryOrderRes> queryOrder(Page<QueryOrderRes> page, @Param("req") QueryOrderReq req);

    List<AdminHomeOrder> queryHomeOrder();

    List<AdminHomeOrderPrice> queryHomeOrderPrice();

}
