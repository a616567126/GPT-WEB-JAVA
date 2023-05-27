package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sd_model")
public class SdModel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 模型名
     */
    private String modelName;

    /**
     * 模型图片地址
     */
    private String imgUrl;


}
