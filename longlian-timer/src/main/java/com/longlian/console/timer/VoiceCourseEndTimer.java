package com.longlian.console.timer;

import com.huaxin.util.Utility;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.EndLiveService;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.Course;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 * 循环处理 语音课程没有内容结束
 */
@Component("voiceCourseEndTimer")
public class VoiceCourseEndTimer extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(VoiceCourseEndTimer.class);
    @Autowired
    CourseService courseService;
    @Autowired
    ChatMsgRemote remoteService;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "循环处理 语音课程没有内容结束";
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
            log.error("循环处理 语音课程没有内容结束："+e.getMessage());
        }
    }

    public void job() throws  Exception {
        log.info("courseEndTimer语音>5M,课程隐藏状态========== start");
        //查询到今日开播的所有语音课程
        List<Course> list = courseService.getCourseByVoiceToday();
        if(list != null && list.size() > 0){
            for(Course c : list){
                //查看课程开课时间大于5M
                long startTime = c.getStartTime().getTime();
                long nowTime = new Date().getTime();
                long valueTime = nowTime - startTime;
                String isHide = "0";
                if(valueTime > 0){
                    //获取这节语音课程的消息内容
                    ActResultDto actResultDto = remoteService.findLastMsg(c.getId(), c.getAppId());
                    List ls = (List) actResultDto.getData();
                    if(ls == null || ls.size() < 1){
                        //如果课程开课5分钟以后没有内容，hidden
                        courseService.updateHidden(c.getId(),"1");
                        isHide = "1";
                    }else{
                        //如果有内容 判断时间
                        long nowTime2 = new Date().getTime();
                        Map chatroomMsg = (Map) ls.get(0);
                        if((nowTime2 - Long.parseLong(chatroomMsg.get("msgTimestamp").toString())) > (5 * 60 * 1000)){
                            courseService.updateHidden(c.getId(),"1");
                            isHide = "1";
                        }else{
                            courseService.updateHidden(c.getId(),"0");
                            isHide = "0";
                        }
                    }
                }
                //如果系列课，设置状态
                if(c.getSeriesCourseId() > 0){
                    Course nestCourse = courseService.getCourseByBefore(c.getSeriesCourseId()); //获取当前系列课下当前时间的课
                    //如果当前语音课的下一节课没开播，该系列课隐藏
                    if(c.getId() == nestCourse.getId() || nestCourse.getStartTime().getTime() > new Date().getTime()){
                        courseService.updateHidden(c.getSeriesCourseId(),isHide);
                    }
                }
            }
        }
    }
}
