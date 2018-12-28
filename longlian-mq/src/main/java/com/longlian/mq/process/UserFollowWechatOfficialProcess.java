package com.longlian.mq.process;

import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.longlian.live.service.AppMsgService;
import com.longlian.live.service.UserFollowWechatOfficialService;
import com.longlian.model.UserFollowWechatOfficial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lh on 2016/10/20.
 * APP消息处理
 */
@Service
public class UserFollowWechatOfficialProcess extends LongLianProcess {
    @Autowired
    private AppMsgService appMsgService;
    @Autowired
    private RedisUtil redisUtil;
    public  int threadCount=10;

    @Autowired
    UserFollowWechatOfficialService userFollowWechatOfficialService;

    @Autowired
    private RedisLock redisLock;

    private Logger log = LoggerFactory.getLogger(UserFollowWechatOfficialProcess.class);
    private class GetData extends DataRunner{

        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map = JsonUtil.getObject(msg , HashMap.class);
            String website = CustomizedPropertyConfigurer.getContextProperty("website");
            System.out.print("UserFollowWechatOfficialProcess MAP================================" + map);
            UserFollowWechatOfficial userFollowWechatOfficial= JsonUtil.getObject(map.get("UserFollowWechatOfficial").toString(), UserFollowWechatOfficial.class);
            WechatMessageInfo wechatMessageInfo = JsonUtil.getObject(map.get("wechatMessageInfo").toString(), WechatMessageInfo.class);
            Long  courseId = Long.parseLong(map.get("courseId").toString());
            Long roomId = Long.parseLong(map.get("liveId").toString());
            Long channelId = Long.parseLong(map.get("channelId").toString());
            Long appId = Long.parseLong(map.get("appId").toString());

            String key = RedisKey.ll_user_follow_wechat_lock + userFollowWechatOfficial.getAppId() + "_" +userFollowWechatOfficial.getWechatAppid();
            //200毫秒轮讯一次 ， 2秒算超时
            boolean flag = redisLock.lock(key , 200 * 1000, 5);

            //获取锁失败，
            if (!flag) {
                log.info("获取锁{}失败!", key);
                GlobalExceptionHandler.sendEmail("获取锁" + key + "失败", "注意");
            } else {
                try{
                    Long followId= 0l;
                    UserFollowWechatOfficial userFollow = userFollowWechatOfficialService.selectFollowThirdOfficial(null, userFollowWechatOfficial.getWechatAppid(),userFollowWechatOfficial.getOpenid());
                    if(Utility.isNullorEmpty(userFollow)) {
                        /*if(100==channelId) {
                            userFollowWechatOfficial.setAppId(0l);
                        }*/
                        userFollowWechatOfficialService.insert(userFollowWechatOfficial);
                        followId = userFollowWechatOfficial.getId();
                    }else{
                        if (1 == userFollow.getStatus()  && userFollow.getOpenid().equals(userFollow.getOpenid())) {
                            userFollowWechatOfficialService.updateFollowStatus(0, userFollow.getId());
                            followId = userFollow.getId();
                        }
                    }
                    if(courseId>0)
                    {
                        userFollowWechatOfficialService.sendCustomMessage4CourseByOpenid(wechatMessageInfo,website, roomId, courseId, appId ,channelId,followId);
                    }else{
                        userFollowWechatOfficialService.sendCustomMessage4LiveByOpenid(wechatMessageInfo,website, roomId,followId);
                    }

                }finally{
                    redisLock.unlock(key);
                }
            }

        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this , redisUtil , RedisKey.ll_user_follow_wechat_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

}
