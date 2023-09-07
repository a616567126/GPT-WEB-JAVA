package com.intelligent.bot.config.mj;

import com.intelligent.bot.model.DiscordAccountConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "mj")
public class MjProperties {

    /**
     * discord账号选择规则.
     */
    private String accountChooseRule = "BestWaitIdleRule";
    /**
     * discord单账号配置.
     */
    private final DiscordAccountConfig discord = new DiscordAccountConfig();
    /**
     * discord账号池配置.
     */
    private final List<DiscordAccountConfig> accounts = new ArrayList<>();
}
