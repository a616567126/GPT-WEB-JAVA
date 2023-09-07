package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.DiscordAccountConfig;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.model.req.sys.admin.DiscordAccountConfigAdd;
import com.intelligent.bot.model.req.sys.admin.DiscordAccountConfigUpdate;

public interface IDiscordAccountConfigService extends IService<DiscordAccountConfig> {

    B<Page<DiscordAccountConfig>> queryPage(BasePageHelper req);

    B<String> add(DiscordAccountConfigAdd req);

    B<String> update(DiscordAccountConfigUpdate req);

    B<Void> delete(BaseDeleteEntity req);

    DiscordAccount addAccount(DiscordAccountConfig configAccount);
}
