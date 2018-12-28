package com.longlian.live.util;

import com.huaxin.exception.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by liuhan on 2017-06-26.
 */
@Component
public class LonglianApplicationListener implements ApplicationListener {
    private Logger logg = LoggerFactory.getLogger(LonglianApplicationListener.class);
    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //如果容器关闭时，触发
        String tip = null;

        if(event instanceof ContextClosedEvent){
            tip = "容器关闭";
            String content = event.toString();
            logg.info(tip  + content);
            GlobalExceptionHandler.sendEmail(tip , content);
        }
        //容器刷新时候触发
        if(event instanceof ContextRefreshedEvent){
            tip ="容器刷新";
            String content = event.toString();
            logg.info(tip  + content);
            GlobalExceptionHandler.sendEmail(tip , content);
        }
        //容器启动的时候触发
        if(event instanceof ContextStartedEvent){
            tip = "容器启动";
            String content = event.toString();
            logg.info(tip  + content);
            GlobalExceptionHandler.sendEmail(tip , content);
        }
        //容器停止时候触发
        if(event instanceof ContextStoppedEvent){
            tip = "容器停止";
            String content = event.toString();
            logg.info(tip  + content);
            GlobalExceptionHandler.sendEmail(tip , content);
        }

    }
}
