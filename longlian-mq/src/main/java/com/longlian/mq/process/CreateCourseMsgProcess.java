package com.longlian.mq.process;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.SendMsgService;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.model.AppUser;
import com.longlian.mq.service.AppUserService;
import com.longlian.type.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 创建课程发送为您模板消息\内部消息
 * Created by admin on 2016/10/20.
 */
@Service
public class CreateCourseMsgProcess  extends LongLianProcess {


    @Autowired
    private RedisUtil redisUtil;
    @Value("${createCourseMsg.threadCount:10}")
    private  int threadCount=10;

    private boolean isSysLogRun = true;
    @Autowired
    WechatOfficialService wechatOfficialService;

    @Autowired
    SendMsgService sendMsgService;
    @Value("${website}")
    private String website;
    @Autowired
    AppUserService appUserService;
    @Autowired
    UserFollowService userFollowService;

    private Logger logg= LoggerFactory.getLogger(CreateCourseMsgProcess.class);

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }
        @Override
        public void process(String msg) throws Exception {
            CourseDto courseDto = JsonUtil.getObject(msg, CourseDto.class);
            if(courseDto.getIsRelay()==1){
                wechatOfficialService.getFollowUserSendWechatTemplateMessageByRelayCourse(courseDto);
            }else{
                wechatOfficialService.getFollowUserSendWechatTemplateMessageByCourse(courseDto);
                //发送内部消息
                String url = website + "/weixin/courseInfo?id="+courseDto.getId();
                sendMsgService.sendMsg(courseDto.getAppId(), MsgType.NEW_COURSE_TEACHER.getType(), courseDto.getId()
                        , MsgConst.replace(MsgConst.NEW_COUSER_REMIND_CONTENT, DateUtil.format(courseDto.getStartTime()))
                        , url);

                Set<String> follows = userFollowService.getFollowUser(courseDto.getRoomId());
                AppUser user = appUserService.getById(courseDto.getAppId());
                String stUrl = website + "/weixin/liveRoom?id="+courseDto.getRoomId();
                for (String userId : follows) {
                    sendMsgService.sendMsg(Long.parseLong(userId), MsgType.NEW_COURSE_STUDENT.getType(), courseDto.getId()
                            , MsgConst.replace(MsgConst.NEW_COUSER_REMIND_FOLLOW_USER_CONTENT, user.getName())
                            , stUrl);
                }
            }
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_live_create_course_send_wechat_messsage);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }


}
