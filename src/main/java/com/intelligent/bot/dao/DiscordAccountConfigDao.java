package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.DiscordAccountConfig;

public interface DiscordAccountConfigDao extends BaseMapper<DiscordAccountConfig> {

    Page<DiscordAccountConfig> queryPage(Page<DiscordAccountConfig> page);

}
