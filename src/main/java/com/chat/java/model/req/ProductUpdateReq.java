package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class ProductUpdateReq implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "id")
    @NotNull(message = "id 不能为空")
    private Long id;

    @ApiModelProperty(value = "商品名")
    private String name;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "类型 0 次数 1 月卡 2 加油包")
    private Integer type;

    @ApiModelProperty(value = "次数")
    private Integer numberTimes;


    @ApiModelProperty(value = "月卡每日可使用次数")
    private Integer monthlyNumber;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "库存数")
    private Integer stock;

}


