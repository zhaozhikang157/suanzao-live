package com.longlian.console.timer;

import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseService;
import com.longlian.live.service.LiveChannelService;
import com.longlian.model.Course;
import com.longlian.model.LiveChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author caiyunchun
 * @date 2018/6/6 00069:14
 */
@Component("clearClouseByNoStream")
public class ClearClouseByNoStream extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(ClearClouseByNoStream.class);

    @Autowired
    CourseService courseService;
    @Autowired
    LiveChannelService qiNiuliveChannelService;

    @Override
    public String getTaskName() {
        return "无流课程下线任务";
    }

    @Override
    public void doExecute() {
        List<Course> ls = courseService.getCourseByToday();
        if(ls != null && ls.size() > 0){
            for(int i=0; i<ls.size(); i++){
                Course course = ls.get(i);
                boolean isExist = qiNiuliveChannelService.getStreamIsExist(course.getId());
                if(!isExist){//视频流不存在则下线操作
                    log.info("课程id="+course.getId()+" 下视频流不存在");
                    courseService.updateNoStreamClassDown(course.getId());
                }
            }
        }
    }
}
