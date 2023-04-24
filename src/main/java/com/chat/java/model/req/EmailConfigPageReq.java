package com.chat.java.model.req;

import com.chat.java.model.base.BasePageHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmailConfigPageReq extends BasePageHelper {

    @ApiModelProperty("邮件账号")
    private Integer username;

}
