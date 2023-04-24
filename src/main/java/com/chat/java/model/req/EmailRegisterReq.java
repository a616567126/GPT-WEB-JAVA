package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;


@Data
public class EmailRegisterReq {

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", position = 5)
    @NonNull
    private String name;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", position = 7)
    @NonNull
    private String mobile;

    @ApiModelProperty(value = "邮件验证码", position = 8)
    @NonNull
    private String emailCode;

    @ApiModelProperty(value = "邮件", position = 8)
    @NonNull
    private String email;
}
