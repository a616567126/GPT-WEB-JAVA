package com.chat.java.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.java.model.Order;
import com.chat.java.model.req.QueryOrderReq;
import com.chat.java.model.res.AdminHomeOrder;
import com.chat.java.model.res.OrderUserRes;
import com.chat.java.model.res.QueryOrderRes;
import com.chat.java.model.res.AdminHomeOrderPrice;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderDao extends BaseMapper<Order> {


    List<OrderUserRes> getUserOrderList(@Param("userId") Long userId);


    Page<QueryOrderRes> queryOrder(Page<QueryOrderRes> page, @Param("req") QueryOrderReq req);

    List<AdminHomeOrder> queryHomeOrder();

    List<AdminHomeOrderPrice> queryHomeOrderPrice();

}
