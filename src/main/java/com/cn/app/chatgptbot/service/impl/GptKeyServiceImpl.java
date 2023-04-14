package com.cn.app.chatgptbot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.dao.GptKeyDao;
import com.cn.app.chatgptbot.exception.CustomException;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.UpdateKeyStateReq;
import com.cn.app.chatgptbot.service.IGptKeyService;
import com.cn.app.chatgptbot.utils.GptUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
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
    public B queryPage(BasePageHelper basePageHelper) {
        JSONObject jsonObject = new JSONObject();
        Page<GptKey> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        QueryWrapper<GptKey> queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("id");
        Page<GptKey> gptKeyPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("gptKeyPage",gptKeyPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    @Override
    public B add(String params) {
        GptKey gptKey = JSONObject.parseObject(params, GptKey.class);
        Long count = this.lambdaQuery()
                .eq(null != gptKey.getKey(), GptKey::getKey,gptKey.getKey())
                .count();
        if(count > 0){
            return B.finalBuild("key已存在");
        }
        gptKey.setCreateTime(LocalDateTime.now());
        gptKey.setOperateTime(LocalDateTime.now());
        this.save(gptKey);
        GptUtil.add(gptKey.getKey());
        log.error("新增key：{}======缓存key信息：{}",gptKey.getKey(),GptUtil.getAllKey());
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
        GptUtil.add(gptKey.getKey());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B delete(BaseDeleteEntity params) {
        List<GptKey> list = this.lambdaQuery().in(GptKey::getId, params.getIds()).list();
        this.removeByIds(params.getIds());
        GptUtil.removeKey(list.stream().map(GptKey::getKey).collect(Collectors.toList()));
        log.error("删除：{}======缓存key信息：{}",list,GptUtil.getAllKey());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

}
