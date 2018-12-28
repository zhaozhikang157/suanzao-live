package com.huaxin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by U on 2016/5/19.
 */
public class EndDateParameteUtil {

    /**
     * 将结束时间解析为 yyyy-MM-dd 23:59:59
     * @param date
     * @return
     */
    public static Date parserEndDate(Date date){
        if(date!=null){
            String dateStr= DateUtil.format(date, "yyyy-MM-dd")+" 23:59:59";
            return DateUtil.parseDate(dateStr);
        }
        return null;
    }
}

