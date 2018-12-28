package com.longlian.console.timer;

import com.huaxin.util.DateUtil;
import com.huaxin.util.MessageClient;
import com.longlian.console.common.elastic_job.AbstractShardingTask;
import com.longlian.console.service.AppUserService;
import com.longlian.console.service.LongLianRewardService;
import com.longlian.console.service.SystemConfigService;
import com.longlian.live.service.*;
import com.longlian.model.FollowRewardRule;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务号到期提醒:30/7/当天各提醒一次
 */
@Component("wechatServerEndRemindTimer")
public class WechatServerEndRemindTimer extends AbstractShardingTask{

    private static Logger log = LoggerFactory.getLogger(WechatServerEndRemindTimer.class);

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private MessageClient messageClient;
    /**
     * 任务名称
     * @return
     */
    @Override
    public String getTaskName() {
        return "服务号接入酸枣到期提醒";
    }

    /**
     * 执行任务
     */
    @Override
    public void doExecute() {
        try {
            job();
        } catch (Exception e) {
            log.error("服务号接入酸枣到期提醒异常："+e.getMessage());
            e.printStackTrace();
        }
    }

    public void job() {
        try {
            Map<String, Object> configMap = systemConfigService.findSystemConfigByType(3);
            if (configMap != null) {
                Object contentObj = configMap.get("content");
                Object dataObj = configMap.get("data");
                if (contentObj != null && dataObj != null) {
                    String content = contentObj.toString();
                    String data = dataObj.toString();
                    List<Map> userPhoneList = appUserService.findUserPhoneList();
                    if (userPhoneList != null && userPhoneList.size() > 0) {
                        for (Map map : userPhoneList) {
                            String mobile = map.get("mobile") != null ? map.get("mobile").toString() : "";
                            String freeDate = map.get("free_date") != null ? map.get("free_date").toString() : "";
                            String[] dataArry = data.split(",");

                            if(dataArry!=null && dataArry.length>0){
                                Date formatDate = DateUtil.format(freeDate, "yyyy-MM-dd HH:mm:ss");//到期时间

                                for(String str:dataArry){
                                    Date diffDate = DateUtil.getDayDiff(new Date(), Integer.parseInt(str));
                                    int endData = DateUtil.getIntervalOfDays(diffDate, formatDate);
                                    if (endData == Integer.parseInt(str)) {
                                        String sendContent = content.replace("${times}", str);
                                        messageClient.sendMessage(mobile, sendContent);
                                    }
                                }
                            }


                           /* Date diffDate1 = DateUtil.getDayDiff(new Date(), Integer.parseInt(dataArry[0]));//当前时间+30天
                            Date diffDate2 = DateUtil.getDayDiff(new Date(), Integer.parseInt(dataArry[1]));//当前时间+7天
                            Date diffDate3 = DateUtil.getDayDiff(new Date(), Integer.parseInt(dataArry[2]));//当前时间+1天
                            Date formatDate = DateUtil.format(freeDate, "yyyy-MM-dd HH:mm:ss");//到期时间
                            int mondays = DateUtil.getIntervalOfDays(diffDate1, formatDate);
                            int week = DateUtil.getIntervalOfDays(diffDate2, formatDate);
                            int days = DateUtil.getIntervalOfDays(diffDate3, formatDate);

                            if (mondays == Integer.parseInt(dataArry[0])) {//==30天
                                String sendContent = content.replace("${times}", dataArry[0]);
                                messageClient.sendMessage(mobile, sendContent);
                            } else if (mondays == Integer.parseInt(dataArry[1])) {//7天
                                String sendContent = content.replace("${times}", dataArry[1]);
                                messageClient.sendMessage(mobile, sendContent);
                            } else if (days == Integer.parseInt(dataArry[2])) {//当天
                                String sendContent = content.replace("${times}", dataArry[2]);
                                messageClient.sendMessage(mobile, sendContent);
                            }*/
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("服务号接入酸枣到期提醒:"+e.getMessage());
        }
    }

}
