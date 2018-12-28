package com.longlian.eureka;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


/**
 * Created by liuhan on 2017-09-18.
 */
@SpringBootApplication
@EnableEurekaServer//开启Eureka Server
public class EurekaServerMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(EurekaServerMain.class).web(true).run(args);
    }
}
