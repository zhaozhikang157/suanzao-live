package com.longlian.live.util.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class FileDTO {
    /**
     *
     */
    private static final long serialVersionUID = 6314849124435092391L;

    @JSONField(name = "Bucket")
    private String bucket;
    @JSONField(name = "Location")
    private String location;
    @JSONField(name = "Object")
    private String object;

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Bucket", bucket);
        jsonObject.put("Location", location);
        jsonObject.put("Object", object);

        return jsonObject;
    }

    public FileDTO() {

    }


    public FileDTO(String bucket, String location, String object) {
        this.setBucket(bucket);
        this.setLocation(location);
        this.setObject(object);

    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
