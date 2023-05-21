package com.intelligent.bot.model.gpt.billing;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionData {

    /**
     * 赠送金额：美元
     */
    @JsonProperty("hard_limit_usd")
    private BigDecimal hardLimitUsd;
}
