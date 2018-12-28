package com.longlian.mq.task;

import com.huaxin.util.DateUtil;
import com.longlian.live.service.DataChargeRecordService;
import com.longlian.model.DataChargeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/11/1.
 */
@Component("dataChargeTask")
public class DataChargeTask {
    private static Logger log = LoggerFactory.getLogger(DataChargeTask.class);

    @Autowired
    DataChargeRecordService recordService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void doJob() throws Exception {
        List<DataChargeRecord> recordList = recordService.getAllRecord();
        for(DataChargeRecord chargeRecord: recordList){
            if(chargeRecord.getInvalidDate() != 0 ){
                Date invalidTime = chargeRecord.getInvalidRealDate();
                if(invalidTime == null){
                    if(chargeRecord.getInvalidDate() > 0 ){
                        if("0".equals(chargeRecord.getInvalidDateUnit())){       //天
                            invalidTime = DateUtil.getDayHourAfter(chargeRecord.getOrderTime(), chargeRecord.getInvalidDate() * 24);
                        }else if("1".equals(chargeRecord.getInvalidDateUnit())){ //月
                            invalidTime = DateUtil.getDayHourAfter(chargeRecord.getOrderTime(), chargeRecord.getInvalidDate() * 24 * 30);
                        }else if("2".equals(chargeRecord.getInvalidDateUnit())){ //年
                            invalidTime = DateUtil.getDayHourAfter(chargeRecord.getOrderTime(), chargeRecord.getInvalidDate() * 24 * 30 * 365);
                        }
                        Date nowTime = new Date();
                        String iT = DateUtil.format(invalidTime, "yyyy-MM-dd") + " 23:59:59";
                        if(nowTime.compareTo(DateUtil.format(iT)) > 0 ){
                            chargeRecord.setStatus("2");
                            recordService.updateStatus(chargeRecord);
                        }
                    }
                } else {
                    Date nowTime = new Date();
                    String iT = DateUtil.format(invalidTime, "yyyy-MM-dd") + " 23:59:59";
                    if(nowTime.compareTo(DateUtil.format(iT)) > 0 ){
                        chargeRecord.setStatus("2");
                        recordService.updateStatus(chargeRecord);
                    }
                }
            }
        }
    }

}
