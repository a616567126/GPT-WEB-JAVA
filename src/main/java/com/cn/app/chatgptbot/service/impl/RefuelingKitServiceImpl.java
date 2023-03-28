package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ValidateException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.dao.OrderDao;
import com.cn.app.chatgptbot.dao.RefuelingKitDao;
import com.cn.app.chatgptbot.model.*;
import com.cn.app.chatgptbot.model.req.CreateOrderReq;
import com.cn.app.chatgptbot.model.req.ReturnUrlReq;
import com.cn.app.chatgptbot.model.res.CreateOrderRes;
import com.cn.app.chatgptbot.service.*;
import com.cn.app.chatgptbot.uitls.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


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
