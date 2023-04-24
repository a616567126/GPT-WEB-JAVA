package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.exception.CustomException;
import com.chat.java.model.GptKey;
import com.chat.java.model.req.GptKeyAddReq;
import com.chat.java.model.req.UpdateKeyStateReq;
import com.chat.java.utils.InitUtil;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.GptKeyDao;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.service.IGptKeyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(Product)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("GptKeyService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class GptKeyServiceImpl extends ServiceImpl<GptKeyDao, GptKey> implements IGptKeyService {




    @Override
    public B<JSONObject> queryPage(BasePageHelper basePageHelper) {
        JSONObject jsonObject = new JSONObject();
        Page<GptKey> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        QueryWrapper<GptKey> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<GptKey> gptKeyPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("gptKeyPage",gptKeyPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    @Override
    public B add(GptKeyAddReq req) {
        GptKey gptKey = BeanUtil.copyProperties(req, GptKey.class);
        Long count = this.lambdaQuery()
                .eq(null != gptKey.getKey(), GptKey::getKey,gptKey.getKey())
                .count();
        if(count > 0){
            return B.finalBuild("key已存在");
        }
        gptKey.setCreateTime(LocalDateTime.now());
        gptKey.setOperateTime(LocalDateTime.now());
        this.save(gptKey);
        InitUtil.add(gptKey.getKey());
        this.save(gptKey);
        log.error("新增key：{}======缓存key信息：{}",gptKey.getKey(), InitUtil.getAllKey());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B updateState(UpdateKeyStateReq params) {
        GptKey gptKey = this.getById(params.getId());
        if(null == gptKey){
            throw new CustomException("key不存在");
        }
        gptKey.setState(gptKey.getState());
        this.saveOrUpdate(gptKey);
        InitUtil.add(gptKey.getKey());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B delete(BaseDeleteEntity params) {
        List<GptKey> list = this.lambdaQuery().in(GptKey::getId, params.getIds()).list();
        this.removeByIds(params.getIds());
        InitUtil.removeKey(list.stream().map(GptKey::getKey).collect(Collectors.toList()));
        log.error("删除：{}======缓存key信息：{}",list, InitUtil.getAllKey());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

}
