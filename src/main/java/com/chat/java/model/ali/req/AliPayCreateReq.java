package com.chat.java.model.ali.req;

import io.swagger.annotations.ApiModelProperty;
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
public class AliPayCreateReq {

    @ApiModelProperty(value = "产品id")
    @NonNull
    private Long productId;

    @ApiModelProperty(value = "数量")
    @NonNull
    private Integer payNumber;

}
