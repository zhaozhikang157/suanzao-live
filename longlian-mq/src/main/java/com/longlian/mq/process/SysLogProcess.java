package com.longlian.mq.process;

import com.huaxin.util.EmailUtil;
import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;

import com.longlian.model.SystemLog;
import com.longlian.mq.service.SystemLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志 处理
 */
@Service("sysLogProcess")
public class SysLogProcess extends LongLianProcess {
	
	@Autowired
	RedisUtil  redisUtil;
	@Autowired
	SystemLogService systemLogService;

	@Value("${syslog.threadCount:10}")
	private  int threadCount=10;
	private Logger logg = LoggerFactory.getLogger(SysLogProcess.class);

	private class GetData extends DataRunner{
		public GetData(LongLianProcess longLianProcess, RedisUtil redisUtil, String redisKey) {
			super(longLianProcess, redisUtil, redisKey);
		}

		@Override
		public void process(String msg) throws Exception {
			SystemLog ud=JsonUtil.getObject(msg, SystemLog.class);
			systemLogService.insert(ud);
		}

	}

	@Override
	public void addThread() {
		GetData t1 = new GetData(this, redisUtil , RedisKey.ll_log_sync_save_key);
		threadPool.execute(t1);
	}

	@Override
	public int getThreadCount() {
		return this.threadCount;
	}

	
}
