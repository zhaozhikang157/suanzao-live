package com.longlian.weixin.ParamesAPI;

import com.longlian.live.util.weixin.LocalOauth2Url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum WebNavigationType {
    home("0" , LocalOauth2Url.weixin, "首页"),
    personalCenter("1" , LocalOauth2Url.personalCenter , "个人中心"),
    liveRoom("2" , LocalOauth2Url.liveRoom , "直播间"),
    //createSeriesCourse("3" , LocalOauth2Url.createSeriesCourse , "创建系列课"),
    //createSingleCourse("4" , LocalOauth2Url.createSingleCourse , "创建单节课"),
    //createSerieSingleCourse("5" , LocalOauth2Url.createSerieSingleCourse , "创建系列课单节课"),
    teacherSeries("6" , LocalOauth2Url.teacherSeries , "系列课详情")
    ;
    private String val;
    private String url;
    private String name;

    private WebNavigationType(String val , String url, String name)  {
        this.val = val;
        this.url = url;
        this.name = name;
    }

    public  static List<Map> getList(){
        List list = new ArrayList();
        WebNavigationType[] logLevels = WebNavigationType.values();
        for (WebNavigationType ll : logLevels) {
            Map map = new HashMap();
            map.put("val" , ll.getVal());
            map.put("name" , ll.getName());
            map.put("url" , ll.getUrl());
            list.add(map);
        }
        return list;
    }

    /**
     * 获取名称 by value
     * @param value
     * @return
     */
    public static String getNameByValue(String value) {
        WebNavigationType[] LogTableTypes = WebNavigationType.values();
        for (WebNavigationType ltt : LogTableTypes) {
            if ((ltt.getVal()+"").equalsIgnoreCase(value)) {
                return ltt.getName();
            }
        }
        return "";
    }

    /**
     * 获取路径  by value
     * @param value
     * @return
     */
    public static String getUrlByValue(String value) {
        WebNavigationType[] LogTableTypes = WebNavigationType.values();
        for (WebNavigationType ltt : LogTableTypes) {
            if ((ltt.getVal()+"").equalsIgnoreCase(value)) {
                return ltt.getUrl();
            }
        }
        return "";
    }

    /**
     * 判断是否存在此路径
     * @param url
     * @return
     */
    public static  String getValByUrl(String url){
        String val = "";
        WebNavigationType[] LogTableTypes = WebNavigationType.values();
        for (WebNavigationType ltt : LogTableTypes) {
            if (ltt.getUrl().equalsIgnoreCase(url)
                    || ltt.getUrl().equalsIgnoreCase(url + "/")) {
                return ltt.getVal();
            }
        }
        return val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
