package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("gpt_key")
public class GptKey extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "key", position = 5)
    @TableField("`key`")
    private String key;

    @ApiModelProperty(value = "使用次数", position = 7)
    private Integer useNumber;

    @ApiModelProperty(value = "排序", position = 8)
    private Integer sort;

    @ApiModelProperty(value = "状态 0 启用 1禁用", position = 8)
    private Integer state;

}
