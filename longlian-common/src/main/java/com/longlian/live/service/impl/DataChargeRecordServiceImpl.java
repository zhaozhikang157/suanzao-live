package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.DataChargeRecordDto;
import com.longlian.live.newdao.DataChargeRecordMapper;
import com.longlian.live.service.DataChargeRecordService;
import com.longlian.model.DataChargeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by admin on 2017/10/31.
 */
@Service
public class DataChargeRecordServiceImpl implements DataChargeRecordService{
    private static Logger log = LoggerFactory.getLogger(DataChargeRecordServiceImpl.class);

    private final BigDecimal k = new BigDecimal(1024l);
    private final BigDecimal m =  new BigDecimal(1048576l);
    private final BigDecimal g =  new BigDecimal(1073741824l);
    private final BigDecimal t =  new BigDecimal(1099511627776l);

    @Autowired
    DataChargeRecordMapper dataChargeRecordMapper;

    @Override
    public DataChargeRecord getModelByOrderId(Long orderId) {
        return dataChargeRecordMapper.findModelByOrderId(orderId);
    }

    @Override
    public void insert(DataChargeRecord dataChargeRecord) {
        dataChargeRecordMapper.insert(dataChargeRecord);
    }

    @Override
    public void updateStatus(DataChargeRecord dataChargeRecord) {
        dataChargeRecordMapper.updateByPrimaryKey(dataChargeRecord);
    }

    @Override
    public List<DataChargeRecord> getAllRecordByRoomId(Long roomId) {
        return dataChargeRecordMapper.getAllRecordByRoomId(roomId);
    }

    @Override
    public List<DataChargeRecord> getAllRecord() {
        DataChargeRecord record = new DataChargeRecord();
        record.setStatus("1");
        return dataChargeRecordMapper.select(record);
    }

    @Override
    public Map getAllRecordByAppIdPage(Integer pageSize , Integer offset , Long appId) {
        if (pageSize == null || pageSize == 0) pageSize = 10;
        if (offset == null) offset = 0;
        DataGridPage dg = new DataGridPage();
        dg.setPageSize(pageSize);
        dg.setOffset(offset);
        List<DataChargeRecordDto> recordList = dataChargeRecordMapper.getAllRecordByAppIdPage(dg,appId);
        Set<String> set = new HashSet<>();
        for(DataChargeRecordDto record : recordList){
            if("1".equals(record.getIsPlatformGift())){
                if(record.getTotalAmount()>0){
                    record.setAmount(new BigDecimal(record.getTotalAmount()).divide(g, 2, RoundingMode.HALF_UP).longValue());
                }else{
                    record.setAmount(0l);
                }
                record.setPrefPrice(new BigDecimal(0));
            }
            String month = DateUtil.format(record.getOrderTime(),"yyyy-MM-dd");
            set.add(month);
        }
        Iterator<String> iterator = set.iterator();
        Map<String , Object> map = new HashMap<String , Object>();
        if(recordList.size() < pageSize && recordList.size() > 0){
            map.put("isMore",1);
        }else if(recordList.size() == 0){
            map.put("isMore",2);
        }else {
            map.put("isMore",0);
        }
        String nowMonthStr = DateUtil.format(new Date(), "yyyy-MM-dd");
        Date beaferTime = DateUtil.getBeaferTime(new Date());
        String strBeafer = DateUtil.format(beaferTime, "yyyy-MM-dd");
        while (iterator.hasNext()) {
            List<DataChargeRecordDto> monthRecord = new ArrayList<DataChargeRecordDto>();
            String str = iterator.next();
            String strYear = str.substring(0,4);
            String strMonth = str.substring(5, 7);
            for(DataChargeRecordDto record : recordList){
                String month = DateUtil.format(record.getOrderTime(), "yyyy-MM-dd");
                if (strMonth.equals(month.substring(5,7)) && strYear.equals(month.substring(0,4))) {
                    String orderTime = DateUtil.format(record.getOrderTime(), "yyyy-MM-dd HH:mm");
                    String mm = orderTime.substring(11, orderTime.length());
                    record.setStatusName(getStatusName(record.getStatus()));
                    int invalid = record.getInvalidDate();
                    if (invalid == 0) {
                        record.setValitTime("永久有效");
                    } else {
                        record.setValitTime("有效期至 " + DateUtil.format(record.getInvalidRealDate(), "yyyy-MM-dd"));
                    }
                    if ((nowMonthStr).equals(month)) {
                        record.setTimeName("今天 " + mm);
                    } else if (strBeafer.equals(month)) {
                        record.setTimeName("昨天 " + mm);
                    } else {
                        record.setTimeName(month.substring(5, month.length()) + " " + mm);
                    }
                    monthRecord.add(record);
                }
            }
            map.put(strYear+strMonth, monthRecord);
        }
        return map;
    }

    @Override
    public List<Map> getAllBuyFlowPage(DatagridRequestModel requestModel, long roomId, String beginTime , String et , Double amount) {
        Date startTime = DateUtil.format(beginTime);
        Date endTime = DateUtil.format(et);
        if(amount == 0.00){
            amount = null;
        }
        List<Map> list = dataChargeRecordMapper.getAllBuyFlowPage(requestModel,roomId,startTime ,endTime ,amount);
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
            BigDecimal usedAmount = new BigDecimal(String.valueOf(map.get("usedAmount")));
            if (usedAmount.compareTo(t)>=0) {
                map.put("usedAmount", usedAmount.divide(t,2, RoundingMode.HALF_UP)+ "T");
            } else if (usedAmount.compareTo(g)>=0) {
                map.put("usedAmount", usedAmount.divide(g, 2, RoundingMode.HALF_UP) + "G");
            } else if (usedAmount.compareTo(m)>=0) {
                map.put("usedAmount", usedAmount.divide(m, 2, RoundingMode.HALF_UP) + "M");
            } else if (usedAmount.compareTo(k)>=0) {
                map.put("usedAmount", usedAmount.divide(k, 2, RoundingMode.HALF_UP) + "KB");
            } else {
                map.put("usedAmount", usedAmount.setScale(2) + "B");
            }
        }
        return list;
    }

    public static String getStatusName(String status){
        if("-2".equals(status)){
            return "未支付";
        }else if("-1".equals(status)){
            return "支付失败";
        }else if("0".equals(status)){
            return "交易成功";
        }else if("1".equals(status)){
            return "交易成功";
        }else if("2".equals(status)){
            return "已过期";
        }else{
            return status;
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long recordId) {
        dataChargeRecordMapper.deleteByIds(recordId);
    }

    @Override
    public DataChargeRecord findById(long id){
      return   dataChargeRecordMapper.selectByPrimaryKey(id);
    }
}
