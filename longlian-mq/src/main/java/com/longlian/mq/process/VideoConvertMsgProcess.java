package com.longlian.mq.process;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.mts.model.v20140618.QueryMediaListRequest;
import com.aliyuncs.mts.model.v20140618.QueryMediaListResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.VideoService;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.aliyun.ActivityDTO;
import com.longlian.live.util.aliyun.ActivityType;
import com.longlian.live.util.aliyun.MediaState;
import com.longlian.live.util.aliyun.MediaWorkflowExecutionDTO;
import com.longlian.model.Video;
import com.longlian.mq.service.CourseService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * Created by lh on 2016/10/20.
 * APP消息处理
 */
@Service
public class VideoConvertMsgProcess extends LongLianProcess {
    @Value("${videoConvertMsg:http://1872266391331581.mns.cn-beijing.aliyuncs.com/}")
    private  String msgUrl = "http://1872266391331581.mns.cn-beijing.aliyuncs.com/";
    //视频上传目录&视频监听主题
    @Value("${videoConvertMsgTopic:videoprodconvertqueue}")
    private String videoConvertMsgTopic = "videoprodconvertqueue";
    @Autowired
    private VideoService videoService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private RedisUtil redisUtil;

    public  int threadCount=1;

    private static final String REGION = "cn-beijing";

    private static final String mtsEndpoint = "mts." + REGION + ".aliyuncs.com";

    private  CloudAccount account;
    private  CloudQueue queue;
    private  DefaultAcsClient aliyunClient;//调用MTS API的Client

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        account = new CloudAccount(LonglianSsoUtil.accessKeyId, LonglianSsoUtil.accessKeySecret, msgUrl);
        MNSClient mnsClient = account.getMNSClient();
        queue = mnsClient.getQueueRef(videoConvertMsgTopic);

        try {
            DefaultProfile.addEndpoint(REGION, REGION, "Mts", mtsEndpoint);
        } catch (ClientException e) {
            logg.info(ExceptionUtils.getStackTrace(e));
            //System.exit(1);
        }
        aliyunClient = new DefaultAcsClient(DefaultProfile.getProfile(REGION, LonglianSsoUtil.accessKeyId, LonglianSsoUtil.accessKeySecret));
    }


    private Logger logg = LoggerFactory.getLogger(VideoConvertMsgProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil) {
            super(longLianProcess ,redisUtil);
        }

        @Override
        public void run() {
            //不是所有关闭
            while(!LongLianProcess.isAllColse && longLianProcess.isRun){
                //从消息队列里到得消息
                List<Message> messages = null;
                try {
                    messages = queue.batchPopMessage(16, 30);
                } catch (Exception ex) {
                    continue;
                }
                if(messages == null || messages.size() == 0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    try{
                        for (Message message : messages) {
                            handlingMessage(message);
                        }
                    }catch(Exception e){
                        logg.error("{},处理报错:{}", this.getClass().getName(),e);
                        GlobalExceptionHandler.sendEmail(e , "mq错误");
                    }
                }
            }
            logg.info( "类{} ：线程 ：{}已经关闭" , this.getClass().getName(),Thread.currentThread().getName());
        }

        public void process(String msg) {}


        private  void handlingMessage(Message message) {
            String messageBody = message.getMessageBody();
            ActivityDTO activityDTO = JSONObject.parseObject(messageBody, ActivityDTO.class);
            if (activityDTO.getType().equals(ActivityType.Start.name())) {
                //若工作流启动消息不希望处理，则直接删除
                queue.deleteMessage(message.getReceiptHandle());
            } else if (activityDTO.getType().equals(ActivityType.Report.name())) {
                //logg.info(JSONObject.toJSONString(activityDTO));
                //获取媒体工作流执行信息
                MediaWorkflowExecutionDTO mediaWorkflowExecutionDTO = activityDTO.getMediaWorkflowExecutionDTO();

                //从媒体工作流中取活动列表
                List<ActivityDTO> activityDTOS = mediaWorkflowExecutionDTO.getActivities();


                int status = 0;
                String fail = "";
                String code = "";
                //遍历活动列表，判断转码是否成功
                if (null != activityDTOS) {
                    for (ActivityDTO activity : activityDTOS) {
                        System.out.println(activity.getJobId());
                        if (activity.getType().equals(ActivityType.Transcode.name())) {

                            if (activity.getState().equals(com.longlian.live.util.aliyun.ActivityState.Success.name())) {
                                // logg.infoln("transcode activity:" + activity.getName() + " is success!");
                                //status = 1;
                            } else if (activity.getState().equals(com.longlian.live.util.aliyun.ActivityState.Running.name())) {
                                //status = 2;

                            } else if (activity.getState().equals(com.longlian.live.util.aliyun.ActivityState.Fail.name())) {
                                status = -1;
                                fail = activity.getMsg();
                                code = activity.getCode();
                            } else if (activity.getState().equals(com.longlian.live.util.aliyun.ActivityState.Initiated.name())) {
                                //status = 0;
                            }
                            if (status != 1) {
//                                String fileUrl = activity.getMediaWorkflowExecutionDTO().getInput().getFile().getLocation();
//                                logg.info("---------------------" + fileUrl);
//                                List<Video> videos = videoService.findByUrl(fileUrl);
//                                //转化失败，但是还没有插入的,先把结果放到缓存
//                                if ((videos == null || videos.size() == 0 ) && status == -1) {
//                                    Video v = new Video();
//                                    v.setConvertStatus(status);
//                                    redisUtil.setex(RedisKey.ll_video_convert_pre + fileUrl , 60 * 60 * 24 * 3 , JsonUtil.toJson(v));
//                                    logg.error("视频：{},转化失败！,code:{},msg:{}",fileUrl,activity.getCode() , activity.getMsg());
//                                }
//                                for (Video v : videos) {
//                                    v.setConvertStatus(status);
//                                    videoService.updateByPrimaryKeySelective(v);
//                                }

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
                                //logg.infoln(media);
                                for (QueryMediaListResponse.Media.Play play : playList) {
                                    //遍历转码输出地址
                                    save(media.getCoverURL()  , media.getFile().getURL() , 1 ,Long.parseLong(play.getDuration()), Long.parseLong(play.getSize()) , play.getFile().getURL() , play.getWidth() , play.getHeight());
                                }
                            }
                        }

                        if (status == -1) {
                                String fileUrl = media.getFile().getURL();
                                    logg.info("---------------------" + fileUrl);
                                if (fileUrl !=null &&fileUrl.startsWith("http://")) {
                                    fileUrl = fileUrl.replace("http://", "https://");
                                }
                                List<Video> videos = videoService.findByUrl( fileUrl);
                                //转化失败，但是还没有插入的,先把结果放到缓存
                                if ((videos == null || videos.size() == 0 ) && status == -1) {
                                    Video v = new Video();
                                    v.setConvertStatus(status);
                                    v.setFailReason(fail);
                                    redisUtil.setex(RedisKey.ll_video_convert_pre +  DigestUtils.md5DigestAsHex(fileUrl.getBytes()) , 60 * 60 * 24 * 3 , JsonUtil.toJson(v));
                                    logg.error("视频：{},转化失败！,code:{},msg:{}",fileUrl , code , fail);
                                }
                                for (Video v : videos) {
                                    v.setConvertStatus(status);
                                    v.setFailReason(fail);
                                    videoService.updateByPrimaryKeySelective(v);
                                    logg.error("视频：{},转化失败！,code:{},msg:{}",fileUrl , code , fail);
                                }
                        }

                    } catch (ServerException e) {
                        logg.error("上传阿里云异常：", e);
                        //System.exit(1);

                    } catch (ClientException e) {
                        logg.error("上传阿里云异常：", e);
                        //System.exit(1);
                    }catch(Exception e){
                        logg.error("监听阿里云转换消息处理异常：", e);

                    }

                //如果是report消息，需要从队列中删除，否则会一直在队列中
                queue.deleteMessage(message.getReceiptHandle());
            }
        }


        public void save(String imageAddress  , String fileUrl , int status , long duration , long size , String address , String width , String height) {
            if (fileUrl !=null &&fileUrl.startsWith("http://")) {
                fileUrl = fileUrl.replace("http://", "https://");
            }

            List<Video> videos = videoService.findByUrl(fileUrl);

            if (videos == null || videos.size() == 0) {
                Video v = new Video();
                v.setVideoAddress(fileUrl);
                v.setConvertAddress(address);
                //如果封面为空时才修改
                v.setImgAddress(imageAddress);
                v.setConvertStatus(status);
                v.setDuration(duration);
                v.setSize(size);
                v.setHeight(Integer.parseInt(height));
                v.setWidth(Integer.parseInt(width));

                //System.out.println("URL:"+ fileUrl +",md5:" + DigestUtils.md5DigestAsHex(fileUrl.getBytes()));
                //System.out.println("JSON:"+ JsonUtil.toJson(v));
                redisUtil.setex(RedisKey.ll_video_convert_pre + DigestUtils.md5DigestAsHex(fileUrl.getBytes()) , 60 * 60 * 24 * 3 , JsonUtil.toJson(v));
                return ;
            }

            for (Video v : videos) {
                v.setConvertAddress(address);
                v.setImgAddress(imageAddress);
                v.setConvertStatus(status);
                v.setDuration(duration);
                v.setSize(size);
                v.setHeight(Integer.parseInt(height));
                v.setWidth(Integer.parseInt(width));
                //System.out.println("URL2:"+ fileUrl +",md52:" + DigestUtils.md5DigestAsHex(fileUrl.getBytes()));
               // System.out.println("JSON2:"+ JsonUtil.toJson(v));
                videoService.saveConvertSuccessStauts(v);
            }
        }
/*
        public void process(List<Message> messages) {
            for (MediaWorkflowMessage message : messages) {
                logg.info("-------------------------------------------------url:{},workflowName:{}",message.fileURL() , message.messageWorkflowName());
                Media media = client.getMedia(message.messageWorkflowName(),
                        message.fileURL());

                // 开始消息
                if (message.type() == MediaWorkflowMessageType.Start) {
                    if (media != null && media.workflowExecutionOutputs() != null && media.workflowExecutionOutputs().get(0) != null && media.workflowExecutionOutputs().get(0).transcodeOutputs().size() > 0  )  {
                        for (TranscodeOutput transcodeOutput : media.workflowExecutionOutputs().get(0).transcodeOutputs()) {
                            logg.info( "TranscodeOutput state:\t" +  transcodeOutput.state());
                            if (ActivityState.Running == transcodeOutput.state()) {

                            }
                        }
                    }
                }
                // 完成消息
                if (message.type() == MediaWorkflowMessageType.Report) {
                    // 处理消息
                    String address = "";
                    String imageAddress = "";
                    int status = -1;
                    Long duration = 0l;
                    Long size = 0l ;
                    if (media != null && media.workflowExecutionOutputs() != null && media.workflowExecutionOutputs().get(0) != null && media.workflowExecutionOutputs().get(0).transcodeOutputs().size() > 0  )  {
                        for (TranscodeOutput transcodeOutput : media.workflowExecutionOutputs().get(0).transcodeOutputs()) {
                            logg.info("TranscodeOutput state:\t" + transcodeOutput.state());
                            if (ActivityState.Success == transcodeOutput.state()) {
                                logg.info("TranscodeOutput OSS URL:\t" + transcodeOutput.ossUrl());

                                address = transcodeOutput.ossUrl();
                                if (address !=null && address.startsWith("http://")) {
                                    address = address.replace("http://", "https://");
                                }
                                logg.info(  "TranscodeOutput Presigned OSS URL:\t" +    transcodeOutput.generatePresignedOSSUrl());
                                logg.info(   "Width: " + transcodeOutput.mediaInfo().width() +
                                        ", Height: " + transcodeOutput.mediaInfo().height() +
                                        ", Duration: " + transcodeOutput.mediaInfo().duration() +
                                        ", Format: " + transcodeOutput.mediaInfo().format() +
                                        ", FileSize: " + transcodeOutput.mediaInfo().fileSize() +
                                        ", Bitrate: " + transcodeOutput.mediaInfo().bitrate());

                                duration = (new Float( transcodeOutput.mediaInfo().duration() * 1000)).longValue();
                                status = 1;
                                size = transcodeOutput.mediaInfo().fileSize();
                            } else if (ActivityState.Fail == transcodeOutput.state()) {
                                status = -1;
                            }
                        }
                    }
                    if (media != null && media.workflowExecutionOutputs() != null && media.workflowExecutionOutputs().get(0) != null && media.workflowExecutionOutputs().get(0).snapshotOutputs().size() > 0  )  {
                        for (SnapshotOutput snapshotOutput : media.workflowExecutionOutputs().get(0).snapshotOutputs()) {
                            logg.info(       "SnapshotOutput state:\t" +  snapshotOutput.state());
                            logg.info(
                                    "SnapshotOutput OSS URL:\t" +
                                            snapshotOutput.ossUrl());
                            imageAddress = snapshotOutput.ossUrl();

                            if (imageAddress !=null &&imageAddress.startsWith("http://")) {
                                imageAddress = imageAddress.replace("http://", "https://");
                            }

                            logg.info(
                                    "SnapshotOutput Presigned OSS URL:\t" +
                                            snapshotOutput.generatePresignedOSSUrl());
                        }
                    }

                    String fileUrl = message.fileURL();
                    if (fileUrl != null && fileUrl.startsWith("http://")) {
                        fileUrl = fileUrl.replace("http://", "https://");
                    }


                }
                List<String> receiptHandles = new ArrayList<String>();
                receiptHandles.add(message.receiptHandle());
                client.deleteQueueMessages(msgUrl,  videoConvertMsgTopic, receiptHandles);
            }
        }*/
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }



}
