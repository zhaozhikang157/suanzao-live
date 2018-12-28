package com.huaxin.util.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.huaxin.util.type.OutPutEnum;

/**
 * Created with IntelliJ IDEA.
 * To change this template use File | Settings | File Templates.
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonFilter {
      public OutPutEnum value() default OutPutEnum.ALL_NO_WRITE;
}
