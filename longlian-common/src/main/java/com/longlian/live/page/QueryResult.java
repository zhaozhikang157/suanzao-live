package com.longlian.live.page;

import java.util.List;


/**
* @Description:分页 
* @author zhouxiaolong
* @date 2014-11-13 上午12:34:28
* @version V1.0
 */
public class QueryResult <T>{
	private List<T> result;
	private long totalrecord;
	public List<T> getResult() {
		return result;
	}
	public void setResult(List<T> result) {
		this.result = result;
	}
	public long getTotalrecord() {
		return totalrecord;
	}
	public void setTotalrecord(long totalrecord) {
		this.totalrecord = totalrecord;
	}
	

}
