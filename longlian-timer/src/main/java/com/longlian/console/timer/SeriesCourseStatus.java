package com.longlian.console.timer;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.dto.ActResultDto;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 * 系列课状态
 */
@Component("seriesCourseStatus")
public class SeriesCourseStatus extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(SeriesCourseStatus.class);
    @Autowired
    CourseService courseService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "循环处理 系列课状态";
    }

    /**
     * 具体执行的任务
     */
    @Override
    public void doExecute() {
        try{
            job();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("系列课状态："+e.getMessage());
        }
    }

    public void job() throws  Exception {
        log.info("SeriesCourseStatus,系列课状态========== start");
        //查询到今日开播的所有语音课程
        List<Course> list = courseService.getSeriesCourseToday();
        if(list != null && list.size() > 0){
            for(Course seriesCourse : list){
                Course singleCourse = courseService.getCourseByBefore(seriesCourse.getId());
                System.out.println("liveWay:"+singleCourse.getLiveWay() + "----isHide:"+singleCourse.getIsHide());
                System.out.println("IsConnection:"+singleCourse.getIsConnection() + "----isRecord:"+singleCourse.getIsRecorded());
                String isHide = "0";
                if("1".equals(singleCourse.getLiveWay())){ //语音课
                    if("1".equals(singleCourse.getIsHide())){
                        isHide = "1" ;
                    }else{
                        isHide = "0" ;
                    }
                }else if("1".equals(singleCourse.getIsRecorded())){ //是录播  //不是语音课，判断 录播 或者 直播
                    if(singleCourse.getStartTime().getTime() > new Date().getTime()){
                        isHide = "1" ;
                    }else{
                        isHide = "0" ;
                    }
                }else{
                    String isConnection = redisUtil.get(RedisKey.ll_live_notify_cation + singleCourse.getId());
                    if(isConnection == null || isConnection.length() < 1){
                        isConnection = singleCourse.getIsConnection() ;
                    }
                    System.out.println("isConnection:="+singleCourse.getIsConnection());
                    if("1".equals(isConnection)){
                        isHide = "0" ;
                    }else{
                        isHide = "1" ;
                    }
                }
                courseService.updateHidden(singleCourse.getSeriesCourseId(),isHide);
            }
        }
    }
}
