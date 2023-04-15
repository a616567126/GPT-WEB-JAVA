package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

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
public class OrderCallBackReq {



    @ApiModelProperty(value = "商户id")
    private Integer pid;

    @ApiModelProperty(value = "易支付订单号")
    private String trade_no;

    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;

    @ApiModelProperty(value = "支付方式")
    private String type;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品金额")
    private String money;

    @ApiModelProperty(value = "支付状态只有TRADE_SUCCESS是成功")
    private String trade_status;

    @ApiModelProperty(value = "业务扩展参数")
    private String param;

    @ApiModelProperty(value = "签名字符串")
    private String sign;

    @ApiModelProperty(value = "签名类型")
    private String sign_type;

}
