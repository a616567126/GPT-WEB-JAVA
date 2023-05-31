package com.intelligent.bot.model.res.stablestudio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountRes {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户组织列表
     */
    private List<organization> organizations;

    /**
     * 头像
     */
    @JsonProperty("profile_picture")
    private String profilePicture;


}
@Data
class organization{

    /**
     * 用户id
     */
    private String id;

    /**
     * 是否默认
     */
    @JsonProperty("is_default")
    private boolean isDefault;

    /**
     * 用户名
     */
    private String name;

    /**
     * 角色
     */
    private String role;
}