package com.cn.app.chatgptbot.uitls;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @author JiangZhi
 * @date 2021/7/8 10:37
 */
@Configuration
public class RedissonConfig {
    @Value("${spring.profiles.active}")
    private String active;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if ("dev".equals(active)) {
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6380")
                    .setPassword("123456")
                    .setDatabase(0);
        } else {
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6380")
                    .setPassword("123456")
                    .setDatabase(0);
        }
        return Redisson.create(config);
    }
}
