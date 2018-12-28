package com.longlian.chatmsg.config;

import com.huaxin.util.db.DataSourceInterceptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)
public class AopServiceAspect {
    private final static Logger LOGGER = LoggerFactory.getLogger(AopServiceAspect.class);

    @Pointcut("execution(public * com.longlian.*.service.*.*(..))")
    public void accessAspect(){
    }

    @Before(value = "accessAspect()" )
    public void dataSourceBefore(JoinPoint joinPoint) throws Exception {
    }
    @AfterReturning(value = "accessAspect()", returning = "rtv" )
    public void doAfterReturning(JoinPoint joinPoint, Object rtv) {

    }
}
