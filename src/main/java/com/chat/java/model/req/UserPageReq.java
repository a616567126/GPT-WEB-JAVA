package com.chat.java.model.req;

import com.chat.java.model.base.BasePageHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageReq extends BasePageHelper {

    @ApiModelProperty("姓名")
    private Integer name;

    @ApiModelProperty("手机号")
    private Integer mobile;
}
