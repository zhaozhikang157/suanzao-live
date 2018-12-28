package com.longlian.live.util.log;

/**
 * 全局线程
 */
public class RequestInfoContext {
	private static ThreadLocal<LogRequestInfo> globalInfo = new ThreadLocal<LogRequestInfo>();

	public static LogRequestInfo getRequestInfo(){
		return globalInfo.get();
	}

	public static void setRequestInfo(LogRequestInfo requestInfo){
		globalInfo.set(requestInfo);
	}
	
	public static void clear(){
		globalInfo.remove();
	}
}
