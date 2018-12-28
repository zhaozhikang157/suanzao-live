package com.longlian.console.task_mq;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseRelayService;
import com.longlian.console.service.CourseService;
import com.longlian.dto.CourseAuditDto;
import com.longlian.live.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by admin on 2017/5/19.
 */
@Component("updateCourseStatusTask")
public class UpdateCourseStatusTask extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(UpdateCourseStatusTask.class);

    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    CourseService courseService;
    @Autowired
    CourseAuditService courseAuditService;

    @Autowired
    WechatOfficialService wechatOfficialService;

    @Autowired
    CountService countService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getTaskName() {
        return "更新课程审核状态";
    }

    @Override
    public void doExecute() {
        try {
            doJob();
        } catch (Exception e) {
            log.error("更新课程审核状态：",e);
        }
    }

    public void doJob() throws Exception {
       List<CourseAuditDto> list =  courseAuditService.getAllNoAudit();

       for (CourseAuditDto v : list) {
           //如果是系列课
           if ("1".equals(v.getIsSeriesCourse())  ) {
               //改成上线状态
               if ("1".equals(v.getIsGarbage())) {
                   courseBaseService.updateStatus(v.getCourseId() , "0");
                   courseBaseService.updateStatusToRelay(v.getCourseId(),"0");
                   courseAuditService.updateStatus(v.getCourseId() , "" , "1");
               } else if ("-1".equals(v.getIsGarbage())) {
                   courseAuditService.updateStatus(v.getCourseId() , v.getGarbageTip() , "-1");
               }
               continue;
           }

            //如果是单节课
           if ("0".equals(v.getIsSeriesCourse())) {

               boolean isPass = true;
               StringBuffer sb = new StringBuffer();
               if ("-1".equals(v.getIsGarbage())) {
                  sb.append(v.getGarbageTip()).append(";");
                   isPass = false;
               }

               if (v.getConvertStatus() == -1) {
                   sb.append("视频转化失败！").append(";");
                   isPass = false;
               } else {
//                   String isVerticalScreen = v.getIsVerticalScreen();
//                   int width = v.getWidth();
//                   int height = v.getHeight();
//                   if ("1".equals(isVerticalScreen)) {
//                        if (width > height) {
//                            isPass = false;
//                            sb.append("竖屏直播但上传的视频宽度大于高度！").append(";");
//                        }
//                   } else {
//                       if (width < height) {
//                           isPass = false;
//                           sb.append("横屏直播但上传的视频高度大于宽度！").append(";");
//                       }
//                   }
               }
               //改成上线状态
               if (isPass) {
                   courseBaseService.updatePassStatus(v.getCourseId());
                   courseBaseService.updateStatusToRelay(v.getCourseId(),"0");
               } else  {
                   courseAuditService.updateStatus(v.getCourseId() , sb.toString() , "-1");
               }
           }

       }
    }
}
