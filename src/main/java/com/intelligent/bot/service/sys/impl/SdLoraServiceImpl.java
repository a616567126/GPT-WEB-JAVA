package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.SdLoraDao;
import com.intelligent.bot.model.SdLora;
import com.intelligent.bot.service.sys.ISdLoraService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service("SdLoraService")
@Log4j2
public class SdLoraServiceImpl extends ServiceImpl<SdLoraDao, SdLora> implements ISdLoraService {

}
