package com.chat.java.model.req;

import com.chat.java.model.base.BasePageHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AnnouncementAddReq  {

    @ApiModelProperty(value = "标题")
    @NotNull(message = "标题不能为空")
    private String title;

    @ApiModelProperty(value = "内容")
    @NotNull(message = "内容不能为空")
    private String content;


    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "公告类型 1-公告、2-指南")
    @NotNull(message = "公告类型不能为空")
    private Integer type;
}
