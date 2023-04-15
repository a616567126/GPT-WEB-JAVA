package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * ClassName:QueryOrderReq
 * Package:com.chat.java.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/27 - 08:19
 * @Version: v1.0
 */

@Data
public class QueryOrderReq {

    @ApiModelProperty(value = "当前页数", position = 1)
    @NotNull(message ="当前页数不能为空")
    private Integer pageNumber;

    @ApiModelProperty(value = "每页条数", position = 2)
    @NotNull(message ="每页条数不能为空")
    private Integer pageSize;

    private String mobile;

    private Long id;

    private Integer state;


}
