package com.longlian.live.util.log;

import com.longlian.type.LogType;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
	LogType type(); //操作类型
	String systemType() default "0";//系统类型 0 -app端 1-console端
	String content()  default ""; //日志说明
	String deviceNo() default  "";//手机设备号
}
