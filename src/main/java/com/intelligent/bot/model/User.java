package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user")
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 密码
     */
    private String password;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 类型 0 次数用户 1 月卡用户 2临时用户
     */
    private Integer type;

    /**
     * 剩余次数
     */
    private Integer remainingTimes;

    /**
     * 微信用户账号
     */
    private String fromUserName;

    /**
     * 是否关注公众号 0未关注 1关注
     */
    private Integer isEvent;

    /**
     * 邮件地址
     */
    private String email;

    /**
     * 用户浏览器指纹
     */
    private String browserFingerprint;

    /**
     * 用户ip地址
     */
    private String ipAddress;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 是否手机端登录默认客户端登录
     */
    @TableField(exist = false)
    private Integer isMobile = 0;



}
