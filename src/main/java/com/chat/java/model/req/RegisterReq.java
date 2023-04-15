package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

/**
 * ClassName:RegisterReq
 * Package:com.chat.java.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:56
 * @Version: v1.0
 */
@Data
public class RegisterReq {

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
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", position = 8)
    @NonNull
    private String password;
    @ApiModelProperty(value = "短信验证码", position = 8)
    @NonNull
    private String msgCode;
}
