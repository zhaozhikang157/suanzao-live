package com.huaxin.util.redis;

import java.lang.annotation.*;

/**
 * Created by han on 2017/7/9.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockedObject {
    //不需要值
}