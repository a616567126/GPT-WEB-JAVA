package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.GptRole;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.GptRoleAddReq;
import com.intelligent.bot.model.req.sys.admin.GptRoleQueryReq;
import com.intelligent.bot.model.req.sys.admin.GptRoleUpdateReq;
import com.intelligent.bot.model.res.sys.admin.GptRoleQueryRes;

import java.util.List;

public interface IGptRoleService extends IService<GptRole> {

    B<Page<GptRoleQueryRes>> queryPage(GptRoleQueryReq req);

    B<Void> add(GptRoleAddReq req);

    B<Void> update(GptRoleUpdateReq req);

    B<Void> delete(BaseDeleteEntity req);

    List<GptRoleQueryRes> getGptRoleLimit10();




}
