package com.longlian.mq.process;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.YellowService;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.mq.service.CourseService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 直播流截图事件处理方法
 * @author liuhan
 *
 */
@Service
public class LiveImgShotProcess  extends LongLianProcess{


	@Autowired
	private RedisUtil  redisUtil;
	@Value("${liveImgShot.threadCount:10}")
	private  int threadCount=10;

	@Autowired
	private YellowService yellowService;
	@Autowired
	private CourseService courseService;

	private Logger log = LoggerFactory.getLogger(LiveImgShotProcess.class);

	@Override
	public void addThread() {
		 GetData t1 = new  GetData(this, redisUtil , RedisKey.ll_live_img_shot_wait2db);
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
		public void process(String json) throws Exception {
			/*
						 "oss": {
                "bucket": {
                    "arn": "acs:oss:cn-hangzhou:1872266391331581:longlian-live",
                    "name": "longlian-live",
                    "ownerIdentity": "1872266391331581",
                    "virtualBucket": ""},
                "object": {
                    "deltaSize": 4234,
                    "eTag": "A24DE11308227E62C9F93AF7B0C5E265",
                    "key": "images_prod/354/1154/1.jpg",
                    "size": 4234},
                "ossSchemaVersion": "1.0",
                "ruleId": "green-yellow-prod"},
            "region": "cn-hangzhou",
            "requestParameters": {"sourceIPAddress": "121.196.206.80"},
            "responseElements": {"requestId": "5930EA08F9C7D3EB274D5413"},
            "userIdentity": {"principalId": "378841469027963223"}}]}
						 */
			Map map = (Map) JsonUtil.getObject(json, HashMap.class);
			JSONArray array = (JSONArray)map.get("events");
			JSONObject object = array.getJSONObject(0);
			JSONObject oss = object.getJSONObject("oss");
			JSONObject o  = oss.getJSONObject("object");
			String key = o.getString("key");
			JSONObject b  = oss.getJSONObject("bucket");
			String bucketName = b.getString("name");
			log.info("新建图片key:{},bucketname:{}", key , bucketName);
			String[] keys = key.split("/");

			//http://longlian-live.oss-cn-hangzhou.aliyuncs.com/images/57/228/1.jpg
			if (keys.length > 2) {
				long courseId = Long.parseLong(keys[2]);
				String url = LonglianSsoUtil.getUrlByName(key , bucketName);
				log.info("新建图片地址:{}",url);
				//调用绿网检查
				yellowService.yellow(url , courseService.getCourseFromRedis(courseId));
			}
		}
	}


	public static void main(String args[]) {
		String json = "{\"events\": [{\"eventName\": \"ObjectCreated:PostObject\",\"eventSource\": \"acs:oss\",\"eventTime\": \"2017-03-16T08:02:31.000Z\",\"eventVersion\": \"1.0\",\"oss\": {\"bucket\": {\"arn\": \"acs:oss:cn-hangzhou:1872266391331581:longlian-live\",\"name\": \"longlian-live\",\"ownerIdentity\": \"1872266391331581\"},\"object\": {\"deltaSize\": 0,\"eTag\": \"A6CFB0A7DADBAB4703E81F170AF0EEAE\",\"key\": \"images/aaa.jpg\",\"size\": 35720},\"ossSchemaVersion\": \"1.0\",\"ruleId\": \"green-yellow\"},\"region\": \"cn-hangzhou\",\"requestParameters\": {\"sourceIPAddress\": \"110.53.133.106\"},\"responseElements\": {\"requestId\": \"58CA46973DD124568501DC8B\"},\"userIdentity\": {\"principalId\": \"1872266391331581\"}}]}";

		try {
			Map map = (Map) JsonUtil.getObject(json, HashMap.class);
			JSONArray array = (JSONArray)map.get("events");
			JSONObject object = array.getJSONObject(0);
			JSONObject o  = object.getJSONObject("oss").getJSONObject("object");
			String key = o.getString("key");
			String[] keys = key.split("/");
			for (String k : keys) {
				System.out.println(k);
			}
			System.out.println(keys[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
