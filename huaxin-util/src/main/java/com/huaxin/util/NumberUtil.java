package com.huaxin.util;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by longlian007 on 2018/1/15.
 */
public class NumberUtil {
    /**
     * 随机两个数
     * @param maxNumber 传入最大数值
     * @return
     */
    public static int[] randomNumber(int maxNumber){
        Random random = new Random();
        int i = random.nextInt(maxNumber-1);
        int j = 0;
        if(i==0){
            j=i+1;
        }else{
            j=i-1;
        }
        int[] is= new int[]{i,j};
        return is;
    }
    public static void setListMaps(List<Map> maps,RedisUtil redisUtil,boolean l){
        if(redisUtil!=null&&l&&maps!=null&&maps.size()==4){
            redisUtil.del(RedisKey.course_live_weekly_selection_id_cc);
        }
        for (Map map : maps) {//设置时间 今天 明天 或者几天后
            if(redisUtil!=null&&l){
                redisUtil.sadd(RedisKey.course_live_weekly_selection_id_cc, map.get("id").toString());
            }
            if (map.get("startTime") != null) {
                String startTime = map.get("startTime").toString();
                String s = DateUtil.transFormationStringDate(startTime);
                map.put("startTimeStr", s);
                String endTime = null;
                if (map.get("endTime") != null) {
                    endTime = map.get("endTime").toString();
                }
                map.put("statusStr", DateUtil.getStatusStr(map.get("startTime").toString(), endTime));
            } else {
                map.put("startTimeStr", "暂无");
            }
            if(map.get("joinCount") != null&& StringUtils.isNotEmpty(map.get("joinCount").toString())&&Pattern.compile("[0-9]{1,}").matcher(map.get("joinCount").toString()).matches()){
                map.put("joinCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("joinCount").toString())));
            }
           /* if(map.get("chargeAmt") != null){
                map.put("chargeAmt", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("chargeAmt").toString())));
            }*/
        }
    }
    public static BigDecimal trimBigDecimal(BigDecimal bd) {
        String ss  = trim(bd.toString());
        BigDecimal bd2 = new BigDecimal(ss);
        return bd2;
    }

    /**
     *
     * @Description: BigDecimal 去掉后面的0
     * @author wfz .
     * @methodName: trim .
     *
     * @param str
     * @return
     *
     */
    public static String trim(String str) {
        if (str.indexOf(".") != -1 && str.charAt(str.length() - 1) == '0') {
            return trim(str.substring(0, str.length() - 1));
        } else {
            return str.charAt(str.length() - 1) == '.' ? str.substring(0, str.length() - 1) : str;
        }
    }
    public static void setCourseListMap(List<Map> maps){
        for (Map map : maps) {
            String startTime = map.get("startTime").toString();
            String endTime = map.get("endTime").toString();
            if (endTime != null) {
                map.put("startTimeStr", DateUtil.transFormationStringDate(map.get("endTime").toString()));
            } else {
                map.put("startTimeStr", "");
            }
            String liveTimeStatus =DateUtil.getStatusStr(startTime, endTime);
            map.put("liveTimeStatus", liveTimeStatus);
            if(map.get("joinCount") != null){
                map.put("joinCount", com.huaxin.util.StringUtil.numberStr(new BigDecimal(map.get("joinCount").toString())));
            }
        }
    }

}
