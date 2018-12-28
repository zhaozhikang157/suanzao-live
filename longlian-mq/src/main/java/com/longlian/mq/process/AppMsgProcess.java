package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppMsgService;
import com.longlian.model.AppMsg;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by lh on 2016/10/20.
 * APP消息处理
 */
@Service
public class AppMsgProcess extends LongLianProcess {

    @Autowired
    private AppMsgService appMsgService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${appMsg.threadCount:10}")
    public  int threadCount=10;

    private Logger logg = LoggerFactory.getLogger(AppMsgProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            logg.info("msg===="+msg);
            AppMsg appMsg= JsonUtil.getObject(msg, AppMsg.class);
            MsgType type = MsgType.getMsgTypeByValue(appMsg.getType());
            logg.info("appMsg.getType():"+appMsg.getType()+"   type===="+type);
            if (type.isCourseMsg()) {
                Course course = courseService.getCourseFromRedis(appMsg.getTableId());
                if (course != null) {
                    appMsg.setTeacherId(course.getAppId());
                    appMsg.setIsSeriesCourse(course.getIsSeriesCourse());
                    appMsg.setSeriesCourseId(course.getSeriesCourseId());
                    appMsg.setRoomId(course.getRoomId());
                }
            }

            if (type.getType() == MsgType.LIVE_ROOM_APPLY.getType()) {
                appMsg.setRoomId(appMsg.getTableId());
            }

            appMsgService.insertAppMsg(appMsg);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_app_msg_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
