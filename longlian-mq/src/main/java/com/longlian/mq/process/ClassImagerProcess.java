package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.SsoUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.model.CourseImg;
import com.longlian.mq.service.CourseService;
import com.longlian.type.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by admin on 2017/8/8.
 */
@Service
public class ClassImagerProcess extends LongLianProcess {
    private static Logger log = LoggerFactory.getLogger(ClassImagerProcess.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    CourseService courseService;

    private int threadCount = 10;

    @Override
    public void addThread() {
        GetData t1 = new GetData(this, redisUtil, RedisKey.ll_class_img_wait2db);
        threadPool.execute(t1);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    private class GetData extends DataRunner {
        public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
            super(longLianProcess, redisUtil, redisKey);
        }

        @Override
        public void process(String msg){
            Map map = JsonUtil.getObject(msg, Map.class);
            long courseId = Long.valueOf(String.valueOf(map.get("courseId")));
            List<Map<Long,String>> serviceIds = JsonUtil.getObject(String.valueOf(map.get("serviceIdList")), List.class);
            for(Map imgMap : serviceIds){
                Set keys = imgMap.keySet();
                Iterator iterator = keys.iterator();
                if (iterator.hasNext()){
                    Object key = iterator.next();
                    String serviceId = imgMap.get(key).toString();
                    try {
                        byte[] bytes = weixinUtil.getTemporaryMediaByMediaid(serviceId);
                        if(bytes!=null){
                            String url = ssoUtil.putObject( courseId + serviceId + ".jpg", bytes);
                            courseService.updateClassImg(url, Long.valueOf(key.toString()));
                        }
                    }catch (Exception e){
                        SystemLogUtil.saveSystemLog(LogType.get_weixin_img_url_file.getType(), "0", courseId, "", "{课程Id:" + courseId + ",serviceId：" + serviceId
                                + "}", "微信上传附件拉取图片更新失败");
                    }
                }
            }
            redisUtil.del(RedisKey.course_img + courseId);
            List<CourseImg> list = courseService.getCourseImgList(courseId);
            if(list != null && list.size() > 0){
                redisUtil.set(RedisKey.course_img + courseId , JsonUtil.toJson(list));
                redisUtil.expire(RedisKey.course_img + courseId , 5*24*60*60);
            }
        }
    }
}
