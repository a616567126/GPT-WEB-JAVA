package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class ProductQueryRes implements Serializable {

    /**
     * id
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
     * 排序
     */
    private Integer sort;

    /**
     * 库存数
     */
    private Integer stock;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}


