package com.intelligent.bot.model.res.gpt;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GptBillingRes {

    /**
     * 总额度
     */
    private BigDecimal hardLimitUsd;

    /**
     * 已用额度
     */
    private BigDecimal totalUsage;

    /**
     * 使用信息
     */
    private List<LineItems> lineItems;


}
