package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户表(Product)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("product")
@ApiModel(value = "Product 对象")
public class Product extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;
    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名", position = 5)
    private String name;
    /**
     * 价格
     */
    @ApiModelProperty(value = "价格", position = 7)
    private BigDecimal price;
    /**
     * 类型 0 次数 1 月卡 2 加油包
     */
    @ApiModelProperty(value = "类型 0 次数 1 月卡 2 加油包", position = 8)
    private Integer type;
    /**
     * 次数
     */
    @ApiModelProperty(value = "次数", position = 9)
    private Integer numberTimes;

    /**
     * 月卡每日可使用次数
     */
    @ApiModelProperty(value = "月卡每日可使用次数", position = 10)
    private Integer monthlyNumber;


    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", position = 11)
    private Integer sort;

    @ApiModelProperty(value = "库存数", position = 11)
    private Integer stock;

}


