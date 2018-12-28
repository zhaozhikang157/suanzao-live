package com.longlian.console.common.elastic_job;

import com.dangdang.ddframe.job.api.ShardingContext;

/**
 * 任务数据项分片抽象类
 * 把任务拆成多个数据项执行
 * 
 * @author zzp
 * @date 2018年04月19日 下午13:00:02
 */
public abstract class AbstractShardingItemTask extends AbstractTask {
	
	/**
	 * 具体执行的任务
	 * 
	 * @param shardingContext
	 */
	public abstract void doExecute(ShardingContext shardingContext);
	
	/**
	 * 具体执行的任务
	 */
	@Override
	public void doExecute() {
		this.doExecute(TaskContext.getTaskContext().getShardingContext());
	}

}
