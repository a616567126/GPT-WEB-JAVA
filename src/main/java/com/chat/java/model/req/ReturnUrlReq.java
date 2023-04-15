package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

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
public class ReturnUrlReq {



    @ApiModelProperty(value = "订单id")
    @NotNull
    private Long orderId;

}
