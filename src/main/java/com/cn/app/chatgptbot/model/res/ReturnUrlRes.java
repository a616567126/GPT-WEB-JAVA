package com.cn.app.chatgptbot.model.res;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ClassName:CreateOrderReq
 * Package:com.cn.app.chatgptbot.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:03
 * @Version: v1.0
 */
@Data
public class ReturnUrlRes  implements Serializable {



    @ApiModelProperty(value = "返回状态码 1为成功，其它值为失败")
    private Integer code;

    @ApiModelProperty(value = "返回信息")
    private String msg;

    @ApiModelProperty(value = "易支付订单号")
    private String trade_no;

    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;

    @ApiModelProperty(value = "支付方式")
    private String type;

    @ApiModelProperty(value = "商户ID")
    private Integer pid;

    @ApiModelProperty(value = "创建订单时间")
    private String addtime;

    @ApiModelProperty(value = "完成交易时间")
    private String endtime;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品金额")
    private BigDecimal money;

    @ApiModelProperty(value = "支付状态")
    private Integer status;

    @ApiModelProperty(value = "业务扩展参数")
    private String param;

    @ApiModelProperty(value = "支付者账号")
    private String buyer;

}
