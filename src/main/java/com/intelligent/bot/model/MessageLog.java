package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.*;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("message_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 使用次数
     */
    private Integer useNumber;

    /**
     * 使用类型 1 次数 2 月卡
     */
    private Integer useType;

    /**
     * 聊天内容
     */
    private String useValue;

    /**
     * 所用使用的key
     */
    private String gptKey;

    /**
     * 消息类型  1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing 7-stableStudio 8-gpt4 9-spark-v2 10-spark-v3 11-spark-v3.5
     */
    private Integer sendType;


}
