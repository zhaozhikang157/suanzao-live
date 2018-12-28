package com.longlian.console.timer;

import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.CourseRelayService;
import com.longlian.console.service.CourseService;
import com.longlian.console.service.OrdersService;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Administrator on 2017/2/23.
 * 检查转播系列单节课补充任务
 */
@Component("courseRelayCheckAndUpdate")
public class CourseRelayCheckAndUpdate extends AbstractShardingTask {
    private static Logger log = LoggerFactory.getLogger(CourseRelayCheckAndUpdate.class);
    @Autowired
    CourseService courseService;
    @Autowired
    ChatMsgRemote remoteService;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    OrdersService ordersService;
    @Autowired
    CourseRelayService courseRelayService;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "循环处理 检查转播系列单节课补充任务";
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
            log.error("循环处理 检查转播系列单节课补充任务："+e.getMessage());
        }
    }

    public void job() throws  Exception {
        List<Long> noRelay = new ArrayList<Long>();
        Map<Long,Long> oriCount = courseService.getCourseCountBySeriesInToday();  //当天新建的系列课
        Object[] obs = oriCount.keySet().toArray();
        Long[] ids = new Long[obs.length] ;
        for(int i=0; i<obs.length; i++){
            ids[i] = (Long) obs[i];
        }
        Map<Long,Map> relayCount = courseService.getCourseRelayCountBySeriesToday(ids);
        if(oriCount != null && oriCount.size() > 0){  //原课
            for(Map.Entry<Long,Long> oEntry : oriCount.entrySet()){
                long oriKey = oEntry.getKey();
                long oriValue = oEntry.getValue();
                if(relayCount != null && relayCount.size() > 0){ //转播课 比较
                    boolean flag = false ;
                    Map<Long,Long> relayMap = relayCount.get(oriKey);
                    if(relayMap != null && relayMap.size() > 0){  //大于0，说明这个被转播了
                        Set set = relayMap.keySet();
                        Iterator iter = set.iterator();
                        while(iter.hasNext()){
                            long relayKey = (long) iter.next();
                            long relayValue = relayMap.get(relayKey);
                            if(relayValue < oriValue){
                                flag = true;
                                break;
                            }
                        }
                    }
                    if(flag){
                        noRelay.add(oriKey);
                    }
                }

            }
        }
        if(noRelay.size() > 0){
            //有系列单节课没有同步，处理
            log.info("没有同步成功的系列单节课：" + noRelay);
            for(long courseId : noRelay){
                Course course = courseBaseService.getCourse(courseId);  //原系列课
                List<Long> oriCourseIds = courseRelayService.getCourseIdsBySeries(courseId); //原系列单节课ID
                CourseRelayDto searchRelay = new CourseRelayDto();      //构建查询条件bean
                searchRelay.setOriCourseId(courseId);
                searchRelay.setIsSeriesCourse("1");
                List<CourseRelayDto> relaySeries = courseRelayService.getRelayCourse(searchRelay);  //所有的转播的系列课
                if(relaySeries != null && relaySeries.size() > 0){
                    for(int i=0; i<relaySeries.size(); i++){
                        CourseRelayDto relayDto = relaySeries.get(i); //转播的系列课
                        Map<Long,Long> ctds = courseRelayService.getRelayCourseIds(relayDto.getId()); //系列转播ID 获取单节课ID
                        for(Long oriCourseId : oriCourseIds){
                            if(ctds == null || !ctds.containsValue(oriCourseId)){ //如果转播课在原课列表里面不存在，说明没转播过来
                                //缺少转播系列单节课
                                log.info("补充缺少的转播课 ==== id:"+oriCourseId);
                                CourseRelayDto singleCourse = new CourseRelayDto();
                                Course oriCourse = courseService.getCourseFromRedis(oriCourseId); //获取原课信息
                                singleCourse.setOriAppId(oriCourse.getAppId());
                                singleCourse.setOriCourseId(oriCourseId);
                                singleCourse.setAppId(relayDto.getAppId());
                                singleCourse.setIsSeriesCourse("0");
                                singleCourse.setSeriesCourseId(relayDto.getId());
                                singleCourse.setRoomId(relayDto.getRoomId());
                                //补充过来的转播课和原课的状态保持一直
                                singleCourse.setStatus(oriCourse.getStatus());
                                singleCourse.setIsDelete("0");
                                if("1".equals(oriCourse.getLiveWay())){ //如果是语音课，chatRoomId是原课的
                                    singleCourse.setChatRoomId(oriCourse.getChatRoomId());
                                }
                                courseRelayService.createCourseRelay(singleCourse);
                                //添加转播云信token  语音课用原来的
                                if(!"1".equals(oriCourse.getLiveWay())){
                                    courseBaseService.setRelayChatroomId(singleCourse, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
