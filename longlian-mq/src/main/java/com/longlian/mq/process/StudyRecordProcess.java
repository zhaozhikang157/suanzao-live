package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.StudyRecordDetailService;
import com.longlian.model.Course;
import com.longlian.model.StudyRecord;
import com.longlian.model.StudyRecordDetail;
import com.longlian.mq.service.CourseService;
import com.longlian.mq.service.StudyRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by admin on 2016/10/20.
 * 学习记录增加
 */
@Service
public class StudyRecordProcess extends LongLianProcess {


    @Autowired
    private RedisUtil redisUtil;
    @Value("${studyRecord.threadCount:10}")
    private  int threadCount=10;


    @Autowired
    StudyRecordService studyRecordServiceMq;
    @Autowired
    com.longlian.live.service.StudyRecordService studyRecordService;
    @Autowired
    StudyRecordDetailService studyRecordDetailService;
    @Autowired
    CourseService courseService;

    private Logger logg= LoggerFactory.getLogger(StudyRecordProcess.class);

    private class GetData  extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }
        @Override
        public void process(String json) throws Exception {
            StudyRecord record = JsonUtil.getObject(json, StudyRecord.class);
            //是不是回看

            boolean isExist = true;
            if (studyRecordServiceMq.isExist(record.getAppId() , record.getCourseId())) {
                record.setReview("1");
            } else {
                isExist = false;
                record.setReview("0");
            }
            studyRecordServiceMq.insertRecord(record);
            //是第一次
            if (!isExist) {
                //加入，人员列表
                studyRecordService.addUser2Redis(record.getCourseId(),   record.getAppId(), new Date(), record.isVirtualUser());
            }

            //课程已经结束，需要保存细节
            Course course = courseService.getCourseFromRedis(record.getCourseId());
            if (course != null && course.getEndTime() != null) {
                Long studyStartPoint = 0l;
                //已经结束了，需要返回上次学到什么时间点了。
                StudyRecordDetail rec =  studyRecordDetailService.getStudyRecordDetailRec(record.getCourseId() , record.getAppId());
                if (rec != null) {
                    //上次是看完了的是学习开始点则为0
                    //如果上次不是看完了的。则从结束时间点开始看起，如果没有结束时间点，则和上次开始点一致
                    if ("0".equals(rec.getIsEnd())) {
                        studyStartPoint = rec.getStudyEndPoint();
                        if (studyStartPoint == null || studyStartPoint == 0) {
                            studyStartPoint = rec.getStudyStartPoint();
                        }
                    }
                }
                StudyRecordDetail now =new StudyRecordDetail();
                now.setCourseId(record.getCourseId());
                now.setAppId(record.getAppId());
                now.setRecordId(record.getId());
                now.setIsEnd("0");
                now.setStudyStartTime(new Date());
                now.setStudyStartPoint(studyStartPoint);
                studyRecordDetailService.addStudyRecordDetail(now);
            }
        }

    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_study_record_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
