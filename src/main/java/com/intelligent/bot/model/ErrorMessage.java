package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.*;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("error_message")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 异常内容
     */
    private String errorMessage;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 异常位置
     */
    private String position;


}
