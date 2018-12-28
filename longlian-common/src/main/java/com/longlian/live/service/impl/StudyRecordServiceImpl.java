package com.longlian.live.service.impl;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.CourseBaseNumMapper;
import com.longlian.live.dao.StudyRecordMapper;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.StudyRecordService;
import com.longlian.live.service.UserRewardRecordService;
import com.longlian.model.Course;
import com.longlian.model.CourseBaseNum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-05-26.
 */
@Service("studyRecordService")
public class StudyRecordServiceImpl implements StudyRecordService {
    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Autowired
    private CourseBaseService courseBaseService;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    UserRewardRecordService userRewardRecordService;

    @Autowired
    CourseBaseNumMapper courseBaseNumMapper;


    /**
     * 学习统计
     *
     * @param courseId
     * @return
     */
    @Override
    public long getStudyRecordCount(Long courseId) {
        return this.getCountByCourseId(courseId);
        //return studyRecordMapper.findByCourseId(courseId);
    }

    @Override
    public void addUser2Redis(Long courseId, Long userId, Date createTime, boolean isVirtualUser) {
        loadCourseUser(courseId);
        String key = RedisKey.ll_visit_course_user_key + courseId;

        Long date = createTime.getTime();
        //是虚拟用户,-5年时间，，让它们排在实际用户后面
        if (isVirtualUser) {
            date  =  date -   5l * 365 * 24 * 60 * 60 * 1000;
        }

        redisUtil.zadd(key, String.valueOf(userId), date );

        Course  c = courseBaseService.getCourseFromRedis(courseId);

        //是系列课下面的单节课
        if (c.getSeriesCourseId() > 0 ) {
            //redisUtil.expire(key , 24 * 60 * 60 * 5);//放到队列处理
            //加入课程计数处理,将最新的数据写入course表中的joinCount字段
            redisUtil.lpush(RedisKey.ll_visit_count_wait2db, String.valueOf(courseId));
            Map sendMsg = new HashMap();
            sendMsg.put("courseId",String.valueOf( courseId));
            sendMsg.put("userId" , String.valueOf(userId));
            redisUtil.lpush(RedisKey.ll_course_visit_user_deal , JsonUtil.toJson(sendMsg));
        }

    }



    /**
     * 加载对应的加入课程的人员到redis
     * @param courseId
     */
    public void loadCourseUser(Long courseId){
        String key = RedisKey.ll_visit_course_user_key + courseId;
        if (redisUtil.exists(key)){
            return ;
        }

        String lockKey = RedisKey.ll_join_lock_pre + courseId;
        if (redisLock.lock(lockKey , 200 * 1000, 6)) {
            try {
                key = RedisKey.ll_visit_course_user_key + courseId;
                if (redisUtil.exists(key)){
                    return ;
                }
                List<Map> res = studyRecordMapper.getCourseStudyUser(courseId);
                //放入zset里面，方便以后做分页列表,数据统计
                if (res.size() > 0) {
                    //long firstUserTime = 0;
                    for (int i = 0 ;i < res.size() ;i++) {
                        //设置第一个加入用户的时间
                        Map m = res.get(i);
//                        if (i == 0) {
//                            firstUserTime =((Date)m.get("CREATE_TIME")).getTime();
//                            redisUtil.setex(RedisKey.ll_join_course_first_user , 24 * 60 * 60 * 5 , String.valueOf(firstUserTime));
//                        }
                        Long date = ((Date)m.get("CREATE_TIME")).getTime();
                        //是虚拟用户,-5年时间，，让它们排在实际用户后面 ,
                        if ("1".equals( m.get("IS_VIRTUAL_USER"))) {
                            date = date -   5l * 365 * 24 * 60 * 60 * 1000;
                        }

                        redisUtil.zadd(key ,String.valueOf(m.get("APP_ID")) , date );
                        Map m2 = new HashMap();
                        m2.put("id", m.get("APP_ID"));
                        m2.put("name", m.get("NAME"));
                        m2.put("photo", m.get("PHOTO"));
                        redisUtil.hset(RedisKey.ll_user_info ,  String.valueOf(m.get("APP_ID")) , JsonUtil.toJson(m2));
                    }
                    //处理分值，将打赏的值融入列表中，便于提取,用队头的分值－打赏的金额，打赏的越多，就能越排在前面
                    List<Map> userRewards = userRewardRecordService.loadUserReward2Redis(courseId, true);
                    if (userRewards != null) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Calendar.AM_PM , 0);
//                        calendar.set(calendar.get(Calendar.YEAR) , 0, 1 , 0, 0 , 0  );
//                        calendar.set(Calendar.MILLISECOND , 0);

                        for (Map m : userRewards) {
                            String userId = (String)m.get("userId");
                            //加基础100年时间，*100去掉小数点, 让它们排在实际用户前面
                            BigDecimal amout = ((BigDecimal) m.get("amout"));
                            amout = amout.add(new BigDecimal(String.valueOf(100l * 365 * 24 * 60 * 60 * 1000))).multiply(new BigDecimal(100l));
                            //增加5年时间，让它们排在实际用户前面
                            redisUtil.zadd(key ,String.valueOf(userId) ,   amout.doubleValue() );
                        }
                    }

                    //只保存5天时间
                    redisUtil.expire(key , 24 * 60 * 60 * 5);
                }

                //课程参加学习基数,后台设置的
                CourseBaseNum base = courseBaseNumMapper.selectByCourse(courseId , "0");

                long baseCount = 0l;
                if (base != null) {
                    baseCount = base.getCount();
                }

                redisUtil.hset(RedisKey.ll_course_base_join_num, String.valueOf(courseId) , String.valueOf(baseCount));
            } finally {
                redisLock.unlock(lockKey);
            }
        }
    }

    /**
     * 取得课程参加人数
     * @param courseId
     * @return
     */
    @Override
    public long getCountByCourseId(Long courseId) {
        loadCourseUser(courseId);
        //课程参加学习基数,后台设置的
        String value = redisUtil.hget(RedisKey.ll_course_base_join_num, String.valueOf(courseId));
        if (StringUtils.isEmpty(value)) {
            value = "0";
        }

        return redisUtil.zcard(RedisKey.ll_visit_course_user_key + courseId) + Long.parseLong(value);
    }
}
