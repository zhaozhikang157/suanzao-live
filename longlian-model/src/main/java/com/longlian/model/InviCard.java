package com.longlian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by admin on 2017/2/15.
 */
public class InviCard {
    private static Logger log = LoggerFactory.getLogger(InviCard.class);

    private long id;
    private String code; //模板编号
    private String name;    //模本名称
    private String address; //地址
    private String xy;      //坐标
    private String type;    //0:课程  1:直播间
    private int imgWidth;
    private String size;    //字体,图片大小
    private String color; //颜色
    private String hight;   //换行高度

    public String getHight() {
        return hight;
    }

    public void setHight(String hight) {
        this.hight = hight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        InviCard.log = log;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getXy() {
        return xy;
    }

    public void setXy(String xy) {
        this.xy = xy;
    }
}
