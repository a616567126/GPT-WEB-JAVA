package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.Order;
import com.intelligent.bot.model.req.sys.admin.OrderQueryReq;
import com.intelligent.bot.model.res.sys.ClientOrderRes;
import com.intelligent.bot.model.res.sys.admin.AdminHomeOrder;
import com.intelligent.bot.model.res.sys.admin.AdminHomeOrderPrice;
import com.intelligent.bot.model.res.sys.admin.OrderQueryRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderDao extends BaseMapper<Order> {


    List<ClientOrderRes> userOrderList(@Param("userId") Long userId);


    Page<OrderQueryRes> queryOrder(Page<OrderQueryRes> page, @Param("req") OrderQueryReq req);

    List<AdminHomeOrder> queryHomeOrder();

    List<AdminHomeOrderPrice> queryHomeOrderPrice();


}
