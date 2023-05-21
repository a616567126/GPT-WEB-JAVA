package com.intelligent.bot.model.req.sys.admin;

import com.intelligent.bot.model.base.BasePageHelper;
import lombok.Data;

@Data
public class EmailConfigQueryReq extends BasePageHelper {


    /**
     * 邮件账号
     */
    private String username;

}
