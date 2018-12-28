package com.longlian.live.util;

import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.SpringContextUtil;
import com.longlian.live.util.log.LogRequestInfo;
import com.longlian.live.util.log.RequestInfoContext;
import com.longlian.model.SystemLog;
import com.longlian.token.AppUserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @ClassName:SysLogUtils
 * @author ?
 */
public class SystemLogUtil {
	private static final Logger logger = LoggerFactory.getLogger(SystemLogUtil.class);
	
	/**
	 * 插入系统日志
	 * @param type 日志类型：SysLogUtils.OPTION_TYPE 操作日志；SysLogUtils.EXCEPTION_TYPE 异常日志
	 * @param comment 操作描述private
	 * long shopId;//店铺Id
     * private long employeeId;            //操作人id
    *  private String employeeName;        //操作人姓名
    *  private String level;               //操作级别 默认为 0-警告 1-严重 2-核心
    *  private String type;                // 操作的类型 枚举
    *  private String object;              //操作对象
    *   private String comment;             //操作备注
    *  private String tableType;//日志反查，特殊的业务表关联 枚举类
    *  private long tableId;//表Id
	 */
	public static void saveSystemLog(int type , String systemType , long opId , String opName   ,  String object , String comment , String tableType , long tableId) {
		SystemLog sLog = new SystemLog();
		sLog.setUserId(opId);
		sLog.setUserName(opName);
		sLog.setLogType(String.valueOf(type));
		sLog.setSystemType(systemType);
		sLog.setObject(object);
		sLog.setContent(comment);
		sLog.setTableId(tableId);
		sLog.setTableType(tableType);
		saveLog(sLog) ;
	}
	
	public static void saveSystemLog(int type , String systemType , long opId , String opName  ,  String object , String comment ) {
		saveSystemLog(type, systemType, opId, opName, object, comment, "", 0l);
	}
	
	public static void saveLog(SystemLog log) {
		HttpServletRequest rq = SpringContextUtil.getCurRequest();
		log.setIpAddress(IPUtil.getClientAddress(rq));
		log.setLogTime(new Date());
		RedisUtil.getRedisUtil().lpush(RedisKey.ll_log_sync_save_key, JsonUtil.toJson(log));
	}


	/**
	 * 设置日志拦截 请求对象
	 * @param request
	 */
	public static void setLoginRequestInfo(HttpServletRequest request){
		LogRequestInfo info = new LogRequestInfo();
		info.setRequest(request.getParameterMap());
		AppUserIdentity userIdentity = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
		if(userIdentity != null){
			info.setId(userIdentity.getId());
			info.setUserName(userIdentity.getName());
			info.setUserId(userIdentity.getId());
		}
		RequestInfoContext.setRequestInfo(info);
	}

}
