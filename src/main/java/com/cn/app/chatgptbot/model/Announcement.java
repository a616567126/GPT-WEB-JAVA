package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户表(User)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("announcement")
public class Announcement extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;
    /**
     * key
     */
    @ApiModelProperty(value = "标题", position = 5)
    private String title;
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容", position = 7)
    private String content;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", position = 7)
    private Integer sort;

    /**
     * 公告类型 1-公告、2-指南
     */
    @ApiModelProperty(value = "公告类型 1-公告、2-指南", position = 7)
    private Integer type;

}
