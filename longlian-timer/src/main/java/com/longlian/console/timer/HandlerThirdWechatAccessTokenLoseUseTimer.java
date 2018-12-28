package com.longlian.console.timer;

import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.live.service.WechatOfficialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/7/8.
 * 处理第三方授权公众号失效
 */
@Component("handlerThirdWechatAccessTokenLoseUseTimer")
public class HandlerThirdWechatAccessTokenLoseUseTimer extends AbstractShardingTask {

    private static Logger log = LoggerFactory.getLogger(HandlerThirdWechatAccessTokenLoseUseTimer.class);

    @Autowired
    WechatOfficialService wechatOfficialService;

    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "处理第三方授权公众号失效";
    }

    /**
     * 执行任务
     */
    @Override
    public void doExecute() {
        try {
            job();
        } catch (Exception e) {
            log.error("处理第三方授权公众号失效异常："+e.getMessage());
            e.printStackTrace();
        }
    }

    public void job() throws  Exception {
        wechatOfficialService.handleThirdWechatTokenLoseUse();
    }

}
