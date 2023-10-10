package com.intelligent.bot.model.res.sys;

import lombok.Data;

@Data
public class UserAuthRes {

    /**
     * 用户授权token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户剩余次数
     */
    private Integer expirationTime;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String mobile;

}
