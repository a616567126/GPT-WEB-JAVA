package com.chat.java.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author  
 * @date 2022-03-25 16:01
 */
@Data
@ApiModel(value = "用户登录对象")
public class UserLogin {

    @NotEmpty(message = "手机号")
    @ApiModelProperty(value = "手机号", position = 1)
    private String mobile;

    @NotEmpty(message = "密码")
    @ApiModelProperty(value = "密码", position = 1)
    private String password;
}
