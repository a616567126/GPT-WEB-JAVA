package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user")
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "姓名", position = 5)
    private String name;

    @ApiModelProperty(value = "手机号", position = 7)
    private String mobile;

    @ApiModelProperty(value = "密码", position = 8)
    private String password;

    @ApiModelProperty(value = "上次登录时间", position = 9)
    private LocalDateTime lastLoginTime;


    @ApiModelProperty(value = "类型 0 次数用户 1 月卡用户", position = 10)
    private Integer type;


    @ApiModelProperty(value = "月卡到期日期", position = 11)
    private LocalDateTime expirationTime;


    @ApiModelProperty(value = "剩余次数", position = 12)
    private Integer remainingTimes;


    @ApiModelProperty(value = "月卡当日使用最大次数", position = 12)
    private Integer cardDayMaxNumber;

    @ApiModelProperty(value = "微信用户账号", position = 14)
    private String fromUserName;

    @ApiModelProperty(value = "是否关注公众号 0未关注 1关注", position = 14)
    private Integer isEvent;

}
