package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.CourseDto;
import com.longlian.dto.ShareRecordDto;
import com.longlian.live.service.WechatOfficialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 创建课程发送为您模板消息
 * Created by admin on 2016/10/20.
 */
@Service
public class ShareRoomOrCourseCardMsgProcess extends LongLianProcess {


    @Autowired
    private RedisUtil redisUtil;
    @Value("${createCourseMsg.threadCount:10}")
    private  int threadCount=10;

    private boolean isSysLogRun = true;
    @Autowired
    WechatOfficialService wechatOfficialService;

    private Logger logg= LoggerFactory.getLogger(ShareRoomOrCourseCardMsgProcess.class);

    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }
        @Override
        public void process(String msg) throws Exception {
            ShareRecordDto shareRecordDto = JsonUtil.getObject(msg, ShareRecordDto.class);
            wechatOfficialService.sendRoomOrCourseCardWachatMessage(shareRecordDto);
        }
    }

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil , RedisKey.ll_live_share_course_or_room_card_send_wechat_messsage);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }


}
