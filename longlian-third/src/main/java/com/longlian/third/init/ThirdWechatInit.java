package com.longlian.third.init;

import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.SystemParaService;
import com.longlian.live.service.WechatOfficialRoomService;
import com.longlian.live.service.WechatOfficialService;
import com.longlian.model.WechatOfficial;
import com.longlian.model.WechatOfficialRoom;
import com.longlian.third.service.WechatOfficiaService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 系统参数放入redis
 * Created by syl on 2017/3/23.
 */
@Component("thirdWechatInit")
public class ThirdWechatInit implements InitializingBean {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    WechatOfficialRoomService wechatOfficialRoomService;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean isExistsKey =  redisUtil.exists(RedisKey.ll_live_appid_use_authorizer_room_info);//先取redis取
        if(!isExistsKey){
            //如果没有则从数据库查询并存入redis
//            List<WechatOfficial> list = wechatOfficialService.selectUseList();
            List<WechatOfficialRoom> list =   wechatOfficialRoomService.selectUseList();
            for (WechatOfficialRoom wechatOfficial: list){
                long liveId = wechatOfficial.getLiveId();
                String appid = wechatOfficial.getWechatId();
                redisUtil.hset(RedisKey.ll_live_appid_use_authorizer_room_info ,liveId + "" ,appid);
            }
        }
    }
}
