package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("card_pin")
public class CardPin extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 卡密
     */
    private String cardPin;

    /**
     * 次数
     */
    private Integer number;

    /**
     * 使用用户id
     */
    private Long userId;

    /**
     * 状态 0 未使用 1使用
     */
    private Integer state;


}
