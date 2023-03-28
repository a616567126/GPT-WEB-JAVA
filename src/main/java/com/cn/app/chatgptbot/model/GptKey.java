package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表(User)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("gpt_key")
public class GptKey extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;
    /**
     * key
     */
    @ApiModelProperty(value = "key", position = 5)
    @TableField("`key`")
    private String key;
    /**
     * 使用次数
     */
    @ApiModelProperty(value = "使用次数", position = 7)
    private Integer useNumber;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", position = 8)
    private Integer sort;

    @ApiModelProperty(value = "状态 0 启用 1禁用", position = 8)
    private Integer state;

}
