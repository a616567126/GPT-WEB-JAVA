package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class AdminHomeOrderPrice {

    private BigDecimal price;

    private String days;
}
