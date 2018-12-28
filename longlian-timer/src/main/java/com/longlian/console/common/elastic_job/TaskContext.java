package com.longlian.console.common.elastic_job;

import com.dangdang.ddframe.job.api.ShardingContext;

/**
 * 任务上下文
 */
public class TaskContext {
	
	private static final ThreadLocal<TaskContext> localContext = new ThreadLocal<TaskContext>() {
		@Override
		protected TaskContext initialValue() {
			return new TaskContext();
		}
	};
	
	/**
	 * 分片上下文
	 */
	private ShardingContext shardingContext;
    
    private TaskContext() {
		
	}
    
    public static TaskContext getTaskContext() {
        return localContext.get();
    }
	
	public static void remove() {
        localContext.remove();
    }

	public ShardingContext getShardingContext() {
		return shardingContext;
	}

	public void setShardingContext(ShardingContext shardingContext) {
		this.shardingContext = shardingContext;
	}

}
