package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.*;
import com.longlian.model.Account;
import com.longlian.model.AppUser;
import com.longlian.model.Course;
import com.longlian.type.ReturnMessageType;
import com.longlian.token.AppUserIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by liuhan on 2017-07-12.
 */
@Service("liveService")
public class LiveServiceImpl implements LiveService {
    @Autowired
    AccountService accountService;
    @Autowired
    AppUserCommonService appUserCaommonService;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    StudyRecordService studyRecordService;

    @Autowired
    RedisUtil redisUtil;
    @Override
    public void setLiveInfo(Map result, Course course) {
        //老师收益
        result.put("incomeAmt", getIncome(course.getId(), course.getAppId()) );

        //系列课下面的单节课
        if (course.getSeriesCourseId() >0) {
            List<Map> showUsers = getShowUsers(course.getId(), true);
            if(showUsers==null||showUsers.size()==0){
                result.put("userCount",0);
            }else {
                result.put("userCount", studyRecordService.getCountByCourseId(course.getId()));
            }
            result.put("showUsers", showUsers);
        } else {
            List<Map> showUsers = getShowUsers(course.getId(), false);
            if(showUsers==null||showUsers.size()==0){
                result.put("userCount",0);
            }else {
                result.put("userCount", joinCourseRecordService.getCountByCourseId(course.getId()));
            }
            result.put("showUsers", showUsers);

        }
    }




    @Override
    public BigDecimal getIncome(Long courseId, Long appId ) {
        Account account = accountService.getAccountByAppId(appId);
        return account.getAddTotalAmount();
    }

    /**
     * 取得显示的人员
     * @param courseId
     * @param isSeriesSigCourse 否是系列课下的单节课
     * @return
     */
    @Override
    public List<Map>   getShowUsers(  Long courseId  , boolean isSeriesSigCourse) {
        String key = null;
        //是系列课下的单节课,查询的是学习的人数
        if (isSeriesSigCourse) {
            studyRecordService.loadCourseUser(courseId);
            key = RedisKey.ll_visit_course_user_key + courseId;
        } else {
            joinCourseRecordService.loadJoinCourseUser(courseId);
            key = RedisKey.ll_join_course_user_key + courseId;
        }


        int limit = 100;
//        if (isVertical) {
//            limit = 4;
//        }

        Set<Tuple> tuples = redisUtil.zrevrangeWithScores(key , 0 , limit - 1 );
        List<Map> users = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        //List<Integer> ids100 = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        //StringBuffer sb = new StringBuffer();
        int i = 0 ;
        int index = 0 ;
        if (tuples != null &&  tuples.size() > 0 ) {
            for(Tuple tuple : tuples ) {
                String id = tuple.getElement();
                //前面4人的头像
                if (i < limit) {
                    Double score = tuple.getScore();

                    Map temp = appUserCaommonService.getUserInfoFromRedis(Long.parseLong(id));
                    if (temp != null) {
                        //排前3位
                        if (score < 0 && i < 3 ) {
                            temp.put("index" , (i + 1 ));
                        } else {
                            temp.put("index" , 0);
                        }
                        i++;
                        users.add(temp);
                    }
                    //后面100人
                }
                //else {
                //sb.append(id).append(",");

                // ids100.add(Integer.parseInt(id));
                //}
                index++;
            }
        }

        //if (sb.length() > 0 ) {
        //   sb.deleteCharAt(sb.length() - 1);
        //}

        return users;
        //result.put("showUserids", ids100);
    }

    @Override
    public ActResultDto getUsersByOffset(Long courseId, Integer offset, Integer pageSize) {
        ActResultDto resultDto  = new ActResultDto();

        Course course = courseBaseService.getCourseFromRedis(courseId);
        String key = null;
        //系列课里面的单节课
        if (course.getSeriesCourseId() >0) {
            studyRecordService.loadCourseUser(courseId);
            key = RedisKey.ll_visit_course_user_key + courseId;
        } else {
            joinCourseRecordService.loadJoinCourseUser(courseId);
            key = RedisKey.ll_join_course_user_key + courseId;
        }
        Set<String> ids = redisUtil.zrevrange(key , offset , offset + pageSize - 1);
        List<Map> result = new ArrayList<>();
        for (String id : ids) {
            Map temp = appUserCaommonService.getUserInfoFromRedis(Long.parseLong(id));
            if (temp != null) {
                result.add(temp);
            }
        }
        if(result!=null && result.size()>0){
            resultDto.setData(result);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        }else{
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return resultDto;
    }


    @Override
    public Set<String> getAllUses(Long courseId){
        Course course ;
        if(courseId!=0 && String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            course = courseBaseService.getRelayCourse(courseId);
        }else{
            course = courseBaseService.getCourseFromRedis(courseId);
        }
        String key = null;
        //系列课里面的单节课
        if (course.getSeriesCourseId() >0) {
            studyRecordService.loadCourseUser(courseId);
            key = RedisKey.ll_visit_course_user_key + courseId;
        } else {
            joinCourseRecordService.loadJoinCourseUser(courseId);
            key = RedisKey.ll_join_course_user_key + courseId;
        }
        Set<String> ids = redisUtil.zrevrange(key , 0 ,  - 1);
        return ids;
    }

    private static List<String> getAllUsersArray( String[] arrayIds , Integer pageSize) {
        List<String> result = new ArrayList<>();
        int index = 0;
        for (int i = index ;i < arrayIds.length ;) {
            StringBuffer sb = new StringBuffer();
            for (int j = i ; j < i + pageSize  && j <  arrayIds.length  ;j++) {
                sb.append(arrayIds[j]).append(",");
            }
            if (sb.length() > 0 ) {
                sb.deleteCharAt(sb.length() - 1);
            }
            if (!StringUtils.isEmpty(sb.toString())) {
                result.add(sb.toString());
            }
            i += pageSize ;
        }

        return result;
    }

    /**
     * 取第一页数据
     * @return
     */
    private static String[] getFirstPageUsers(List<String> list  ) {
        return list.get(0).split(",");
    }

    @Override
    public ActResultDto getAllUsers(Long courseId, String ids, Integer pageSize , AppUserIdentity token) {
        String[] arrayIds = null;
        Map allData = new HashMap();
        List<Map> result = new ArrayList<>();

        if (StringUtils.isEmpty(ids)) {
            Set<String> alls = this.getAllUses(courseId);
            boolean isIn = false;
            //自已
            String own = String.valueOf(token.getId());
            //没买过
            if (alls.contains(own)) {
                isIn = true;
                alls.remove(own);
            }

            String[] allIds = alls.toArray(new String[]{});
            String[] newIds = allIds;
            if (isIn) {
                newIds = new String[allIds.length + 1];
                //把自己排到第一
                newIds[0] = own;
                if (allIds.length > 0) {
                    for (int i = 0 ; i < allIds.length ;i++) {
                        newIds[i+1] = allIds[i];
                    }
                }
            }

            List<String> list = getAllUsersArray(newIds  ,   pageSize);
            if (list != null && list.size() > 0 ) {
                arrayIds = getFirstPageUsers(list);
                list.remove(0);
                allData.put("allUser", list);
            } else {
                arrayIds = new String[0];
                allData.put("allUser", new ArrayList<>());
            }
        } else {
            allData.put("allUser", new ArrayList<>());
            arrayIds = ids.split(",");
        }

        for (int i = 0 ;i < arrayIds.length ;i++) {
            String id = arrayIds[i];
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            String userInofStr = redisUtil.hget(RedisKey.ll_user_info, id);
            if (!StringUtils.isEmpty(userInofStr)) {
                Map temp = JsonUtil.getObject(userInofStr , HashMap.class);
                result.add(temp);
            }
        }
        allData.put("users" , result);

        return ActResultDto.success().setData(allData);
    }

    public static void main(String args[]) {
        String[] news = "1,2,3,4,5,6,7,8,9,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56".split(",");

        List<String> alls = new ArrayList<>();
        for (String s :news) {
            alls.add(s);
        }

        Map allData = new HashMap();
        List<Map> result = new ArrayList<>();

        boolean isIn = false;
        //自已
        String own = "18";
        //没买过
        if (alls.contains(own)) {
            isIn = true;
            alls.remove(own);
        }

        String[] allIds = alls.toArray(new String[]{});
        String[] newIds = allIds;
        if (isIn) {
            newIds = new String[allIds.length + 1];
            //把自己排到第一
            newIds[0] = own;
            if (allIds.length > 0) {
                for (int i = 0 ; i < allIds.length ;i++) {
                    newIds[i+1] = allIds[i];
                }
            }

        }
        List<String> list = getAllUsersArray(newIds  ,   10);
        String[] arrayIds = getFirstPageUsers(list);
        list.remove(0);
        allData.put("allUser", list);
        allData.put("users" , arrayIds);

        System.out.println(JsonUtil.toJson(ActResultDto.success().setData(allData)));;

    }
}
