package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("use_log")
public class UseLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "用户id", position = 5)
    private Long userId;


    @ApiModelProperty(value = "使用次数", position = 7)
    private Integer useNumber;


    @ApiModelProperty(value = "使用类型 1 次数 2 月卡 3 加油包", position = 7)
    private Integer useType;


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
