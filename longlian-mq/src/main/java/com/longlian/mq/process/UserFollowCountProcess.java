package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppMsgService;
import com.longlian.model.UserFollow;
import com.longlian.mq.service.AppUserService;
import com.longlian.mq.service.UserFollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 用户关注处理
 * Created by admin on 2016/10/20.
 */
@Service
public class UserFollowCountProcess  extends LongLianProcess {

    @Autowired
    private UserFollowService userFollowServiceMq;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${userFollow.threadCount:10}")
    private  int threadCount=10;
    @Autowired
    private AppMsgService appMsgService;
    @Autowired
    private AppUserService appUserService;


    private Logger logg= LoggerFactory.getLogger(UserFollowCountProcess.class);

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            UserFollow userFollow= JsonUtil.getObject(msg, UserFollow.class);
            if(1 == userFollow.getStatus()){   //取消点赞
                userFollowServiceMq.followOrConcelFollow(userFollow);
            }else{

                Integer i = userFollowServiceMq.getCount(userFollow);
                //如果以前没关注过
                if(i == null || i ==0){
                    //增加关注
                    userFollowServiceMq.insertUserFollow(userFollow);
                    // LiveRoom room = liveRoomService.getLiveRoomById(userFollow.getRoomId());
                    // Long teacherId = room.getAppId();
                    // Long[] idarray = new Long[]{teacherId , userFollow.getAppId()};
                    // List<Long> ids = Arrays.asList(idarray);
                    //Map map = appUserService.getNames(ids);

                    //提醒老师
                    //appMsgService.sendMsg(teacherId , MsgType.FOLLOW_LIVE_ROOM.getType() ,userFollow.getId() , MsgConst.replace(MsgConst.NEW_FOLLOW_REMIND_TEACHER_CONTENT , (String)map.get(userFollow.getAppId())), "");
                    //提醒学生
                    //appMsgService.sendMsg(userFollow.getAppId() ,MsgType.FOLLOW_LIVE_ROOM_STUDENT.getType() ,userFollow.getId(), MsgConst.replace(MsgConst.FOLLOW_REMIND_STUDENT_CONTENT, (String)map.get(teacherId)) ,"" );
                } else {
                    //修改关注
                    userFollowServiceMq.followOrConcelFollow(userFollow);
                }

            }
        }
    }
    @Override
    public void addThread() {
         GetData t1 = new GetData(this, redisUtil , RedisKey.ll_user_follow_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
}
