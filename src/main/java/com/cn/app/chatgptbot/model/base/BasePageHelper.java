package com.cn.app.chatgptbot.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel(value = "BasePageHelper",description = "基础查询对象")
public class BasePageHelper implements Serializable {

    @ApiModelProperty(value = "当前页数", position = 1)
    @NotNull(message ="当前页数不能为空")
    private Integer pageNumber;

    @ApiModelProperty(value = "每页条数", position = 2)
    @NotNull(message ="每页条数不能为空")
    private Integer pageSize;

    private String name;

    private String mobile;

    private Integer state;

    private Long userId;

    private Integer type;
}
