package com.longlian.live.util.log;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求信息对象，用于存放一个请求中或者一个线程中的所需数据信息
 * @author syl
 *
 */
public class LogRequestInfo {
	//用户主键
	private long id;

	//用户名
	private String userName;
	//用户ID
	private long userId;
	//用户IP
	private String ipAddress;
	//表类型
	private String tableType;
	private long tableId;//表ID
	private String object; //操作对象

	//是否创建对象
	private boolean isCreateLog = true;//

	//请求数据对象
	private Map request = new HashMap();
	//session数据对象
	private Map session = new HashMap();
	//日志模型
	private Map logModel = new HashMap();
	//日志第一次记录
	private boolean loggingFirst = false;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Map getRequest() {
		return request;
	}

	public void setRequest(Map request) {
		this.request = request;
	}

	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	public Map getLogModel() {
		return logModel;
	}

	public void setLogModel(Map logModel) {
		this.logModel = logModel;
	}

	public boolean isLoggingFirst() {
		return loggingFirst;
	}

	public void setLoggingFirst(boolean loggingFirst) {
		this.loggingFirst = loggingFirst;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public boolean isCreateLog() {
		return isCreateLog;
	}

	public void setIsCreateLog(boolean isCreateLog) {
		this.isCreateLog = isCreateLog;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
}
