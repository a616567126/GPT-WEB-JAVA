package com.chat.java.bing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BingChatReq {

    @ApiModelProperty("问题")
    private String prompt;


    @ApiModelProperty("h3precise -- 准确模式 h3imaginative -- 创造模式 harmonyv3 -- 均衡模式")
    private String mode;




}
