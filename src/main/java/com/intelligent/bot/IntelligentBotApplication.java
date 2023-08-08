package com.intelligent.bot;

import com.intelligent.bot.config.BeanConfig;
import com.intelligent.bot.utils.sys.IDUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@SpringBootApplication
@MapperScan("com.intelligent.bot.dao")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@Import({BeanConfig.class})
public class IntelligentBotApplication {

	/**
	 * 初始化ID获取方法，共全局使用
	 */
	public static IDUtil idUtil = new IDUtil(1, 0);
	public static void main(String[] args) {
		SpringApplication.run(IntelligentBotApplication.class, args);
	}

}
