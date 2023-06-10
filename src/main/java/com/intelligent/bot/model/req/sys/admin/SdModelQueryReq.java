package com.intelligent.bot.model.req.sys.admin;

import com.intelligent.bot.model.base.BasePageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SdModelQueryReq extends BasePageHelper {


    /**
     * 模型名
     */
    private String modelName;


}


