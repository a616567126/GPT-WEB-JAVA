package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserAddReq {

    @ApiModelProperty(value = "姓名")
    @NotNull(message = "姓名不能为空")
    private String name;

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "默认次数")
    @NotNull(message = "默认次数不能为空")
    private Integer remainingTimes;
}
