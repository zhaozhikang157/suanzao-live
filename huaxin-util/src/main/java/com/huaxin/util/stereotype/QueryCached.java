package com.huaxin.util.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * using for redis to record cached
 *
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryCached {	  
	public int timeout() default 60*15;

    public String key() default "";

    public String keyPreFix() default "";
	public Class retclass() default Object.class;
}
