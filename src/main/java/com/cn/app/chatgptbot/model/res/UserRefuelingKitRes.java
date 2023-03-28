package com.cn.app.chatgptbot.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class UserRefuelingKitRes implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;



    @ApiModelProperty(value = "加油包名称", position = 5)
    private String productName;


    @ApiModelProperty(value = "可使用次数", position = 7)
    private Integer numberTimes;


    @ApiModelProperty(value = "过期时间", position = 7)
    private LocalDateTime expirationDateTime;

    @ApiModelProperty(value = "已使用次数", position = 7)
    private Integer useNumber;

    @ApiModelProperty(value = "状态 0正常 1过期", position = 7)
    private Integer state;

}
