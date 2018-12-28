package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.live.newdao.DataUseRecordMapper;
import com.longlian.live.service.DataUseRecordService;
import com.longlian.live.service.DataUseService;
import com.longlian.model.DataUseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-11-13.
 */
@Service("dataUseRecordService")
public class DataUseRecordServiceImpl implements DataUseRecordService{

    private final BigDecimal k = new BigDecimal(1024l);
    private final BigDecimal m =  new BigDecimal(1048576l);
    private final BigDecimal g =  new BigDecimal(1073741824l);
    private final BigDecimal t =  new BigDecimal(1099511627776l);

    @Autowired
    DataUseRecordMapper dataUseRecordMapper;
    @Override
    public void save(DataUseRecord record) {
        dataUseRecordMapper.insert(record);
    }

    @Override
    public List<Map> getAllUseFlowPage(DatagridRequestModel requestModel, Long roomId,String beginTime , String et , String courseName) {
        Date startTime = DateUtil.format(beginTime);
        Date endTime = DateUtil.format(et);
        List<Map> list = dataUseRecordMapper.getAllUseFlowPage(requestModel,roomId,startTime,endTime,courseName);
        for(Map map : list ){
            BigDecimal size = new BigDecimal(String.valueOf(map.get("flow")));
            if (size.compareTo(t)>=0) {
                map.put("s", size.divide(t,2, RoundingMode.HALF_UP)+ "T");
            } else if (size.compareTo(g)>=0) {
                map.put("s", size.divide(g, 2, RoundingMode.HALF_UP) + "G");
            } else if (size.compareTo(m)>=0) {
                map.put("s", size.divide(m, 2, RoundingMode.HALF_UP) + "M");
            } else if (size.compareTo(k)>=0) {
                map.put("s", size.divide(k, 2, RoundingMode.HALF_UP) + "KB");
            } else {
                map.put("s", size.setScale(2) + "B");
            }
        }
        return list;
    }
}
