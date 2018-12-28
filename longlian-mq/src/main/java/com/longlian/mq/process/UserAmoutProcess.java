package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AccountService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Account;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 增加用户收益发送课程消息
 * Created by admin on 2016/10/20.
 */
@Service
public class UserAmoutProcess extends LongLianProcess{
    @Autowired
    private RedisUtil redisUtil;

    private  int threadCount = 10;
    @Autowired
    AccountService accountService;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;

    private Logger logg= LoggerFactory.getLogger(UserAmoutProcess.class);

    @Override
    public void addThread() {
         GetData t1 = new GetData(this, redisUtil , RedisKey.ll_course_user_income_queue);
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
            String[] ss = msg.split(",");
            Long appId = Long.parseLong(ss[0]);
            Long courseId = Long.parseLong(ss[1]);
            Long chatRoomId = Long.parseLong(ss[2]);

            Account account = accountService.getAccountByAppId(appId);
            //发送信息云信
            try{
                Map msg2 = new HashMap();
                msg2.put("type",  YunxinCustomMsgType.USER_COURSE_INCOME_CHANGE.getType() );
                Map val = new HashMap();
                val.put("value", account.getAddTotalAmount());
                msg2.put("data",val);
                //给相关的聊天室老师早币发送人员变更消息
                logg.info(JsonUtil.toJson(msg2));

                yunxinChatRoomUtil.sendMsg(String.valueOf(chatRoomId) ,String.valueOf(appId)  , "100", JsonUtil.toJson(msg2));
            } catch (Exception ex) {
                logg.error("发送信息云信报错",ex);
            }

        }

    }


}
