package com.longlian.mq.process;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.MessageClient;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.model.Course;
import com.longlian.mq.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 直播推流，通知客服手机号，处理
 */
@Service("livePushNotityProcess")
public class LivePushNotityProcess extends LongLianProcess{
	
	@Autowired
	RedisUtil  redisUtil;

	private  int threadCount=10;

	@Autowired
	MessageClient messageClient;

	private Logger logg=LoggerFactory.getLogger(LivePushNotityProcess.class);
	@Value("${livePushNotity.mobiles:}")
	private String mobiles = "";
	@Autowired
	CourseService courseService;

	@Autowired
	AppUserCommonService appUserCaommonService;

	@Override
	public void addThread() {
		GetData t1 = new GetData(this, redisUtil , RedisKey.ll_live_push_notify);
		threadPool.execute(t1);
	}

	@Override
	public int getThreadCount() {
		return this.threadCount;
	}

	private class GetData  extends DataRunner{
		public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
			super(longLianProcess, redisUtil, redisKey);
		}

		@Override
		public void process(String msg) throws Exception {
			Map map  = JsonUtil.getObject(msg, HashMap.class);

			String action = (String)map.get("action");
			String courseId = (String)map.get("courseId");

			if ("publish".equals(action) || "connected".equals(action)) {
				Course course = courseService.getCourseFromRedis(Long.parseLong(courseId));

				Map user = appUserCaommonService.getUserInfoFromRedis(course.getAppId());

				String[] aMobiles = mobiles.split(",");
				for (String mobile : aMobiles) {
					if (!StringUtils.isEmpty(mobile)) {
						String content = "老师："+user.get("name")+"的课程《" + course.getLiveTopic() + "》正在直播，课程开课设置时间为："+DateUtil.format(course.getStartTime())+"，请准时关注！【酸枣在线】";
						messageClient.sendMessage(mobile, content);
					}
				}
			}
		}

	}

	
}
