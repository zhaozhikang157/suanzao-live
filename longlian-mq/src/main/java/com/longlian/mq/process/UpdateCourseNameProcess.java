package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.ChatRoomMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lh on 2016/10/20.
 * 修改课程名称同时修改课程
 */
@Service
public class UpdateCourseNameProcess extends LongLianProcess {

    @Autowired
    private ChatRoomMsgService chatRoomMsgService;
    @Autowired
    private RedisUtil redisUtil;

    public  int threadCount=1;



    private Logger logg = LoggerFactory.getLogger(UpdateCourseNameProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map msgMap = JsonUtil.getObject(msg, HashMap.class);
            String courseId = (String)msgMap.get("id");
            String name = (String)msgMap.get("name");
            chatRoomMsgService.updateCourseName(Long.parseLong(courseId) , name);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_update_course_name);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
