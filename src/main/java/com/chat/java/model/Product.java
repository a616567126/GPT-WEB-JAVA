package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("product")
@ApiModel(value = "Product 对象")
public class Product extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "商品名", position = 5)
    private String name;

    @ApiModelProperty(value = "价格", position = 7)
    private BigDecimal price;

    @ApiModelProperty(value = "类型 0 次数 1 月卡 2 加油包", position = 8)
    private Integer type;

    @ApiModelProperty(value = "次数", position = 9)
    private Integer numberTimes;


    @ApiModelProperty(value = "月卡每日可使用次数", position = 10)
    private Integer monthlyNumber;

    @ApiModelProperty(value = "排序", position = 11)
    private Integer sort;

    @ApiModelProperty(value = "库存数", position = 11)
    private Integer stock;

}


