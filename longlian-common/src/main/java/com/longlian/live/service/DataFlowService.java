package com.longlian.live.service;

import com.longlian.model.DataChargeLevel;
import com.longlian.model.DataChargeRecord;

/**
 * Created by liuhan on 2017-10-27.
 */
public interface DataFlowService {
    /**
     * 流量统计根据roomId
     * @param roomId
     * @return
     */
    public long getCountByLiveRoom(Long roomId);

    /**
     * 流量充值
     * @param totalCount
     * @param level
     * @return
     */
    public boolean dataCharge(Long totalCount , DataChargeLevel level);

    /**
     * 流量已使用
     * @param useCount
     * @param useDate
     * @return
     */
    public boolean dataUse(Long useCount , String useDate);
}
