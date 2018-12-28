package com.longlian.console.util;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by admin on 2016/9/9.
 * 时间 加减操作
 */
public class DateAddWha {
    /**
     * 天数增加
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date,int day){
        if(date!=null){
            GregorianCalendar gc =new GregorianCalendar();
            gc.setTime(date);
            gc.add(5, day);
            return gc.getTime();
        }
        return date;
    }
}
