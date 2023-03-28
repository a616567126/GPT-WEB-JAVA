package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表(User)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user")
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", position = 5)
    private String name;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", position = 7)
    private String mobile;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", position = 8)
    private String password;
    /**
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间", position = 9)
    private LocalDateTime lastLoginTime;

    /**
     * 类型 0 次数用户 1 月卡用户
     */
    @ApiModelProperty(value = "类型 0 次数用户 1 月卡用户", position = 10)
    private Integer type;

    /**
     * 月卡到期日期
     */
    @ApiModelProperty(value = "月卡到期日期", position = 11)
    private LocalDateTime expirationTime;

    /**
     * 剩余次数
     */
    @ApiModelProperty(value = "剩余次数", position = 12)
    private Integer remainingTimes;


    @ApiModelProperty(value = "月卡当日使用最大次数", position = 12)
    private Integer cardDayMaxNumber;

}
