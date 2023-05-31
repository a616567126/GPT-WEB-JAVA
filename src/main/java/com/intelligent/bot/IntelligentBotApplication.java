package com.intelligent.bot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@SpringBootApplication
@MapperScan("com.intelligent.bot.dao")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class IntelligentBotApplication {


	public static void main(String[] args) {
		SpringApplication.run(IntelligentBotApplication.class, args);
	}

}
