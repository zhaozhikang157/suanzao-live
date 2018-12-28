package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by pangchao on 2017/1/22.
 */
public class SystemPara implements Serializable {
    private static Logger log = LoggerFactory.getLogger(SystemPara.class);

    private long id;
    private String name;        //名称
    private String code;        //代码
    private String value;    //参数值
    private String describe;     //描述

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        SystemPara.log = log;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}