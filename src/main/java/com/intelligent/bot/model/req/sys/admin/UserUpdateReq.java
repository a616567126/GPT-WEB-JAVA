package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;


@Data
public class UserUpdateReq {


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
     * 剩余次数
     */
    private Integer remainingTimes;

    /**
     * id
     */
    private Long id;

}
