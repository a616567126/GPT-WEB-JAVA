package com.cn.app.chatgptbot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.dao.RefuelingKitDao;
import com.cn.app.chatgptbot.model.*;
import com.cn.app.chatgptbot.service.*;
import com.cn.app.chatgptbot.utils.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("RefuelingKitService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class RefuelingKitServiceImpl extends ServiceImpl<RefuelingKitDao, RefuelingKit> implements IRefuelingKitService {

    @Override
    public Long getUserKitId() {
        Long kitId = this.baseMapper.getUserKitId(JwtUtil.getUserId());
        return  kitId == null ? 0 : kitId;
    }
}
