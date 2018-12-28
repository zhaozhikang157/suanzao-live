package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.GagService;
import com.longlian.model.Gag;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/8/7.
 */
@Service
public class CourseGagDelProcess extends LongLianProcess{
    private static Logger log = LoggerFactory.getLogger(CourseGagDelProcess.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    GagService gagService;

    private  int threadCount=10;

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_del_user_gag_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Gag gag = JsonUtil.getObject(msg, Gag.class);
            if(gag != null){
                gagService.delGagMq(gag);
                redisUtil.del(RedisKey.ll_set_user_gag + gag.getCourseId());
                String s = gagService.findUserIdByCourseId(gag.getCourseId());
                if(StringUtils.isNotEmpty(s)){
                    redisUtil.set(RedisKey.ll_set_user_gag + gag.getCourseId(), s);
                }
            }
        }
    }
}
