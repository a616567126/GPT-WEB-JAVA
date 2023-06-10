package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.SdModel;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.SdModelAddReq;
import com.intelligent.bot.model.req.sys.admin.SdModelQueryReq;
import com.intelligent.bot.model.req.sys.admin.SdModelUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SdModelQueryRes;


public interface ISdModelService extends IService<SdModel> {

    B<Page<SdModelQueryRes>>  queryModelPage(SdModelQueryReq req);

    B<Void> addModel(SdModelAddReq req);

    B<Void> updateModel(SdModelUpdateReq req);

    B<Void> deleteModel(BaseDeleteEntity req);


}
