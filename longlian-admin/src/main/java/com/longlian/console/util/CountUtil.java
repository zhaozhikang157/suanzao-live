package com.longlian.console.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

/**
 * Created by liuhan on 2017-07-15.
 */
public class CountUtil {

    /**
     * 计算平均数
     * @param time
     * @param users
     * @return
     */
    public static  BigDecimal avg(Long time , Long users) {
        //人均停留
        BigDecimal avg  = new BigDecimal(0l);
        if (users != 0l) {
            avg = new BigDecimal(time).divide(new BigDecimal(users), 0, RoundingMode.HALF_UP);
        }
        return avg;
    }

    /**
     * 向m中增加多少
     * @param m
     * @param pageId
     * @param addCount
     */
    public static void mapAdd(Map<Long , Long> m , Long pageId , Long addCount) {
        if ( m.containsKey(pageId)){
            m.put(pageId , m.get(pageId) + addCount);
        } else {
            m.put(pageId , addCount);
        }
    }


    private static String getDateHmStr() {
        String dateStr = DateFormatUtils.format(new Date(), "HH:mm");
        return dateStr;
    }
    private static String getDateStr() {
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return dateStr;
    }


}
