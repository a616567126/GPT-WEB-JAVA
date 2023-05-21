package com.intelligent.bot.model.req.sys.admin;

import com.intelligent.bot.model.base.BasePageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class OrderQueryReq extends BasePageHelper {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 订单状态
     */
    private Integer state;


}
