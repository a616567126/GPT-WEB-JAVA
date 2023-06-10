package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sd_lora")
public class SdLora extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * lora名
     */
    private String loraName;

    /**
     * lora图片地址
     */
    private String imgUrl;


}
