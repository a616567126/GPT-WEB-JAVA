package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class UserHomeReq {


    @ApiModelProperty(value = "消息类型 0-gpt正常 1-gpt流式 2bing流式", position = 5)
    @NotNull(message = "类型不能为空")
    private Integer sendType;
}
