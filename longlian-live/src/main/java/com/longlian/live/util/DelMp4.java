package com.longlian.live.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.huaxin.util.JsonUtil;
import com.longlian.type.OssBucket;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public class DelMp4 {
    public static String accessKeyId="HdhrU64EnCRlKEmY";
    public static String accessKeySecret="82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";
    public static void main(String[] args) {
        OSSClient ossClient = null;
        OssBucket ossBucket = OssBucket.longlian_live2;
        int i =0;
        try {
            ossClient = new OSSClient(ossBucket.getWriteEndpoint(),accessKeyId,accessKeySecret);
            final int maxKeys = 200;
            String nextMarker = null;
            ObjectListing objectListing = null;
            do {
                objectListing = ossClient.listObjects(new ListObjectsRequest(ossBucket.getName()).withPrefix("dev/record/").withMarker(nextMarker).withMaxKeys(maxKeys));
                List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    String key = s.getKey();
                    if (key.endsWith(".mp4")) {
                        i++;
                        System.out.println( s.getKey());
                        ossClient.deleteObject(ossBucket.getName() , key);
                    }
                }
                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            close(ossClient);
        }
        System.out.println(i);
    }
    /**
     * 关闭客户端
     * @param ossClient
     */
    public static void close(OSSClient ossClient){
        if(ossClient != null){
            ossClient.shutdown();
        }
    }
}
