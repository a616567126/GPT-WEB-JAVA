package com.cn.app.chatgptbot.model.base;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class BaseEntity implements Serializable {
    /**
     * 编号
     */
    @ApiModelProperty(value = "主键(修改传)", position = 1)
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
    @ApiModelProperty(value = "逻辑删除字段",hidden=true)
    private Boolean deleted;

    /**
     * 创建人编号（默认为0）
     */
    @ApiModelProperty(value = "创建人编号",hidden=true)
    private Long creator;

    /**
     * 创建时间（默认为创建时服务器时间）
     */
    @ApiModelProperty(value = "创建时间", position = 2,hidden=true)
    private LocalDateTime createTime;

    /**
     * 操作人编号（默认为0）
     */
    @ApiModelProperty(value = "操作人编号",hidden=true)
    private Long operator;

    /**
     * 操作时间（每次更新时自动更新）
     */
    @ApiModelProperty(value = "更新时间", position = 3,hidden=true)
    private LocalDateTime operateTime;
}
