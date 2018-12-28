package com.longlian.mq.task;

import com.longlian.live.service.WechatOfficialService;
import com.longlian.model.WechatOfficial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/11/13.
 */
@Component("FreeTimeTask")
public class FreeTimeTask {
    private static Logger log = LoggerFactory.getLogger(FreeTimeTask.class);

    @Autowired
    WechatOfficialService wechatOfficialService;

    @Scheduled(cron = "0 1 0 * * ?")
    public void doJob() throws Exception {
        List<WechatOfficial> list = wechatOfficialService.getAllWechat();
        if(list.size()>0){
            for(WechatOfficial wechatOfficial : list){
                Date freeTime = wechatOfficial.getFreeDate();
                if(freeTime.compareTo(new Date()) < 0 ){
                    wechatOfficialService.updateFreeDate(wechatOfficial.getId());
                    //把所欠的流量清零
                }
            }
        }
    }
}
