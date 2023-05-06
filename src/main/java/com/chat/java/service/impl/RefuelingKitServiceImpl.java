package com.chat.java.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.model.RefuelingKit;
import com.chat.java.service.IRefuelingKitService;
import com.chat.java.utils.JwtUtil;
import com.chat.java.dao.RefuelingKitDao;
import com.chat.java.model.*;
import com.chat.java.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("RefuelingKitService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class RefuelingKitServiceImpl extends ServiceImpl<RefuelingKitDao, RefuelingKit> implements IRefuelingKitService {

    @Override
    public Long getUserKitId(Long userId) {
        Long kitId = this.baseMapper.getUserKitId(userId);
        return  kitId == null ? 0 : kitId;
    }
}
