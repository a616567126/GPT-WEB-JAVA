package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.SdModelDao;
import com.intelligent.bot.model.SdModel;
import com.intelligent.bot.service.sys.ISdModelService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service("SdModelService")
@Log4j2
public class SdModelServiceImpl extends ServiceImpl<SdModelDao, SdModel> implements ISdModelService {

}
