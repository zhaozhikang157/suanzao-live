package com.huaxin.util.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static <T> T getBeanByType(Class<T> beanClass) {
        return SpringContextUtil.applicationContext.getBean(beanClass);
    }

    public static Object getBeanByName(String beanName) {
        return SpringContextUtil.applicationContext.getBean(beanName);
    }
    
    /**
	 * @Title: getCurRequest
	 * @Description:(获得当前的request) 
	 * @param:@return 
	 * @return:HttpServletRequest
	 */
	public static HttpServletRequest getCurRequest(){
		RequestAttributes requestAttributes = null;
		try {
			requestAttributes = RequestContextHolder.currentRequestAttributes();
		} catch (IllegalStateException ex) {
			//ex.printStackTrace();
		}

		if(requestAttributes != null && requestAttributes instanceof ServletRequestAttributes){
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)requestAttributes;
			return servletRequestAttributes.getRequest();
		}
		return null;
	}
}
