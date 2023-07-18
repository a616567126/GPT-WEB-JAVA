package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.GptRoleDao;
import com.intelligent.bot.model.GptRole;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.GptRoleAddReq;
import com.intelligent.bot.model.req.sys.admin.GptRoleQueryReq;
import com.intelligent.bot.model.req.sys.admin.GptRoleUpdateReq;
import com.intelligent.bot.model.res.sys.admin.GptRoleQueryRes;
import com.intelligent.bot.service.sys.IGptRoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service("GptRoleService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class GptRoleServiceImpl extends ServiceImpl<GptRoleDao, GptRole> implements IGptRoleService {

    @Override
    public B<Page<GptRoleQueryRes>> queryPage(GptRoleQueryReq req) {
        Page<GptRoleQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryGptRole(page,req.getRoleName()));
    }

    @Override
    public B<Void> add(GptRoleAddReq req) {
        GptRole gptRole = BeanUtil.copyProperties(req, GptRole.class);
        Long count = this.lambdaQuery()
                .eq(null != gptRole.getRoleName(), GptRole::getRoleName, gptRole.getRoleName())
                .count();
        if (count > 0) {
            return B.finalBuild("角色已存在");
        }
        gptRole.setCreateTime(LocalDateTime.now());
        gptRole.setOperateTime(LocalDateTime.now());
        this.save(gptRole);
        return B.okBuild();

    }

    @Override
    public B<Void> update(GptRoleUpdateReq req) {
        GptRole gptRole = BeanUtil.copyProperties(req, GptRole.class);
        Long count = this.lambdaQuery()
                .eq(null != gptRole.getRoleName(), GptRole::getRoleName, gptRole.getRoleName())
                .ne(GptRole::getId,req.getId())
                .count();
        if (count > 0) {
            return B.finalBuild("角色已存在");
        }
        gptRole.setCreateTime(LocalDateTime.now());
        gptRole.setOperateTime(LocalDateTime.now());
        this.saveOrUpdate(gptRole);
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return B.okBuild();
    }

    @Override
    public List<GptRoleQueryRes> getGptRoleLimit10() {
        return baseMapper.getGptRoleLimit10();
    }
}
