package com.longlian.mq.task;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.VideoService;
import com.longlian.model.Video;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * Created by admin on 2017/5/19.
 */
@Component("updateVideoConvertAddressTask")
public class UpdateVideoConvertAddressTask {
    private static Logger log = LoggerFactory.getLogger(UpdateVideoConvertAddressTask.class);

    @Autowired
    VideoService videoService;
    @Autowired
    RedisUtil redisUtil;


    @Scheduled(cron = "0 0/1 * * * ?")
    public void doJob() throws Exception {
       List<Video> list =  videoService.getAllNoConvertVideo();

       for (Video v : list) {
           if (v == null || StringUtils.isEmpty(v.getVideoAddress())) continue ;
           //System.out.println("URL:"+ v.getVideoAddress() +",md5:" + DigestUtils.md5DigestAsHex(v.getVideoAddress().getBytes()));
           String viStr = redisUtil.get(RedisKey.ll_video_convert_pre + DigestUtils.md5DigestAsHex(v.getVideoAddress().getBytes()));
           if (StringUtils.isEmpty(viStr)) {
             continue;
           }
           Video video = JsonUtil.getObject(viStr, Video.class);

           //转化失败
           if (video.getConvertStatus() == -1) {
               v.setConvertStatus(video.getConvertStatus());
               videoService.updateByPrimaryKeySelective(v);
               //转化成功
           } else if (video.getConvertStatus() == 1) {
               v.setConvertAddress(video.getConvertAddress());
               v.setImgAddress(video.getImgAddress());
               v.setConvertStatus(video.getConvertStatus());
               v.setDuration(video.getDuration());
               v.setSize(video.getSize());
               v.setWidth(video.getWidth());
               v.setHeight(video.getHeight());
               videoService.saveConvertSuccessStauts(v);
           }
       }
    }
}
