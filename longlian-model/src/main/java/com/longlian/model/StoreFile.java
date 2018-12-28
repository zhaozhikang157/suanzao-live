package com.longlian.model;

import java.io.Serializable;
import java.util.Date;

public class StoreFile implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String type;

    public StoreFile() {
    }

    public StoreFile(String name, String md5, Long size , String module) {
        this.name = name;
        this.md5 = md5;
        this.size = size;
        this.createTime = new Date();
        this.module = module;
    }

    private String md5;

    private Integer isDel;

    private String url;

    private String module;

    private Long size;

    private Date createTime;

    private String smallUrl;

    //OSS bucket
    private String bucket;

    private String playUrl ;

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPlayUrl() {
        return playUrl;

    }

    public String getBucket() {
        return bucket;

    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    private String basePath;

    public Long getId() {
        return id;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5 == null ? null : md5.trim();
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}