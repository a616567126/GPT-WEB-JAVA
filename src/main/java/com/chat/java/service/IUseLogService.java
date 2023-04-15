package com.chat.java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.model.UseLog;
import com.chat.java.model.req.ResetLogReq;
import com.chat.java.model.req.UpdateLogReq;
import com.chat.java.base.B;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IUseLogService extends IService<UseLog> {


    Integer getDayUseNumber();

    B updateLog(UpdateLogReq req);

    B resetLog(ResetLogReq req);


}
