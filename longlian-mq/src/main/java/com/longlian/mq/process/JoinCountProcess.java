package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 加入课程计数处理
 * Created by admin on 2016/10/20.
 */
@Service
public class JoinCountProcess  extends LongLianProcess{
    @Autowired
    private RedisUtil redisUtil;
    @Value("${joinCount.threadCount:10}")
    private  int threadCount=10;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseService courseService;

    private Logger logg= LoggerFactory.getLogger(JoinCountProcess.class);

    @Override
    public void addThread() {
         GetData t1 = new GetData(this, redisUtil , RedisKey.ll_join_count_wait2db);
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
            long courseId = Long.parseLong(msg);
            boolean isSend=true;
            if(courseId> SystemCofigConst.RELAY_COURSE_ID_SIZE){
               Course c= courseService.getRelayCourse(courseId);
                if(c!=null&&c.getLiveWay()!=null&&"1".equals(c.getLiveWay())){
                    isSend=false;
                }
            }
            long count =  joinCourseRecordService.getCountByCourseId(courseId);
            joinCourseRecordService.writeJoinCount2DB(courseId , count);
            //发送信息云信
            try{
                //加缓存
                //加变更数量
                Course course = courseService.getCourseFromRedis(courseId);

//                List<Course> list = new ArrayList();
//                //如果是系列课，需要给他下面的课发送,自己不发送
//                if ("1".equals(course.getIsSeriesCourse())) {
//                    list = courseService.getCourseBySeriesId(course.getId());
//                    for (Course c : list) {
//                        redisUtil.lpush(RedisKey.ll_join_count_wait2db , String.valueOf(c.getId()));
//                    }
//                    return;
//                }

                Map msg2 = new HashMap();
                msg2.put("type",  YunxinCustomMsgType.JOIN_USER_COUNT_CHANGE.getType() );
                Map val = new HashMap();
                val.put("value", count);
                msg2.put("data", val);
                //给相关的聊天室人员数量发送人员变更消息
                //logg.info(JsonUtil.toJson(msg2));
                if(isSend) {
                    yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg2));
                }
                } catch (Exception ex) {
                logg.error("发送信息云信报错",ex);
            }

        }

    }


}
