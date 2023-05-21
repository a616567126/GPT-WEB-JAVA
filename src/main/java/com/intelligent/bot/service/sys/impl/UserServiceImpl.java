package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.UserDao;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.UserQueryPageReq;
import com.intelligent.bot.model.req.sys.admin.UserUpdateReq;
import com.intelligent.bot.model.res.sys.admin.UserQueryPageRes;
import com.intelligent.bot.service.sys.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("userService")
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {

    @Override
    public B<Page<UserQueryPageRes>> queryPage(UserQueryPageReq req) {
        Page<UserQueryPageRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryUserPage(page,req));
    }

    @Override
    public B<Void> update(UserUpdateReq req) {
        User user = BeanUtil.copyProperties(req, User.class);
        if(null != user.getPassword()){
            user.setPassword(SecureUtil.md5(req.getPassword()));
        }
        this.updateById(user);
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return B.okBuild();
    }

    @Override
    public User checkTempUser(String browserFingerprint, String ipaddress) {
        return this.baseMapper.checkTempUser(browserFingerprint,ipaddress);
    }
}
