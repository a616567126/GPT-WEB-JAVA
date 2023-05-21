package com.intelligent.bot.model.res.sys;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientProductRes {

    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品名
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 次数
     */
    private Integer numberTimes;

    /**
     * 库存数
     */
    private Integer stock;

}
