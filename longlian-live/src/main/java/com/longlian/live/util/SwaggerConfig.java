package com.longlian.live.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@Configuration
@EnableWebMvc // 启用Mvc //非springboot框架需要引入注解@EnableWebMvc
@EnableSwagger2 // 启用Swagger2，毕竟SpringFox的核心依旧是Swagger
public class SwaggerConfig extends WebMvcConfigurerAdapter {
    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select()// 选择哪些路径和API会生成document
                .apis(RequestHandlerSelectors.basePackage("com.longlian.live.controller"))// 对所有api进行监控
                .paths(PathSelectors.any())// 对所有路径进行监控
                .build().apiInfo(apiInfo());
    }// */

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "酸枣直播API",
                "longlian api",
                "v5.1.2",
                "liuhan@suanzao.com",
                "My Apps API Licence Type",
                "My Apps API License URL",""
        );
        return apiInfo;
    }

}