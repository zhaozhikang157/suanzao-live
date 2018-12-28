package com.huaxin.regexp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class ELExpressionParser {
	private Map<String,Object> cache = new HashMap<String,Object>();
	
	public Object evaluate(Object source,String expression){
		Object value = doExpression(source,expression);
		return value;
	}
	
	private Object doExpression(Object source,String expression){
		if("".equals(expression) || expression==null){
			return source;
		}
		String sp[] = expression.split("\\.");
		Object contextObject = source;
		String token = null;
		for(int i=0;i<sp.length;i++){
			token = sp[i];
			contextObject = getTokenObjectValue(contextObject,token);
		}
		return contextObject;
	}
	private Object getTokenObjectValue(Object contextObject,String token){
		Object obj = null;
		if(contextObject==null){
			return null;
		}
		if(token.endsWith("()")){//is a method
			if(contextObject instanceof Map){//is a map instance
				obj = ((Map)contextObject).get(token);
			}else{//is a simple object but not a instance of array and set
				obj = getTokenObjectValueByMethod(contextObject,token);
			}
		}else{//is a property
			if(contextObject instanceof Map){//is a map instance
				obj = ((Map)contextObject).get(token);
			}else{//is a simple object but not a instance of array and set
				obj = getTokenObjectValueByProperty(contextObject,token);
			}
		}
		return obj;
	}
	
	private Object getTokenObjectValueByMethod(Object contextObject,String methodToken){
		Object obj = null;
		if(contextObject==null){
			return null;
		}
		methodToken = methodToken.replaceAll("\\([A-Za-z0-9]*\\)", "");
		Class clazz = contextObject.getClass();
		String key = "method"+contextObject.hashCode()+methodToken;
		try {
			Method m = (Method) cache.get(key);
			if(m==null){
				m = clazz.getMethod(methodToken, null);
				cache.put(key, m);
			}
			if(m==null){
				return null;
			}
			obj = m.invoke(contextObject, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
	
	private Object getTokenObjectValueByProperty(Object contextObject,String token){
		Object obj = null;
		if(contextObject==null){
			return null;
		}
		Class clazz = contextObject.getClass();
		String key = "property"+contextObject.hashCode()+token;
		try {
			Method m0 = (Method) cache.get(key);
			if(m0==null){
				Method m[] = clazz.getMethods();
				for(Method mm:m){
					if(("get"+token).equalsIgnoreCase(mm.getName())){
						m0 = mm;
						break;
					}
				}
			}
			if(m0!=null){
				obj = m0.invoke(contextObject, null);
			}
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	
}
