package com.longlian.live.service.impl;

import com.longlian.live.newdao.DataChargeRecordMapper;
import com.longlian.live.service.DataFlowService;
import com.longlian.model.DataChargeLevel;
import com.longlian.model.LiveRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuhan on 2017-10-27.
 */
@Service
public class DataFlowServiceImpl implements DataFlowService {
    @Autowired
    private DataChargeRecordMapper dataChargeRecordMapper;

    /**
     * 流量统计根据roomId
     *
     * @param roomId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public long getCountByLiveRoom(Long roomId) {
        Long count = dataChargeRecordMapper.getBalAmount(roomId);
        Long reduce = dataChargeRecordMapper.getReduce(roomId);
        return count - reduce;
    }

    /**
     * 流量充值
     *
     * @param totalCount
     * @param level
     * @return
     */
    @Override
    public boolean dataCharge(Long totalCount, DataChargeLevel level) {
         

        return false;
    }

    /**
     * 流量已使用
     *
     * @param useCount
     * @param useDate
     * @return
     */
    @Override
    public boolean dataUse(Long useCount, String useDate) {
        return false;
    }
}
