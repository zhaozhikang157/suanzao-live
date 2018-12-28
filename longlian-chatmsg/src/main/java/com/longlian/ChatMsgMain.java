package com.longlian;

/**
 * Created by liuhan on 2018-01-02.
 */
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
@ComponentScan(basePackages={"com.huaxin.util","com.longlian.chatmsg"})
public class ChatMsgMain implements CommandLineRunner{
    @PostConstruct
    public void initialization() {
        CustomizedPropertyConfigurer propertyConfigurer = new CustomizedPropertyConfigurer();
        final List<Properties> resourceLst = new ArrayList<Properties>();
        Resource re0 = new ClassPathResource("application.properties");
        Properties  ps = new Properties();
        try {
            ps.load(re0.getInputStream());
            String key = ps.getProperty("spring.profiles.active");
            Resource re1 = new ClassPathResource("application-"+ key +".properties");
            ps.load(re1.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        propertyConfigurer.loadProp(ps);
    }



    public static void main(String[] args) throws Exception {


        SpringApplication.run(ChatMsgMain.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        //查询
        

        //处理1
        //处理2
    }
}
