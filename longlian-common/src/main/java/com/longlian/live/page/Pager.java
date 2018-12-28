package com.longlian.live.page;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * <p>Description: 分页对象 </p>
 * @author su_jian
 * @version 1.0
 */
public class Pager<T> implements Serializable{

	private static final long serialVersionUID = 1950105867049549100L;
	
	private List<T> list;
	@JSONField(serialize=false)
	private T objectT;
	private int currentPage = 1;// 当前页
	private int pageSize = 10;
	private int totalPage;
	private int totalRecord;
	private String orderByFiledId;
	private boolean isAsc = true;
	
	public Pager() {
	}
	
	public Pager(T objectT){
		this.objectT = objectT;
	}

	public T getObjectT() {
		return objectT;
	}

	public void setObjectT(T objectT) {
		this.objectT = objectT;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		int totalPage = totalRecord % getPageSize() == 0 ? totalRecord / getPageSize() : totalRecord / pageSize + 1;
		this.setTotalPage(totalPage);
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	@Override
	public String toString() {
		return "Page [list=" + list + ", objectT=" + objectT + "]";
	}

	public String getOrderByFiledId() {
		return orderByFiledId;
	}

	public void setOrderByFiledId(String orderByFiledId) {
		this.orderByFiledId = orderByFiledId;
	}

	public boolean isAsc() {
		return isAsc;
	}

	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}
}
