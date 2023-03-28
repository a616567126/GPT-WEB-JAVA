package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("refueling_kit")
public class RefuelingKit extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;


    /**
     * 产品id
     */
    @ApiModelProperty(value = "产品id", position = 5)
    private Long productId;


    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", position = 7)
    private Long userId;

    /**
     * 可使用次数
     */
    @ApiModelProperty(value = "可使用次数", position = 7)
    private Integer numberTimes;

}
