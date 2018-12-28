package com.longlian.console.common.elastic_job;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * 简单任务抽象类
 * 不需要分片所有任务都在同一台服务器上运行
 * author:zzp
 */
public abstract class AbstractTask implements SimpleJob {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractTask.class);

	/**
	 * 执行作业
	 * 
	 * @param shardingContext
	 */
	@Override
	public void execute(ShardingContext shardingContext) {
		TaskContext.getTaskContext().setShardingContext(shardingContext);
		// 任务开始时间
		long start = System.currentTimeMillis();
		String taskName = this.getTaskName();
		if (StringUtils.isEmpty(taskName)) {
			taskName = shardingContext.getJobName();
		}
		logger.info("任务【{}】执行开始！", taskName);
		try {
			// 执行任务
			this.doExecute();
		} catch (Exception e) {
			logger.error("任务【{}】执行失败！", taskName, e);
		} finally {
			// 任务结束时间
			long end = System.currentTimeMillis();
			logger.info("任务【{}】执行结束！耗时：{}秒", taskName, (end - start) / 1000.000);
			TaskContext.remove();
		}
	}

	/**
	 * 任务名称
	 * 
	 * @return
	 */
	public abstract String getTaskName();
	
	/**
	 * 具体执行的任务
	 */
	public abstract void doExecute();

}
