package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.VideoService;
import com.longlian.model.Video;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 创建 课程视频
 * Created by admin on 2016/10/20.
 */
@Service
public class CreateVideoProcess extends LongLianProcess {

    @Autowired
    private RedisUtil redisUtil;
    private  int threadCount=10;
    //视频上传处理流程
//    @Value("${videoDealFlow:prod_longlian}")
//    private String videoDealFlow = "prod_longlian ";
    @Autowired
    VideoService videoService;
    @Autowired
    CourseService courseService;

    private static Logger logg= LoggerFactory.getLogger(CreateVideoProcess.class);

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }
        @Override
        public void process(String msg) throws Exception {
            Video video = JsonUtil.getObject(msg, Video.class);
            //setConvert(video);
            videoService.save(video);
            //转化成功,修改课程address
//            if (video.getConvertStatus() == 1) {
//                courseService.updeteAddress(video.getCourseId() ,video.getConvertAddress());
//            }
            //去审核
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_create_video_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }


}
