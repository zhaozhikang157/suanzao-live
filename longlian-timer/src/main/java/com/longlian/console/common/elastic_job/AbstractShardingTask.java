package com.longlian.console.common.elastic_job;

import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.ShardingContext;

/**
 * 任务分片抽象类
 * 把多个任务分配到多个服务器上面运行
 * 
 * @author zzp
 * @date 2018年04月19日 下午13:00:02
 */
public abstract class AbstractShardingTask extends AbstractTask {

	/**
	 * 任务分片
	 */
	@Autowired(required = false)
	public TaskSharding taskSharding;
	
	/**
	 * 执行作业
	 * 
	 * @param shardingContext  作业运行时多片分片上下文
	 */
	@Override
	public void execute(ShardingContext shardingContext) {
		// 任务分片计算，是否允许执行
		if (null == taskSharding || taskSharding.allowRun(shardingContext)) {
			super.execute(shardingContext);
		}
	}
	
}
