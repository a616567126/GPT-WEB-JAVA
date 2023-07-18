package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class GptRoleUpdateReq {


    /**
     * 角色名
     */
    private String roleName;
    /**
     * 角色描述
     */
    private String roleDescribe;

    private Long id;
}
