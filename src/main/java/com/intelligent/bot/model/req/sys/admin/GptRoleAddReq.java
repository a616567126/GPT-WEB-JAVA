package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class GptRoleAddReq {


    /**
     * 角色名
     */
    private String roleName;
    /**
     * 角色描述
     */
    private Integer roleDescribe;
}
