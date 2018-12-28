package com.longlian.live.util;

import com.huaxin.util.DateUtil;
import com.huaxin.util.Utility;

import java.util.Date;

/**
 * Created by Administrator on 2017/2/18.
 */
public class OrderUtil {

    /**
     * 获取订单唯一编号
     * @param id
     * @return
     */
    public static String getOrderNumber(long id){
        String date = Utility.getCurDateTimeStr("yyyyMMdd");
        String orderNumber = "sz_" + date + Utility.getNumCode(0,7) +"" +id;
        return orderNumber;
    }

    /**
     * 获取银联提现 商业流水号
     */
    public static String  getBankOutMerSeqId4Random(){
        String merDate = DateUtil.format(new Date(), "yyyyMMdd");
        String seqIdStr =  Utility.getNumCode(0, 8) ;
        String merSeqId = merDate + seqIdStr;
        return  merSeqId;
    }

    public static void main(String[] args) {
        String ss = "玩转酸枣在线方法和技巧！\n" +
                "  你可以吗";
    }
}
