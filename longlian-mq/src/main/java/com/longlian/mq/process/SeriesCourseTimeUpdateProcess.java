package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.CourseBaseService;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017/05/24.
 * 系列课开课程时间修改,根据单节课程修改
 */
@Service
public class SeriesCourseTimeUpdateProcess extends LongLianProcess {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CourseBaseService courseBaseService;

    private  int threadCount=5;

    private Logger logg= LoggerFactory.getLogger(SeriesCourseTimeUpdateProcess.class);

    @Override
    public void addThread() {
         GetData t1 = new GetData(this, redisUtil , RedisKey.ll_series_course_update_time_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    private class GetData  extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String json) throws Exception {
            Map map  = JsonUtil.getObject(json, HashMap.class);
            Integer seriesId = (Integer)map.get("seriesId");
            courseBaseService.deal(seriesId.longValue());
        }


    }


}
