package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.live.service.UserRewardRecordService;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.Course;
import com.longlian.mq.service.AppUserService;
import com.longlian.mq.service.CourseService;
import com.longlian.type.YunxinCustomMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.util.*;

/**
 * 加入课程处理
 * Created by admin on 2016/10/20.
 */
@Service
public class JoinCourseDealProcess extends LongLianProcess{
    @Autowired
    private RedisUtil redisUtil;
    private  int threadCount=10;
    @Autowired
    JoinCourseRecordService   joinCourseRecordService;
    @Autowired
    YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseService courseService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    AppUserCommonService appUserCaommonService;

    @Autowired
    UserRewardRecordService userRewardRecordService;

    private Logger logg= LoggerFactory.getLogger(JoinCourseDealProcess.class);

    @Override
    public void addThread() {
         GetData t1 = new GetData(this, redisUtil , RedisKey.ll_course_join_user_deal);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }
    private class GetData extends DataRunner{
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg) throws Exception {
            Map map = JsonUtil.getObject(msg , HashMap.class);

            Long courseId = Long.parseLong((String)map.get("courseId"));

            Course course = courseService.getCourseFromRedis(courseId);



            Long userId = Long.parseLong((String)map.get("userId"));

            String key = RedisKey.ll_join_course_user_key + courseId;
            BigDecimal bd =  (BigDecimal)map.get("amout");
            //如果有金额，说明是，打赏过来的
            if (bd != null && bd.compareTo( new BigDecimal(0)) > 0) {


                bd = userRewardRecordService.addUserReward(  courseId ,   userId , bd);
                //在基础上加100年时间，*100去掉小数点
                BigDecimal d = bd.add(new BigDecimal(String.valueOf(100l * 365 * 24 * 60 * 60 * 1000))).multiply(new BigDecimal(100));
                redisUtil.zadd(key ,String.valueOf(userId) ,    d.doubleValue());

                //说明不是，得看一下缓存里有没有appUser,没有得缓存上
            } else {
                //没有用户，则查出来放到缓存
                appUserCaommonService.getUserInfoFromRedis(userId);
            }
            //取前面103位，返回4位有头像+100没有头像 , 竖屏是4个，其它是20个头像
            int limit = 100;
//            if ("1".equals(course.getIsVerticalScreen())) {
//                limit = 4;
//            }


            Set<Tuple> tuples = redisUtil.zrevrangeWithScores(key , 0 , limit - 1 );
            StringBuffer sb = new StringBuffer();
            StringBuffer sb100 = new StringBuffer();
            List<String> ids = new ArrayList<>();
            List<Integer> ids100 = new ArrayList<>();


            List<Double> scores = new ArrayList<>();
            int index = 0 ;
            for(Tuple tuple : tuples) {
                String id = tuple.getElement();

                if (index < limit) {
                    sb.append(id).append(",");
                    ids.add(id);
                    scores.add(tuple.getScore());
                }
                //sb100.append(id).append(",");
                //ids100.add(Integer.parseInt(id));
                index++;
            }
            if (ids.size() > 0 ) {
                sb.deleteCharAt(sb.length() -1);
            }

            //if (sb100.length() > 0 ) {
            //    sb100.deleteCharAt(sb100.length() -1);
            //}

            String now = sb.toString();

            //String now100 = sb100.toString();

            String showUserKey = RedisKey.ll_course_show_users + courseId;

            String showUserAllKey = RedisKey.ll_course_show_all_users + courseId;

            boolean isChange = false;
            if (redisUtil.exists(showUserKey)) {
                String olds = redisUtil.get(showUserKey);
                //前面４位变化
                if (!now.equals(olds)) {
                    isChange = true;
                    //有变化
                    redisUtil.set(showUserKey , now);
                } else {
                    return ;
                }
            } else {
                redisUtil.set(showUserKey , now);
            }

//            if (!isChange && redisUtil.exists(showUserAllKey)) {
//                String olds = redisUtil.get(showUserAllKey);
//                //前面100位变化
//                if (!now100.equals(olds)) {
//                    isChange = true;
//                    //有变化
//                    redisUtil.set(showUserAllKey , now100);
//                }
//            } else {
//                redisUtil.set(showUserAllKey , now100);
//            }
            //没变化
            //if (!isChange) return;

            List<Map> users = new ArrayList<>();
            for(int i = 0 ;i < ids.size() ;i++) {
                String id = ids.get(i);
                Double score = scores.get(i);

                Map temp = appUserCaommonService.getUserInfoFromRedis(Long.parseLong(id));
                if (temp != null) {
                    //排前3位
                    if (score < 0 && i < 3 ) {
                        temp.put("index" , (i + 1 ));
                    } else {
                        temp.put("index" , 0);
                    }
                    users.add(temp);
                }
            }


//            List<Course> list = new ArrayList();
//            //如果是系列课，需要给他下面的课发送,自己不发送
//            if ("1".equals(course.getIsSeriesCourse())) {
//                list = courseService.getCourseBySeriesId(course.getId());
//                for (Course c : list) {
//                    map.put("courseId" ,c.getId() );
//                    redisUtil.lpush(RedisKey.ll_course_join_user_deal , JsonUtil.toJson(c));
//                }
//
//                return;
//            }


            //发送信息云信
            try{
                //是竖屏的话，需要发送，是横屏或者语音的话，不发送
                //if ("1".equals(course.getIsVerticalScreen())) {
                //给相关的聊天室人员发送人员变更消息
                Map msg2 = new HashMap();
                msg2.put("type",  YunxinCustomMsgType.JOIN_USER_CHANGE.getType() );
                Map val = new HashMap();
                val.put("users", users);
                //val.put("userids", ids100);
                msg2.put("data", val);
               // logg.info(JsonUtil.toJson(msg2));
                yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg2));
                //}
            } catch (Exception ex) {
                logg.error("发送信息云信报错",ex);
            }


        }

    }

}
