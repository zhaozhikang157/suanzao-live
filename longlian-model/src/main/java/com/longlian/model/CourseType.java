package com.longlian.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * tablename:course_type
 */
public class CourseType implements Serializable {
    
    private long id;
    private long parent_Id;
    private String name;//名称
    private String path;//课程类型路径
    private String status;//状态 0-正常 1-禁用
    private String picAddress;
    private String remark;
    private Integer orderSort;//排序
    private Date createTime;//创建时间
    private Date updateTime;//跟新时间


   // private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");



    private long parentId;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getParent_Id() {
        return parent_Id;
    }

    public void setParent_Id(long parent_Id) {
        this.parent_Id = parent_Id;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

   /* public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }*/

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
        this.name = name == null ? null : name.trim();
    }

   /* public long getParent_Id() {
        return parent_Id;
    }

    public void setParent_Id(long parent_Id) {
        this.parent_Id = parent_Id;
    }*/

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public String getCreateTime() {
        if(createTime != null){
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(createTime);
        }
        return "";
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime(){
        if(updateTime != null){
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(updateTime);
        }
        return "";
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Integer getOrderSort() {
        return orderSort;
    }

    public void setOrderSort(Integer orderSort) {
        this.orderSort = orderSort;
    }
}