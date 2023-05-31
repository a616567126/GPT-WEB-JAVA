package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class AdminHomeRes implements Serializable {


    /**
     * 今日新增用户
     */
    private Integer dayUserNumber;
    /**
     * 今日订单数
     */
    private Integer dayOrderNumber;

    /**
     * 今日有效订单数
     */
    private Integer dayOkOrderNumber;

    /**
     * 今日收款金额
     */
    private BigDecimal dayPrice;


    private List<AdminHomeOrder> orderList;

    private List<AdminHomeOrderPrice> orderPriceList;

}
