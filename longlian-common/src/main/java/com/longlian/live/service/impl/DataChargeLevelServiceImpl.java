package com.longlian.live.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.SystemLogDto;
import com.longlian.live.newdao.DataChargeLevelMapper;
import com.longlian.live.newdao.DataChargeRecordMapper;
import com.longlian.live.service.DataChargeLevelService;
import com.longlian.model.DataChargeRecord;
import com.longlian.model.SystemLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import com.longlian.model.DataChargeLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Created by admin on 2017/10/31.
 */
@Service("dataChargeLevelService")
public class DataChargeLevelServiceImpl implements DataChargeLevelService {
    private final BigDecimal k = new BigDecimal(1024l);
    private final BigDecimal  m =  new BigDecimal(1048576l);
    private final BigDecimal g =  new BigDecimal(1073741824l);
    private final BigDecimal t =  new BigDecimal(1099511627776l);
    private static Logger log = LoggerFactory.getLogger(DataChargeLevelServiceImpl.class);
    @Autowired
    DataChargeLevelMapper dataChargeLevelMapper;
    @Autowired
    DataChargeRecordMapper dataChargeRecordMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public ActResultDto getDataChargeLevelList(Long userId) {
        ActResultDto result = new ActResultDto();
        String dataChargeLevelRedis = redisUtil.get(RedisKey.ll_data_charge_level);
        List<DataChargeLevel> redisList = null;
        if (StringUtils.isNotEmpty(dataChargeLevelRedis)) {
            redisList = JSONArray.parseArray(dataChargeLevelRedis, DataChargeLevel.class);
        } else {
            redisList = dataChargeLevelMapper.getDataChargeLevelList();
            redisUtil.set(RedisKey.ll_data_charge_level, JsonUtil.toJson(redisList));
        }
        List<Map> ListMap = new ArrayList<Map>();
        Map map = null;
        if(redisList.size()>0){
            for(DataChargeLevel dataChargeLevel :redisList ){
                map = new HashMap<>();
                map.put("id",dataChargeLevel.getId());
                map.put("amount",dataChargeLevel.getAmount());  //流量
                map.put("prefPrice",dataChargeLevel.getPrefPrice()); //优惠价
                String time = "";
                if("0".equals(dataChargeLevel.getInvalidDateUnit())){
                    time = "天";
                }else  if("1".equals(dataChargeLevel.getInvalidDateUnit())){
                    time = "个月";
                }else{
                    time = "年";
                }
                map.put("invalidTime",dataChargeLevel.getInvalidDate()+time);  //有效期
         
                String chargeFlag = "";
                if("1".equals(dataChargeLevel.getIsHot())){
                    chargeFlag = "1";
                }else if("1".equals(dataChargeLevel.getIsRetail())){
                    chargeFlag = "2";
                }else{
                    chargeFlag = "3";
                }
                map.put("chargeFlag",chargeFlag);
                ListMap.add(map);
            }
        }
        String banlanceAmounts = "0";
        DecimalFormat df = new DecimalFormat("#0.00");
        BigDecimal banlanceAmount =  new BigDecimal(dataChargeRecordMapper.getBanlanceByUserId(userId));
        BigDecimal  reduceDataCount =  new BigDecimal(dataChargeRecordMapper.getReduceDataCountbByAppId(userId));
        String unit = "";
        if(!Utility.isNullorEmpty(banlanceAmount) && !Utility.isNullorEmpty(reduceDataCount)) {

            if (banlanceAmount.compareTo(reduceDataCount) == -1) {
                banlanceAmount = reduceDataCount.subtract(banlanceAmount);
                if (banlanceAmount.compareTo(t) >= 0) {
                    banlanceAmounts ="-" +banlanceAmount.divide(t,2, RoundingMode.HALF_UP);
                    unit = "T";
                } else if (banlanceAmount.compareTo(g) >= 0) {
                    banlanceAmounts ="-" +banlanceAmount.divide(g,2, RoundingMode.HALF_UP);
                    unit = "G";
                } else if (banlanceAmount.compareTo(  m) >= 0)  {
                    banlanceAmounts ="-" +banlanceAmount.divide(m,2, RoundingMode.HALF_UP);
                    unit = "M";
                } else if (banlanceAmount.compareTo(  k) >= 0)  {
                    banlanceAmounts ="-" +banlanceAmount.divide(k,2, RoundingMode.HALF_UP);
                    unit = "KB";
                }
            } else if (banlanceAmount.compareTo(reduceDataCount)==1) {
                banlanceAmount = banlanceAmount.subtract(reduceDataCount);
                if (banlanceAmount.compareTo(t) >= 0) {
                    banlanceAmounts = banlanceAmount.divide(t,2, RoundingMode.HALF_UP)+"";
                    unit = "T";
                } else if (banlanceAmount.compareTo(g) >= 0) {
                    banlanceAmounts = banlanceAmount.divide(g,2, RoundingMode.HALF_UP) + "";
                    unit = "G";
                } else if (banlanceAmount.compareTo(  m) >= 0)  {
                    banlanceAmounts = banlanceAmount.divide(m,2, RoundingMode.HALF_UP) + "";
                    unit = "M";
                } else if (banlanceAmount.compareTo(  k) >= 0)  {
                    banlanceAmounts = banlanceAmount.divide(k,2, RoundingMode.HALF_UP)+ "";
                    unit = "KB";
                }
            }else{ 
                banlanceAmounts = "0";
            }
            if(banlanceAmounts.contains(".")){
                String newBanlanceAmounts=banlanceAmounts.substring(banlanceAmounts.indexOf(".")+1);
                if(newBanlanceAmounts.startsWith("0") && newBanlanceAmounts.endsWith("0")){
                    banlanceAmounts = banlanceAmounts.substring(0,banlanceAmounts.indexOf("."));
                }else if(!newBanlanceAmounts.startsWith("0") && newBanlanceAmounts.endsWith("0")){
                    banlanceAmounts = banlanceAmounts.substring(0,banlanceAmounts.indexOf(".")+2);
                }
            }
        }
        result.setData(ListMap);
        result.setExt(banlanceAmounts+unit);
        return result;
    }
    @Override
    public DataChargeLevel findModelById(Long id) {
        String dataChargeLevelRedis = redisUtil.get(RedisKey.ll_data_charge_level);
        List<DataChargeLevel> redisList = null;
        if (StringUtils.isNotEmpty(dataChargeLevelRedis)) {
            redisList = JSONArray.parseArray(dataChargeLevelRedis, DataChargeLevel.class);
        } else {
            redisList = dataChargeLevelMapper.getDataChargeLevelList();
            redisUtil.set(RedisKey.ll_data_charge_level, JsonUtil.toJson(redisList));
        }
        DataChargeLevel dataCharge = null;
        if(redisList.size()>0) {
            for (DataChargeLevel dataChargeLevel : redisList) {
                if (dataChargeLevel.getId() == id) {
                    dataCharge = dataChargeLevel;
                }
            }
        }
        return dataCharge;
    }
    @Override
    public List<Map> getDataChargeLevelList(DatagridRequestModel datagridRequestModel,Map map){
        List<Map>  redisList = dataChargeLevelMapper.getDataChargeLevelListPage(datagridRequestModel, map);
        return redisList;
    }
    @Override
    public void  doSaveAndUpdate(DataChargeLevel dataChargeLevel) throws Exception{
        if(null == dataChargeLevel.getId()){
            dataChargeLevel.setCreateTime(new Date());
            dataChargeLevel.setStatus("1");
            dataChargeLevelMapper.insert(dataChargeLevel);
          
        }else{
            dataChargeLevelMapper.updateByPrimaryKeySelective(dataChargeLevel);
        }
    }
    @Override
    public void deleteById(String ids) throws Exception{
        dataChargeLevelMapper.deleteByIds(ids);
    }
    @Override
    public  DataChargeLevel findById(long id){
        return   dataChargeLevelMapper.selectByPrimaryKey(id);
    }
    @Override
    public  void dataCharge(DataChargeRecord dataChargeRecord){
        dataChargeRecordMapper.insert(dataChargeRecord);
    }
}
