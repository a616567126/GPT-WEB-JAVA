package com.intelligent.bot.api.midjourney.loadbalancer.rule;


import com.intelligent.bot.api.midjourney.loadbalancer.DiscordInstance;

import java.util.List;

public interface IRule {

	DiscordInstance choose(List<DiscordInstance> instances);
}
