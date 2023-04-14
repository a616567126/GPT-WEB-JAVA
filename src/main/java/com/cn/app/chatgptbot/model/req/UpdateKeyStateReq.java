package com.cn.app.chatgptbot.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * ClassName:UpdateKeyStateReq
 * Package:com.cn.app.chatgptbot.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/4/14 - 16:46
 * @Version: v1.0
 */
@Data
public class UpdateKeyStateReq {

    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;


    @ApiModelProperty(value = "状态 0 启用 1禁用")
    @NotNull
    private Integer state;
}
