package com.longlian.dto;

import com.longlian.model.AdvertisingDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by admin on 2017/6/26.
 */
public class AdvertisingDisplayDto extends AdvertisingDisplay {
    private static Logger log = LoggerFactory.getLogger(AdvertisingDisplayDto.class);


    private Map mapCourseInfo;          //扩展字段

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        AdvertisingDisplayDto.log = log;
    }

    public Map getMapCourseInfo() {
        return mapCourseInfo;
    }

    public void setMapCourseInfo(Map mapCourseInfo) {
        this.mapCourseInfo = mapCourseInfo;
    }
}
