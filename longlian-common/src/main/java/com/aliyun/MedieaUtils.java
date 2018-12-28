package com.aliyun;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.mts.model.v20140618.*;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;

import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/6/26 0026.
 */
public class MedieaUtils {

    private static String accessKeyId = "HdhrU64EnCRlKEmY";
    private static String accessKeySecret = "82lNWjoyAUfTuhXfhcDN4rTWpZM8Ta";
    private static String mpsRegionId = "cn-beijing";
    //华东1
//    private static String pipelineId = "b46752bbf074491abf639a68a3e5ed0b";
    //华北2
    private static String pipelineId = "8c50fa8ace474110869508c6a20654aa";

    //华东1
//    private static String templateId = "fd051b7a2d934184a83d19e20ca7ea58";
    //华北2
    private static String templateId = "b0bcaecb26544dfdafe5c98e0dfd366a";

    private static String ossLocation = "oss-cn-beijing";
    private static String ossBucket = "llkeji";
    private static String ossInputObject = "CHAT_MSG_AUDIO/0000a07f76d34082b9d6fff852701193.aac";
    private static String ossOutputObject = "output";

    public static void mergeVideo(){
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(
                mpsRegionId,      // 地域ID
                accessKeyId,      // RAM账号的AccessKey ID
                accessKeySecret); // RAM账号Access Key Secret
        IAcsClient client = new DefaultAcsClient(profile);
        // 创建API请求并设置参数
        SubmitJobsRequest request = new SubmitJobsRequest();
        // Input
        JSONObject input = new JSONObject();
        input.put("Location", ossLocation);
        input.put("Bucket", ossBucket);
        try {
            input.put("Object", URLEncoder.encode(ossInputObject, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("input URL encode failed");
        }
        request.setInput(input.toJSONString());
        // Output
        String outputOSSObject;
        try {
            outputOSSObject = URLEncoder.encode(ossOutputObject, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("output URL encode failed");
        }
        JSONObject output = new JSONObject();
        output.put("OutputObject", outputOSSObject);
        // Ouput->Container
        JSONObject container = new JSONObject();
        container.put("Format", "mp4");
        output.put("Container", container.toJSONString());
        // Ouput->Video
//        JSONObject video = new JSONObject();
//        video.put("Codec", "H.264");
//        video.put("Bitrate", "1500");
//        video.put("Width", "1280");
//        video.put("Fps", "25");
//        output.put("Video", video.toJSONString());
        // Ouput->Audio
//        JSONObject audio = new JSONObject();
//        audio.put("Codec", "AAC");
//        audio.put("Bitrate", "128");
//        audio.put("Channels", "2");
//        audio.put("Samplerate", "44100");
//        output.put("Audio", audio.toJSONString());
        // Ouput->TemplateId
        output.put("TemplateId", templateId);
        //Output->MergeList
        JSONArray mergeList = new JSONArray();
//        try {
        JSONObject mergeOne = new JSONObject();
//            mergeOne.put("MergeURL", URLEncoder.encode("https://longlian-input.oss-cn-beijing.aliyuncs.com/longlian_testACbdysFxsD.mp4", "utf-8"));
        mergeOne.put("MergeURL", "https://llkeji.oss-cn-beijing.aliyuncs.com/CHAT_MSG_AUDIO/0001e238bc304617be7f90318cf837b9.m4a");
        mergeList.add(mergeOne);
        JSONObject mergeTwo = new JSONObject();
//            mergeTwo.put("MergeURL", URLEncoder.encode("https://longlian-input.oss-cn-beijing.aliyuncs.com/longlian_testPk7YK3KQsh.mp4", "utf-8"));
        mergeTwo.put("MergeURL", "https://llkeji.oss-cn-beijing.aliyuncs.com/CHAT_MSG_AUDIO/0001f7b213ce4510aa92ce05608d8bc3.aac");
        mergeList.add(mergeTwo);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        if (mergeList.size() > 0){
            output.put("MergeList", mergeList.toJSONString());
        }

        JSONArray outputs = new JSONArray();
        outputs.add(output);
        request.setOutputs(outputs.toJSONString());
        request.setOutputBucket(ossBucket);
        request.setOutputLocation(ossLocation);
        // PipelineId
        request.setPipelineId(pipelineId);
        // 发起请求并处理应答或异常
        SubmitJobsResponse response;
        try {
            response = client.getAcsResponse(request);
            System.out.println("RequestId is:"+response.getRequestId());
            if (response.getJobResultList().get(0).getSuccess()) {
                System.out.println("JobId is:" + response.getJobResultList().get(0).getJob().getJobId());
            } else {
                System.out.println("SubmitJobs Failed code>>" + response.getJobResultList().get(0).getCode() +
                        "|| message>>" + response.getJobResultList().get(0).getMessage());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) {
        //点播服务所在的Region，国内请填cn-shanghai，不要填写别的区域
        String regionId = "cn-shanghai";
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    public static void getVideoInfo(){
        DefaultAcsClient client = initVodClient(accessKeyId, accessKeySecret);
        GetVideoInfoResponse response = new GetVideoInfoResponse();
        try {
            GetVideoInfoRequest request = new GetVideoInfoRequest();
            request.setVideoId("2d6caf35501d4c6aba94f0d16903864e");

            response = client.getAcsResponse(request);
            System.out.print("Title = " + response.getVideo().getTitle() + "\n");
            System.out.print("Description = " + response.getVideo().getDescription() + "\n");
            System.out.println("Duration = " + response.getVideo().getDuration());
        } catch (Exception e) {
            System.out.print("ErrorMessage = " + e.getLocalizedMessage());
        }
        System.out.print("RequestId = " + response.getRequestId() + "\n");
    }

    public static void main(String[] args) {
        getVideoInfo();
    }

}
