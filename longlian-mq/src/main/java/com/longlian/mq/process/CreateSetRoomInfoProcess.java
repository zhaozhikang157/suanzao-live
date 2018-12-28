package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseBaseService;
import com.longlian.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lh on 2016/10/20.
 */
@Service
public class CreateSetRoomInfoProcess extends LongLianProcess {

    @Autowired
    private CourseBaseService courseService;
    @Autowired
    private RedisUtil redisUtil;
    public  int threadCount=5;

    private Logger logg = LoggerFactory.getLogger(CreateSetRoomInfoProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Course course = JsonUtil.getObject(msg, Course.class);
            courseService.setLiveRoomInfo(course);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_live_create_course_roominfo);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
