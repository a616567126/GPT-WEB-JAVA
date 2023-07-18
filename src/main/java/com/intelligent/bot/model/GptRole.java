package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("gpt_role")
public class GptRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 角色名
     */
    private String roleName;
    /**
     * 角色描述
     */
    private String roleDescribe;


}
