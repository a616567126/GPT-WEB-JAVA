package com.chat.java.model.billing;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 模型消耗金额详情
 */
@Data
public class LineItem {
    /**
     * 模型名称
     */
    private String name;
    /**
     * 消耗金额
     */
    private BigDecimal cost;
}
