package com.chat.java.model.req;

import com.chat.java.model.base.BasePageHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductPageReq extends BasePageHelper {

    @ApiModelProperty("姓名")
    private Integer name;

}
