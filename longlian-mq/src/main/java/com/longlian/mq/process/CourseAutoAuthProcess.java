package com.longlian.mq.process;

import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseAuditService;
import com.longlian.live.service.CourseBaseService;
//import com.longlian.live.util.yunXinAnti.TextQueryByTaskIds;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by lh on 2016/10/20.
 * 课程标题和课程介绍签别
 */
@Service
public class CourseAutoAuthProcess extends LongLianProcess {
    @Autowired
    private RedisUtil redisUtil;

    private  int threadCount=10;

    private static Logger logg= LoggerFactory.getLogger(CourseAutoAuthProcess.class);
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private CourseAuditService courseAuditService;




    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_course_garbage);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            logg.info("课程自动审核:{}" ,msg);
            Map map = JsonUtil.getObject(msg , HashMap.class);

            boolean isGarbage = false;
            StringBuffer remark = new StringBuffer();
            //暂时关掉自动审核
//            int topicAction = TextQueryByTaskIds.Yunxin((String)map.get("liveTopic"), UUIDGenerator.generate());
//            if (topicAction == 2) {
//                isGarbage = true;
//                remark.append("课程标题、");
//            }
//
//            int remarkAction = TextQueryByTaskIds.Yunxin((String)map.get("remark"), UUIDGenerator.generate());
//            if (remarkAction == 2) {
//                isGarbage = true;
//                remark.append("课程简介、");
//            }
//
//            int contentAction = TextQueryByTaskIds.Yunxin((String)map.get("content"), UUIDGenerator.generate());
//            if (contentAction == 2) {
//                isGarbage = true;
//                remark.append("课程图文简介、");
//            }
            //isGarbage = false;
            Long courseId = Long.valueOf((String)map.get("courseId"));

            //标题、课程介绍、图文介绍
            //提交签别
            //根据签别结果，生成自动审核信息

            if (isGarbage) {
                remark.deleteCharAt(remark.length() - 1);
                remark.append("包含垃圾信息！请修改！");
                //改为垃圾信息审核不通过
                courseAuditService.updateGarbageStatus(courseId , remark.toString() , "-1");
            } else {
                //改成上线状态
                //courseBaseService.updateStatus(courseId , "0");
                //改为垃圾信息审核通过
                courseAuditService.updateGarbageStatus(courseId , "" , "1");
            }
            String courseKey = RedisKey.ll_course + courseId;
            redisUtil.del(courseKey);
        }

    }
}
