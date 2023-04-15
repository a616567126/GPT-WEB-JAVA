package com.chat.java.model.req;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;



@Data
public class ProductAddReq  implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "商品名")
    @NotNull(message = "商品名不能为空")
    private String name;

    @ApiModelProperty(value = "价格")
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @ApiModelProperty(value = "类型 0 次数 1 月卡 2 加油包")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "次数")
    private Integer numberTimes;


    @ApiModelProperty(value = "月卡每日可使用次数")
    private Integer monthlyNumber;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "库存数")
    @NotNull(message = "库存不能为空")
    private Integer stock;

}


