package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("announcement")
public class Announcement extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "标题", position = 5)
    private String title;

    @ApiModelProperty(value = "内容", position = 7)
    private String content;


    @ApiModelProperty(value = "排序", position = 7)
    private Integer sort;


    @ApiModelProperty(value = "公告类型 1-公告、2-指南", position = 7)
    private Integer type;

}
