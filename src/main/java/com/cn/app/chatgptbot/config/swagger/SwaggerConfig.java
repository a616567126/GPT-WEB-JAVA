package com.cn.app.chatgptbot.config.swagger;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@EnableSwagger2WebMvc
@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Value("${spring.profiles.active}")
    private String active;

    @Bean(value = "API")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("defaultApi 1.0")
                .apiInfo(apiInfo())
                .enable("dev".equals(active))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.cn.app.chatgptbot.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("GPT-WEB-JAVA", "https://github.com/a616567126/GPT-WEB-JAVA", "shen616567126@gmail.com");
        return new ApiInfoBuilder()
                .title("Api文档")
                .description("GPT-WEB-JAVA API 文档，开源系统。")
                .license("Copyright © " + DateUtil.year(DateUtil.date()) + "a616567126")
                .termsOfServiceUrl("")
                .contact(contact)
                .version("v1.0.0")
                .build();
    }
}