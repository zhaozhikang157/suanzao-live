package com.longlian.console.service.impl;

import com.huaxin.util.ActResult;
import com.longlian.console.dao.VisitCourseRecordStatMapper;
import com.longlian.console.service.VisitCourseRecordService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by admin on 2017/2/24.
 */
@Service("visitCourseRecordStatService")
public class VisitCourseRecordServiceImpl implements VisitCourseRecordService {
    private static Logger log = LoggerFactory.getLogger(VisitCourseRecordServiceImpl.class);
    @Autowired
    VisitCourseRecordStatMapper visitCourseRecordMapper;
    
   public ActResult queryFriendCircleCountByFromType( String beginTimeStr , String endTimeStr){
       ActResult actResult=new ActResult();
       Map friendCircleMap  = new HashMap<>();
       Map friendMap  = new HashMap<>();
       Map totalMap  = new HashMap<>();

       Date startTime = null;
       Date endTime = null;
       try {
           startTime = DateUtils.parseDate(beginTimeStr +" 00:00:00" , "yyyy-MM-dd HH:ss:mm");
           endTime = DateUtils.parseDate(endTimeStr +" 23:59:59" , "yyyy-MM-dd HH:ss:mm");
       } catch (ParseException e) {
           e.printStackTrace();
       }

       List<Map> friendCircleCount = visitCourseRecordMapper.queryFriendCircleCountByFromType(startTime,endTime);
       List<Map>  friendCount =visitCourseRecordMapper.queryFriendCountByFromType(startTime,endTime);
       List<Map>  totalCount =visitCourseRecordMapper.queryCountByFromType(startTime,endTime);
       for(Map map :friendCircleCount)
       {
           friendCircleMap.put(map.get("createTime"),map.get("count")) ;
       }
       for(Map map :friendCount)
       {
           friendMap.put(map.get("createTime"),map.get("count")) ;
       }
       for(Map map :totalCount)
       {
           totalMap.put(map.get("createTime"),map.get("count")) ;
       }
       System.out.print("friendCircleCount"   + friendCircleCount);
       friendCircleMap = this.commonQueryUsing(friendCircleMap);
       friendMap = this.commonQueryUsing1(friendMap);
       totalMap = this.commonQueryUsing2(totalMap);
       Map map = new HashMap<>();
       map.put("friendCircleMap",friendCircleMap);
       map.put("friendMap",friendMap);
       map.put("totalMap",totalMap);
       actResult.setData(map);
       return  actResult;
   }

  
    private   Map commonQueryUsing(Map map){
        List<Map.Entry<String, String>> infoIds =
                new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for(Map.Entry<String, String>   mapStr:infoIds){
            keys.add(mapStr.getKey());
            values.add(mapStr.getValue());
        }
        Map mapStr = new HashMap();
        mapStr.put("keys", keys);
        mapStr.put("values", values);
        return mapStr;
    }
    private   Map commonQueryUsing1(Map map){
        List<Map.Entry<String, String>> infoIds =
                new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for(Map.Entry<String, String>   mapStr:infoIds){
            keys.add(mapStr.getKey());
            values.add(mapStr.getValue());
        }
        Map mapStr = new HashMap();
        mapStr.put("keys1", keys);
        mapStr.put("values1", values);
        return mapStr;
    }
    private   Map commonQueryUsing2(Map map){
        List<Map.Entry<String, String>> infoIds =
                new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for(Map.Entry<String, String>   mapStr:infoIds){
            keys.add(mapStr.getKey());
            values.add(mapStr.getValue());
        }
        Map mapStr = new HashMap();
        mapStr.put("keys2", keys);
        mapStr.put("values2", values);
        return mapStr;
    }
}
