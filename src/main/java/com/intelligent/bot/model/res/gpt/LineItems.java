package com.intelligent.bot.model.res.gpt;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineItems {

    /**
     * 使用时间
     */
    private String name;

    /**
     * 使用额度
     */
    private BigDecimal cost;
}
