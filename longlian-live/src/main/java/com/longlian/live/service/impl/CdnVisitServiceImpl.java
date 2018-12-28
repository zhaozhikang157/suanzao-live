package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.longlian.dto.DataChargeRecordDto;
import com.longlian.live.dao.CdnVisitMapper;
import com.longlian.live.service.CdnVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by admin on 2017/11/3.
 */
@Service()
public class CdnVisitServiceImpl implements CdnVisitService {
    private static Logger log = LoggerFactory.getLogger(CdnVisitServiceImpl.class);

    private final BigDecimal k = new BigDecimal(1024l);
    private final BigDecimal m =  new BigDecimal(1048576l);
    private final BigDecimal g =  new BigDecimal(1073741824l);
    private final BigDecimal t =  new BigDecimal(1099511627776l);


    @Autowired
    CdnVisitMapper cdnVisitMapper;

    @Override
    public Map getAllCourseFlowPage(Integer pageSize, Integer offset, Long roomId) {
        DataGridPage page = new DataGridPage();
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        if (offset == null) {
            offset = 0;
        }
        page.setPageSize(pageSize);
        page.setOffset(offset);
        List<Map> list = cdnVisitMapper.getAllCourseFlowPage(page, roomId);
        Set<String> set = new HashSet<>();
        for (Map map : list) {
            String month = DateUtil.format((Date) map.get("startTime"), "yyyy-MM-dd");
            set.add(month);
        }
        Iterator<String> iterator = set.iterator();
        Map<String, Object> map = new HashMap<String, Object>();
        if (list.size() < pageSize && list.size() > 0) {
            map.put("isMore", 1);
        } else if (list.size() == 0) {
            map.put("isMore", 2);
        } else {
            map.put("isMore", 0);
        }
        String nowMonthStr = DateUtil.format(new Date(), "yyyy-MM-dd");
        Date beaferTime = DateUtil.getBeaferTime(new Date());
        String strBeafer = DateUtil.format(beaferTime, "yyyy-MM-dd");
        while (iterator.hasNext()) {
            String str = iterator.next();
            String strYear = str.substring(0, 4);
            String strMonth = str.substring(5, 7);
            List<Map> arrayList = new ArrayList<Map>();
            for (Map cdvMap : list) {
                String monthStart = DateUtil.format((Date) cdvMap.get("startTime"), "yyyy-MM-dd");
                String monthEnd = DateUtil.format((Date) cdvMap.get("endTime"), "yyyy-MM-dd");
                if (strMonth.equals(monthStart.substring(5, 7)) && strYear.equals(monthStart.substring(0, 4))) {
                    String start = DateUtil.format((Date) cdvMap.get("startTime"), "yyyy-MM-dd HH:mm");
                    String end = DateUtil.format((Date) cdvMap.get("endTime"), "yyyy-MM-dd HH:mm");
                    String smm = start.substring(11, start.length());
                    String emm = "";
                    if("null".equals(end)){
                        emm = "至今";
                        if ((nowMonthStr).equals(monthStart)) {
                            cdvMap.put("st", "今天 " + smm);
                            cdvMap.put("et",  emm);
                        } else if (strBeafer.equals(monthStart)) {
                            cdvMap.put("st", "昨天 " + smm);
                            cdvMap.put("et",  emm);
                        } else {
                            cdvMap.put("st", monthStart.substring(5, monthStart.length()) + " " + smm);
                            cdvMap.put("et",  emm);
                        }
                    }else{
                        emm = end.substring(11, end.length());
                        String st = smm;
                        String et = emm;
                        String sm = monthStart;
                        String em = monthEnd;
                        if(((Date) cdvMap.get("startTime")).compareTo((Date) cdvMap.get("endTime")) > 0){
                            st = emm;
                            et = smm;
                            monthStart = em;
                            monthEnd = sm;

                        }
                        String s = "";
                        String e = "";
                        if(nowMonthStr.equals(monthStart)){
                            s = "今天 " + st;
                        }else if(strBeafer.equals(monthStart)){
                            s = "昨天 " + st;
                        }else{
                            s = monthStart.substring(5, monthStart.length()) + " " + st;
                        }
                        if(nowMonthStr.equals(monthEnd)){
                            e = "今天 " + et;
                        }else if(strBeafer.equals(monthEnd)){
                            e = "昨天 " + et;
                        }else{
                            e = monthEnd.substring(5, monthEnd.length()) + " " + et;
                        }
                        cdvMap.put("st", s);
                        cdvMap.put("et", e);
                    }
                    BigDecimal size = new BigDecimal(String.valueOf(cdvMap.get("size")));
                    if (size.compareTo(t)>=0) {
                        cdvMap.put("s", size.divide(t, 2, RoundingMode.HALF_UP) + "T");
                    } else if (size.compareTo(g)>=0) {
                        cdvMap.put("s", size.divide(g, 2, RoundingMode.HALF_UP) + "G");
                    } else if (size.compareTo(m)>=0) {
                        cdvMap.put("s", size.divide(m, 2, RoundingMode.HALF_UP) + "M");
                    } else if (size.compareTo(k)>=0) {
                        cdvMap.put("s", size.divide(k, 2, RoundingMode.HALF_UP) + "KB");
                    } else {
                        cdvMap.put("s", size.setScale(2) + "B");
                    }
                    arrayList.add(cdvMap);
                }
            }
            map.put(strYear + strMonth, arrayList);
        }
        return map;
    }
}
