package com.cn.app.chatgptbot.model.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * ClassName:CreditGrantsResponse
 * Package:com.cn.app.chatgptbot.model
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/23 - 16:59
 * @Version: v1.0
 */
@Data
public class CreditGrantsResponse {
    private String object;
    /**
     * 总金额：美元
     */
    @JsonProperty("total_granted")
    private BigDecimal totalGranted;
    /**
     * 总使用金额：美元
     */
    @JsonProperty("total_used")
    private BigDecimal totalUsed;
    /**
     * 总剩余金额：美元
     */
    @JsonProperty("total_available")
    private BigDecimal totalAvailable;
    /**
     * 余额明细
     */
    private Grants grants;
}
