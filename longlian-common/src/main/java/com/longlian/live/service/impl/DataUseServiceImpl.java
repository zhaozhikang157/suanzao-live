package com.longlian.live.service.impl;

import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.DataChargeRecordService;
import com.longlian.live.service.DataUseService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.model.CourseBaseNum;
import com.longlian.model.DataChargeRecord;
import com.longlian.model.LiveRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhan on 2017-11-08.
 */
@Service("dataUseService")
public class DataUseServiceImpl implements DataUseService {
    private static Logger log = LoggerFactory.getLogger(DataUseServiceImpl.class);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    DataChargeRecordService dataChargeRecordService;
    @Autowired
    private RedisLock redisLock;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public  void useData(Long roomId, Long useData) {
        //200毫秒轮讯一次 ， 2秒算超时
        boolean flag = redisLock.lock(RedisKey.ll_liveroom_usedata_lock + roomId, 200 * 1000, 5);
        //获取锁失败，
        if (!flag) {
            log.info("获取锁{}失败!", RedisKey.ll_liveroom_usedata_lock + roomId);
            GlobalExceptionHandler.sendEmail("获取锁" + RedisKey.ll_liveroom_usedata_lock + roomId + "失败", "注意");
        } else {
            try {
                List<DataChargeRecord> recordList = dataChargeRecordService.getAllRecordByRoomId(Long.valueOf(roomId));
                LiveRoom liveRoom = liveRoomService.findById(Long.valueOf(roomId));
                if (liveRoom == null) {
                    return;
                }
                long reduceDataCount = liveRoom.getReduceDataCount();
                log.info("roomId:{},消耗流量：{},原欠流量：{}", roomId, useData, reduceDataCount);
                //应该抵扣多少
                useData += reduceDataCount;

                //抵扣
                useData = this.getUseData(recordList, useData);
                //抵扣、未消耗完
                if (useData >= 0) {
                    liveRoomService.updateReduceDataCountById(roomId, useData);
                }
                log.info("roomId:{},未消耗完流量：{}", roomId, useData);
            }finally{
                redisLock.unlock(RedisKey.ll_liveroom_usedata_lock + roomId);
            }
        }
    }

    /**
     * 消耗列表数据
     * @param recordList
     * @param useData
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public  Long getUseData(List<DataChargeRecord> recordList, Long useData) {
        if (recordList != null && recordList.size() > 0) {
            int i = 0;
            do {
                DataChargeRecord chargeRecord = recordList.get(i);
                long roomDayBalance = chargeRecord.getBalAmount() - useData;
                log.info("消耗前：{}", JsonUtil.toJson(chargeRecord));
                if (roomDayBalance == 0) {
                    //改为消耗完--失效
                    chargeRecord.setStatus("2");
                    chargeRecord.setUsedAmount(chargeRecord.getUsedAmount() + useData);
                    chargeRecord.setBalAmount(0l);
                    //chargeRecord.setUseOriginAmount(chargeRecord.getUseOriginAmount() + useData);
                    //正好够低扣，保存后，应该跳出去
                    log.info("");
                    useData = 0l;
                    i = 0 ;
                } else if (roomDayBalance > 0) {
                    //够低扣，应该跳出去
                    chargeRecord.setUsedAmount(chargeRecord.getUsedAmount() + useData);
                    chargeRecord.setBalAmount(roomDayBalance);
                    //chargeRecord.setUseOriginAmount(chargeRecord.getUseOriginAmount() + useData);
                    //够低扣，保存后，应该跳出去
                    i = 0 ;
                    useData = 0l;
                } else {
                    //不够低扣的
                    useData = useData - chargeRecord.getBalAmount();
                    //改为消耗完--失效
                    chargeRecord.setStatus("2");
                    //先把余额加上去
                    chargeRecord.setUsedAmount(chargeRecord.getBalAmount() + chargeRecord.getUsedAmount());
                    chargeRecord.setBalAmount(0l);
                    //chargeRecord.setUseOriginAmount(chargeRecord.getUseOriginAmount() + chargeRecord.getBalAmount());
                    i++;
                }
                log.info("消耗后：{}", JsonUtil.toJson(chargeRecord));
                dataChargeRecordService.updateStatus(chargeRecord);
//                System.out.println(JsonUtil.toJson(chargeRecord));
            } while (i < recordList.size() && i != 0 );
        }
        return useData;
    }

    public   DataChargeRecord getRd( Long bal , Long use) {
        DataChargeRecord rd = new DataChargeRecord();
        rd.setStatus("1");
        rd.setBalAmount(bal);
        rd.setUsedAmount(use);
        return rd;
    }

    public static void main(String[] args) {
        DataUseServiceImpl impl = new DataUseServiceImpl();
        List<DataChargeRecord> recordList = new ArrayList<>();
        recordList.add(impl.getRd(66L, 10L));
        recordList.add(impl.getRd(20L, 0L));
        recordList.add(impl.getRd(20L, 0L));
        System.out.println(impl.getUseData(  recordList, 80L)); ;
    }
}
