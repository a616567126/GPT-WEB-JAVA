package com.chat.java.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.model.WxLog;
import com.chat.java.dao.WxLogDao;
import com.chat.java.service.IWxLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * ClassName:WxServiceImpl
 * Package:com.chat.java.service.impl
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/31 - 16:43
 * @Version: v1.0
 */
@Service("WxLogService")
@Log4j2
public class WxLogServiceImpl extends ServiceImpl<WxLogDao, WxLog> implements IWxLogService {

}
