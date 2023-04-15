package com.chat.java.model.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 金额消耗列表
 */
@Data
public class DailyCost {
    /**
     * 时间戳
     */
    @JsonProperty("timestamp")
    private long timestamp;
    /**
     * 模型消耗金额详情
     */
    @JsonProperty("line_items")
    private List<LineItem> lineItems;

    /**
     * 日期
     */
    private String date;
}
