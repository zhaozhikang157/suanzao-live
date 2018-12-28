package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台统计类型
 * Created by liuhan on 2017-02-21.
 */
public enum CountType {

    NEW_COURSE("101","新增课程数")//set直接写
    , ALL_COURSE_PAY("102","新增付费课程数")//set直接写
    , NEW_COURSE_PAY("103","新增付费(新增的)课程数")//需要算 01和02的交集
    , ALL_PLATFORM_COURSE_COUNTS("104","平台开课数")
    //所有用户
    , NEW_USER("105","新增加用户")//set直接写
    , NEW_TEACHER("106","新增加老师")//set直接写

    , USER_PAY("107","付费总用户数")//set直接写
    , USER_PAY_AMT("108","付费总金额")//string直接写
    , NEW_USER_PAY("109","新增付费(新增的)用户数")//需要算 需要算 05和07的交集

    , ACTIVE_USER("110","活跃用户")//set直接写
    , START_USER("112","PV")//string直接写
     , DAY_RETENTION("113","次留存率")//需要算 ((今天的)10 & (昨天的)05 )/(昨天的)05
    , NEW_USER_PAY_RET("114","新增用户付费率")//需要算 需要算 09/05
    , NEW_COURSE_PAY_RET("115","新增课程付费率") //需要算 需要算 03/01;//需要算 需要算 03/01
    , USING_LIVE_CHANNEL("116","正在用的直播通道")//正在用的直播通道

    , PAGE_STAY_TIME("117" , "页面停留时长")
    , PAGE_EXIT_RATE("118" , "页面退出率")
    , PAGE_VISIT_COUNT("119", "页面综合访问数") //页面PV
    , PAGE_VISIT_USER_COUNT("120", "页面访问人数")//页面UV
    , PAGESITE_VISIT_COUNT("121" , "站点综合访问数")//站点PV
    , PAGESITE_VISIT_USER_COUNT("122" , "站点访问人数")//站点UV
    , PAGE_EXIT_COUNT("123" , "页面退出人数")//UV
    , PAGE_EXIT_USER_COUNT("124" , "页面退出次数")//PV
    , PAGESITE_STAY_TIME("125" , "站点访问时间")//PV
    , BUTTON_CLICK_COUNT("126" , "按扭点击次数")//PV
    , PAGE_HANDLE_TIME_AVG("127" , "页面平均执行时间")//PV

    , ALL_COURSE_RELAY("128" , "新增转播课程数")//PV
    ;

    private final String name;
    private String type;

    CountType(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public static String getNameByValue(String type) {
        CountType[] countTypes = CountType.values();
        for (CountType ms : countTypes) {
            if (ms.getType().equalsIgnoreCase(type)) {
                return ms.getName();
            }
        }
        return "";
    }

    public static List<Map> getList() {
        List list = new ArrayList();
        CountType[] countTypes = CountType.values();
        for (CountType ms : countTypes) {
            Map map = new HashMap();
            map.put("type", ms.getType());
            map.put("name", ms.getName());
            list.add(map);
        }
        return list;
    }
}
