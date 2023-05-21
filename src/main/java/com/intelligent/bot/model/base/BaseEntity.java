package com.intelligent.bot.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class BaseEntity implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 数据版本（默认为0，每次编辑+1）
     */
    @Version
    private Integer dataVersion;

    /**
     * 是否删除：0-否、1-是
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 创建人编号（默认为0）
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;

    /**
     * 创建时间（默认为创建时服务器时间）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 操作人编号（默认为0）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long operator;

    /**
     * 操作时间（每次更新时自动更新）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime operateTime;
}
