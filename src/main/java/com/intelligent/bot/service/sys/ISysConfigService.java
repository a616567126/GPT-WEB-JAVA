package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SysConfigQueryRes;


public interface ISysConfigService extends IService<SysConfig> {

    B<SysConfigQueryRes>  queryPage();

    B<Void>  update(SysConfigUpdateReq req);

}
