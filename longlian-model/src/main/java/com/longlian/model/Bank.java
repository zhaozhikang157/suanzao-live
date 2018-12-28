package com.longlian.model;

import java.io.Serializable;

/**
 * Created by U on 2016/8/1.
 */
public class Bank implements Serializable {
    private long id;
    private String name;//名称
    private String remark;      //备注
    private int orderSort;         //排序号（编号）
    private String status;          //状态 0-启用 1- 禁用
    private String picAddress;// 图片地址
    private String backgroundStart;     //银行类型开始值
    private String backgroundEnd;       //银行类型结束值

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOrderSort() {
        return orderSort;
    }

    public void setOrderSort(int orderSort) {
        this.orderSort = orderSort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public String getBackgroundStart() {
        return backgroundStart;
    }

    public void setBackgroundStart(String backgroundStart) {
        this.backgroundStart = backgroundStart;
    }

    public String getBackgroundEnd() {
        return backgroundEnd;
    }

    public void setBackgroundEnd(String backgroundEnd) {
        this.backgroundEnd = backgroundEnd;
    }
}
