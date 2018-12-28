package com.longlian.live.util.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.mts.model.v20140618.QueryMediaListRequest;
import com.aliyuncs.mts.model.v20140618.QueryMediaListResponse;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

public class Main {

    //步骤1.请填写你所需访问的Region：cn-hangzhou、cn-shenzhen、cn-shanghai、cn-beijing
    private static final String REGION = "cn-shenzhen";

    private static final String ossEndPoint = "http://oss-" + REGION + ".aliyuncs.com";

    //步骤2.从阿里云消息服务的控制台对应区域Copy MNSEndPoint
    //MNS控制台链接：https://mns.console.aliyun.com
    private static final String mnsEndPoint = "";


    private static final String mtsEndpoint = "mts." + REGION + ".aliyuncs.com";


    private static String testVideoFilePath = "videos/test-中文.mp4";


    //步骤3.填写您的AK，确保此AK具有访问MTS的权限（即已经开通MTS）
    private static String accessKeyId = "";
    private static String accessKeySecret = "";

    //步骤4.填写您的媒体工作流输入Bbucket,务必与创建媒体工作流时设置的保持一致
    private static String mediaWorkflowInputBucket = "vodal-wasu-cn-huashu";

    //步骤5.填写媒体工作流处理视频完成时消息发送队列，务必与创建媒体工作流时设置的保持的一致
    private static String mediaWorkflowQueueName = "hua";


    private static CloudAccount account;
    private static CloudQueue queue;
    private static DefaultAcsClient aliyunClient;//调用MTS API的Client

    static {
        account = new CloudAccount(accessKeyId, accessKeySecret, mnsEndPoint);
        MNSClient mnsClient = account.getMNSClient();
        queue = mnsClient.getQueueRef(mediaWorkflowQueueName);

        try {
            DefaultProfile.addEndpoint(REGION, REGION, "Mts", mtsEndpoint);
        } catch (ClientException e) {
            System.out.print(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
        aliyunClient = new DefaultAcsClient(DefaultProfile.getProfile(REGION, accessKeyId, accessKeySecret));
    }


    public static void main(String[] args) throws ClientException {

        //步骤6.上传视频到媒体工作流处理Bucket，媒体工作流将自动启动处理此视频
        //进入MTS控制台：https://mts.console.aliyun.com/
        //进入媒体库设置->工作流，务必确保媒体工作流处于启动状态，否则媒体工作流不会处理此视频
        FileDTO inputFile = uploadVideoFileToMediaWOrkflowInputBucket(mediaWorkflowInputBucket);


        //步骤7.等待媒体工作流消息回调汇报转码完成，
        while (true) {
            List<Message> messageList = queue.batchPopMessage(16, 30);
            if (null == messageList || messageList.isEmpty()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            for (Message message : messageList) {
                handlingMessage(message);
            }


        }


    }

    private static void handlingMessage(Message message) {
        String messageBody = message.getMessageBody();
        ActivityDTO activityDTO = JSONObject.parseObject(messageBody, ActivityDTO.class);
        if (activityDTO.getType().equals(ActivityType.Start.name())) {
            //若工作流启动消息不希望处理，则直接删除
            queue.deleteMessage(message.getReceiptHandle());
        } else if (activityDTO.getType().equals(ActivityType.Report.name())) {
            //System.out.print(JSONObject.toJSONString(activityDTO));
            //获取媒体工作流执行信息
            MediaWorkflowExecutionDTO mediaWorkflowExecutionDTO = activityDTO.getMediaWorkflowExecutionDTO();

            //从媒体工作流中取活动列表
            List<ActivityDTO> activityDTOS = mediaWorkflowExecutionDTO.getActivities();

            //遍历活动列表，判断转码是否成功
            if (null != activityDTOS) {
                for (ActivityDTO activity : activityDTOS) {
                    if (activity.getType().equals(ActivityType.Transcode.name())) {
                        if (activity.getState().equals(ActivityState.Success.name())) {
                           // System.out.println("transcode activity:" + activity.getName() + " is success!");

                        }
                    }

                }
            }


            //获取转码输出地址（OSS地址)
            QueryMediaListRequest request = new QueryMediaListRequest();
            request.setMediaIds(mediaWorkflowExecutionDTO.getMediaId());
            request.setIncludePlayList(true);
            try {
                QueryMediaListResponse response = aliyunClient.getAcsResponse(request);
                List<QueryMediaListResponse.Media> list = response.getMediaList();
                QueryMediaListResponse.Media media = list.get(0);
                if (media.getPublishState().equals(MediaState.Published.name())) {
                    List<QueryMediaListResponse.Media.Play> playList = media.getPlayList();
                    if (null != playList) {
                        for (QueryMediaListResponse.Media.Play play : playList) {
                            //遍历转码输出地址
                            System.out.println(play.getFile().getURL());
                        }
                    }

                }

            } catch (ServerException e) {
                System.out.print(ExceptionUtils.getStackTrace(e));
                System.exit(1);

            } catch (ClientException e) {
                System.out.print(ExceptionUtils.getStackTrace(e));
                System.exit(1);
            }

            //如果是report消息，需要从队列中删除，否则会一直在队列中
            queue.deleteMessage(message.getReceiptHandle());


        }
    }

    private static FileDTO  uploadVideoFileToMediaWOrkflowInputBucket(String mediaWorkflowInputBucket) {
        return uploadLocalFile(mediaWorkflowInputBucket, "demo/video/" + UUID.randomUUID().toString() + "-中文.mp4", testVideoFilePath);
    }


    public static FileDTO uploadLocalFile(String bucket, String object, String filePath) {
        OSSClient client = new OSSClient(ossEndPoint, accessKeyId, accessKeySecret);
        try {
            String utf8FilePath = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(filePath).getPath(),
                    "utf-8");
            File file = new File(utf8FilePath);
            InputStream content = new FileInputStream(file);

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.length());
            client.putObject(bucket, object, content, meta);

            String encodedObjectName = URLEncoder.encode(object, "utf-8");
            return new FileDTO(client.getBucketLocation(bucket), bucket, encodedObjectName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("fail@uploadLocalFile FileInputStream");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("fail@uploadLocalFile URLEncoder");
        }
    }


}
