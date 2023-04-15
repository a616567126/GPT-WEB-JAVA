package com.chat.java.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ClassName:CreateOrderReq
 * Package:com.chat.java.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:03
 * @Version: v1.0
 */
@Data
public class AdminHomeRes implements Serializable {


    @ApiModelProperty(value = "今日新增用户")
    private Integer dayUserNumber;
    @ApiModelProperty(value = "今日订单数")
    private Integer dayOrderNumber;

    @ApiModelProperty(value = "今日有效订单数")
    private Integer dayOkOrderNumber;

    @ApiModelProperty(value = "今日收款金额")
    private BigDecimal dayPrice;

    @ApiModelProperty(value = "今日月卡到期用户数")
    private Integer expirationNumber;

    private List<AdminHomeOrder> orderList;

    private List<AdminHomeOrderPrice> orderPriceList;

}
