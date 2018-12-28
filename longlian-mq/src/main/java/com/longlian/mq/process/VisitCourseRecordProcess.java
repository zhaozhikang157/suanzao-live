package com.longlian.mq.process;

import com.github.pagehelper.StringUtil;
import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseBaseNumService;
import com.longlian.model.CourseBaseNum;
import com.longlian.model.VisitCourseRecord;
import com.longlian.mq.service.CourseService;
import com.longlian.live.service.VisitCourseRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by admin on 2016/10/20.
 */
@Service
public class VisitCourseRecordProcess  extends LongLianProcess {

    @Autowired
    private VisitCourseRecordService visitCourseRecordService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseBaseNumService courseBaseNumService;

    private  int threadCount=10;

    private Logger logg= LoggerFactory.getLogger(VisitCourseRecordProcess.class);

    private class GetData  extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            logg.info(msg);
            VisitCourseRecord visitCourseRecord = JsonUtil.getObject(msg, VisitCourseRecord.class);
            deal(visitCourseRecord);
        }

        private void deal(VisitCourseRecord visitCourseRecord) {
            try{
                String fieldKey = String.valueOf(visitCourseRecord.getAppId()) ;
                //加载所有的
                String key = RedisKey.ll_cache_visit_record_record + visitCourseRecord.getCourseId();

                visitCourseRecordService.loadVisitRecord2Redis(visitCourseRecord.getCourseId());
                //更新成功, 表示，不存在
                if (redisUtil.sadd(key, fieldKey) == 1) {
                    //int i = visitCourseRecordService.deleteRecord(visitCourseRecord.getAppId(),visitCourseRecord.getCourseId());
                    visitCourseRecordService.insertRecord(visitCourseRecord);
                    logg.info("mq添加访问记录:"+JsonUtil.toJson(visitCourseRecord));

                    //发送更新数量的消息
                    //courseService.addVisitCount(visitCourseRecord.getCourseId());
                    long baseNum = 0 ;
                    if (redisUtil.hexists(RedisKey.ll_course_base_visit_num ,String.valueOf(visitCourseRecord.getCourseId()) )) {
                       String baseNumStr  = redisUtil.hget(RedisKey.ll_course_base_visit_num ,String.valueOf(visitCourseRecord.getCourseId()));
                       if (!StringUtil.isEmpty(baseNumStr)) {
                           baseNum = Integer.parseInt(baseNumStr);
                       }
                    } else {
                        CourseBaseNum courseBaseNum = courseBaseNumService.selectByCourse(visitCourseRecord.getCourseId() , "1");
                        if (courseBaseNum != null) {
                            baseNum =  courseBaseNum.getCount();
                        }
                    }
                    //访问基数,后台设置的
                    redisUtil.hset(RedisKey.ll_course_base_visit_num, String.valueOf(visitCourseRecord.getCourseId()), String.valueOf(baseNum));

                    baseNum += redisUtil.scard(key);
                    long courseId=visitCourseRecord.getCourseId();
                    if(courseId!=0 && String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                        //转播课更新访问记录数量
                        courseService.updateRelayVisitCount(visitCourseRecord.getCourseId() , baseNum);
                    }else{
                        courseService.updateVisitCount(visitCourseRecord.getCourseId() , baseNum);
                    }
                }
                redisUtil.expire(key , 60 * 60  * 24 * 3);
            }catch(Exception e){
                logg.error("用户访问记录存储报错",e);
                GlobalExceptionHandler.sendEmail(e , "mq错误");
            }
            //如果是系列课里的单节课，则需要保存系列课的访问记录
            if (visitCourseRecord.getSeriesCourseId() != 0l) {
                VisitCourseRecord seriesCourse = new VisitCourseRecord();
                seriesCourse.setAppId(visitCourseRecord.getAppId());
                seriesCourse.setCreateTime(new Date());
                seriesCourse.setCourseId(visitCourseRecord.getSeriesCourseId());
                seriesCourse.setStatus(0);
                seriesCourse.setFromType(visitCourseRecord.getFromType());
                seriesCourse.setInvitationUserId(visitCourseRecord.getInvitationUserId());
                seriesCourse.setSeriesCourseId(0l);
                deal(seriesCourse);
            }
        }
    }


    /***
     * 每隔30分钟执行一次
     */
   // @Scheduled(cron = "0 */30 * * *")
    public void doJob() {
        Long maxTime = new Date().getTime() - 30 * 60 * 1000 ;
        Long minTime = new Date().getTime() - 90 * 60 * 1000 ;

        redisUtil.zremrangeByScore(RedisKey.ll_cache_visit_record_record  , minTime , maxTime);
    }
    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_live_visit_course_record_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
