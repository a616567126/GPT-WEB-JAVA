package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GptRoleQueryRes {


    /**
     * id
     */
    private Long id;


    /**
     * 角色名
     */
    private String roleName;
    /**
     * 角色描述
     */
    private String roleDescribe;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
