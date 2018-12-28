package com.longlian.mq.process;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.longlian.dto.CourseDto;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.SendMsgService;
import com.longlian.live.service.UserFollowService;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.AppUser;
import com.longlian.model.LiveRoom;
import com.longlian.model.UserFollow;
import com.longlian.mq.service.AppUserService;
import com.longlian.type.MsgType;
import net.sf.json.JSONObject;
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
public class FollowLiveRoomMsgProcess extends LongLianProcess {


    @Autowired
    private RedisUtil redisUtil;
    @Value("${followLiveRoomMsg.threadCount:10}")
    private  int threadCount=10;

    private boolean isSysLogRun = true;
   

    @Autowired
    AppUserService appUserService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    WeixinUtil weixinUtil;

    private Logger logg= LoggerFactory.getLogger(FollowLiveRoomMsgProcess.class);

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }  
        @Override
        public void process(String msg) throws Exception {
            UserFollow userFollow = JsonUtil.getObject(msg, UserFollow.class);
            String website = CustomizedPropertyConfigurer.getContextProperty("website");
            String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
            AppUser appUser =  appUserService.getById(userFollow.getAppId());
            LiveRoom liveRoom = liveRoomService.findById(userFollow.getRoomId());
            //查找直播间
            String title = "亲，我们开通粉丝专属直播间啦，快进来看看吧~" ;
            String description = "直播间:" + liveRoom.getName()  + "\r" + "我们将会以视频、语音形式在这个直播间进行在线直播，也会不定期邀请行业大咖与大家进行交流，大家实时跟老师互动、提问"
                    +"\r" + "所有的直播话题永久保存，不限次数回看！";
            String  picurl = liveRoom.getCoverssAddress();
            String  url = website + LocalOauth2Url.liveRoom + "?id=" + userFollow.getRoomId();
            //发送客服消息
            weixinUtil.sendCustomMessageByOpenId(ll_appid , appUser.getOpenid(), title ,description, url, picurl);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_live_follow_Liveroom_send_wechat_messsage);
        threadPool.execute(t1);
    }
  
    @Override
    public int getThreadCount() {
        return this.threadCount;
    }


}
