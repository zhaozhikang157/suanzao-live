package com.longlian.mq;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by liuhan on 2017-02-17.
 */
public class Main {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext.xml" });
    }
}
