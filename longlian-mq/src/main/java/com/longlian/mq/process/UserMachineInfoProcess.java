package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.model.UserMachineInfo;
import com.longlian.live.service.UserMachineInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by han on 2017/6/9.
 */
@Service
public class UserMachineInfoProcess extends LongLianProcess  {

    @Autowired
    private RedisUtil redisUtil;

    private  int threadCount=5;

    @Autowired
    UserMachineInfoService userMachineInfoService;

    private static Logger logg= LoggerFactory.getLogger(CourseEndProcess.class);
    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_user_machine_info_wait2db);
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
            UserMachineInfo info  = JsonUtil.getObject(json, UserMachineInfo.class);
            userMachineInfoService.saveUserMachineInfo(info);
        }
    }
}
