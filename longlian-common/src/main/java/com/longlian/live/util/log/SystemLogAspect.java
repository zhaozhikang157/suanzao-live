package com.longlian.live.util.log;

import com.huaxin.util.spring.SpringContextUtil;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.model.SystemLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


@Aspect
@Component
@Order(0)
public class SystemLogAspect {

	// 本地异常日志记录对象
	private final static Logger LOGGER = LoggerFactory.getLogger(SystemLogAspect.class);

	@Pointcut("@annotation(com.longlian.live.util.log.Log)")
	public void accessAspect() {
	}
	
	@Pointcut("execution(* com.longlian.live.*Service.*(..))")
	public void throwingAspect(){
	}

	@AfterReturning(value = "accessAspect()", returning = "rtv" )
	public void doAfterReturning(JoinPoint joinPoint, Object rtv) {

		saveLog(joinPoint, rtv);
	}
	@AfterThrowing(value="throwingAspect()",throwing="e")
	public void doAfterThrowing(JoinPoint joinPoint,Throwable e){
		LOGGER.error(e.getLocalizedMessage());
	}
	protected void saveLog(JoinPoint joinPoint, Object rtv){
		try {
			LoggingAntProcessor loggingAntProcessor = new LoggingAntProcessor();
			LogRequestInfo info  = RequestInfoContext.getRequestInfo();
			if(info != null ){
				if(!info.isCreateLog()) return;;
				Object returned = null;//方法返回值
				String logDesc = null;//日志描述
				Method method =  ((MethodSignature) joinPoint.getSignature()).getMethod();
				Object[] arguments = joinPoint.getArgs();
				logDesc = loggingAntProcessor.renderTemplate(info, method, arguments, rtv);
				//HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
				HttpServletRequest rq = SpringContextUtil.getCurRequest();
				Log log = method.getAnnotation(Log.class);
				SystemLog sLog = new SystemLog();
				sLog.setLogType(log.type().getType() + "");
				sLog.setDeviceNo(log.deviceNo());
				sLog.setSystemType(log.systemType() + "");
				sLog.setUserId(info.getId());
				sLog.setUserName(info.getUserName());
				sLog.setContent(logDesc);
				sLog.setTableId(info.getTableId());
				sLog.setTableType(info.getTableType());
				SystemLogUtil.saveLog(sLog);
			}

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}

}
