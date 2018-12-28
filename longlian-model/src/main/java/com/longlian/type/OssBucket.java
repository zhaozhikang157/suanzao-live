package com.longlian.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhan on 2017-06-20.
 */
public enum OssBucket {
    longlian_live("longlian-live" , "oss-cn-hangzhou.aliyuncs.com" , "oss-cn-hangzhou-internal.aliyuncs.com"),
    longlian_live2("longlian-live2" , "oss-cn-shanghai.aliyuncs.com" , "oss-cn-shanghai-internal.aliyuncs.com"),
    longlian_input("longlian-input" , "oss-cn-beijing.aliyuncs.com" ,"oss-cn-beijing-internal.aliyuncs.com") ,
    longlian_output("longlian-output" , "oss-cn-beijing.aliyuncs.com" ,"oss-cn-beijing-internal.aliyuncs.com") ;

    private  String name;
    private String endpoint;
    private String internalEndpoint;

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getInternalEndpoint() {
        return internalEndpoint;
    }

    public String getReadEndpoint() {
        return "http://" + this.getEndpoint() + "/" ;
    }

    public static OssBucket getOssBucketByName(String name) {
        OssBucket[] buckets = OssBucket.values();
        for (OssBucket ms : buckets) {
            if (ms.getName().equalsIgnoreCase(name)) {
                return ms;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public String getWriteEndpoint() {
        return "http://" + this.getEndpoint() + "/" ;
    }

    public String getBucketHttpsAddress() {
       return "https://" + this.getName() + "."+ endpoint +"/";
    }

    OssBucket(String name, String endpoint , String internalEndpoint) {
        this.name = name;
        this.endpoint = endpoint;
        this.internalEndpoint = internalEndpoint;
    }
}
