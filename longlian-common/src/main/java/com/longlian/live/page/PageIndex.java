package com.longlian.live.page;

/**
 * @ClassName: PageIndex 
 * @Description: 分页
 * @author: zhangzhipeng
 * @date: 2016年7月5日 下午5:50:50
 */
public class PageIndex {
	private long firstIndex;
	private long endIndex;
	
	public PageIndex() {
		
	}
	public PageIndex(long firstIndex, long endIndex) {
		this.firstIndex = firstIndex;
		this.endIndex = endIndex;
	}
	public long getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(long endIndex) {
		this.endIndex = endIndex;
	}
	public long getFirstIndex() {
		return firstIndex;
	}
	public void setFirstIndex(long firstIndex) {
		this.firstIndex = firstIndex;
	}
	

}
