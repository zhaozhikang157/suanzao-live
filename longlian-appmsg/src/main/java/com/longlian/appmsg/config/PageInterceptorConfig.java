package com.longlian.appmsg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by liuhan on 2017-05-27.
 */
@Configuration
public class PageInterceptorConfig {

    @Bean(name="pageInterceptor")
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageInterceptor.setProperties(p);
        return pageInterceptor;
    }
}
