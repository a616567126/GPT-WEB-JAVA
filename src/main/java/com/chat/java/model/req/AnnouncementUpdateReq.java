package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnnouncementUpdateReq {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;


    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "公告类型 1-公告、2-指南")
    private Integer type;
}
