package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
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
@TableName("use_log")
public class UseLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;
    /**
     * userId
     */
    @ApiModelProperty(value = "用户id", position = 5)
    private Long userId;

    /**
     * 使用次数
     */
    @ApiModelProperty(value = "使用次数", position = 7)
    private Integer useNumber;

    /**
     * 使用类型 1 次数 2 月卡
     */
    @ApiModelProperty(value = "使用类型 1 次数 2 月卡 3 加油包", position = 7)
    private Integer useType;

    /**
     * 聊天内容
     */
    @ApiModelProperty(value = "聊天内容", position = 7)
    private String useValue;

    @ApiModelProperty(value = "gptKey", position = 7)
    private String gptKey;

    @ApiModelProperty(value = "加油包id", position = 7)
    private Long kitId;

    @ApiModelProperty(value = "是否成功 0成功 1失败", position = 7)
    private Integer state;

    @ApiModelProperty(value = "问题", position = 6)
    private String question;

    @ApiModelProperty(value = "答案", position = 7)
    private String answer;

    @ApiModelProperty(value = "消息类型 0-正常 1-流式", position = 7)
    private Integer sendType;


}
