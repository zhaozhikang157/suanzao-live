package com.huaxin.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.http.client.utils.DateUtils;

import com.alibaba.fastjson.serializer.BigDecimalCodec;

/**
 * object转换工具类
 * @author lh
 *
 */
public class ObjectUtil {
    /**
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {    
        if(obj == null)  
            return null;      
  
        Map<String, Object> map = new HashMap<String, Object>();   
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {    
            String key = property.getName();    
            if (key.compareToIgnoreCase("class") == 0) {   
                continue;  
            }  
            Method getter = property.getReadMethod();  
            Object value = getter!=null ? getter.invoke(obj) : null;  
            map.put(key, value);  
        }    
  
        return map;  
    }  
    /**
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, String> objectToStringMap(Object obj) throws Exception {    
        if(obj == null)  
            return null;      
  
        Map<String, String> map = new HashMap<String, String>();   
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {    
            String key = property.getName();    
            if (key.compareToIgnoreCase("class") == 0) {   
                continue;  
            }  
            Method getter = property.getReadMethod();  
            Object value = getter!=null ? getter.invoke(obj) : null;  
            
            Class c = property.getPropertyType();
            String toValue = "";
            if (c == Long.class || c == long.class) {
                if (value == null) {
                    value = new Long( 0l); 
                } 
                toValue = value.toString();
            } else if (c == BigDecimal.class) {
                if (value == null) {
                    value = new BigDecimal(0);
                 }
                toValue = value.toString();
            } else if (c == Date.class) {
                if (value == null) {
                    toValue = "";
                 } else {
                    toValue = DateUtils.formatDate((Date)value);
                 }
            } else if (c == Integer.class  || c == int.class) {
                if (value == null) {
                    value = new Integer(0); 
                } 
                toValue = value.toString();
            }else if (c == Boolean.class  || c == boolean.class) {
                if (value == null) {
                    toValue = "";
                }else {
                    toValue = value.toString();
                }
            } else {
                if (value == null) {
                    value = ""; 
                } 
                toValue =  (String)value;
            }
            map.put(key, toValue);  
        }    
        return map;  
    }  
    
    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {    
        if (map == null)   
            return null;    
  
        Object obj = beanClass.newInstance();  
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {  
            Method setter = property.getWriteMethod();    
            if (setter != null) {  
                Class c = property.getPropertyType();
                String value = map.get(property.getName());
                Object toValue = value;
                if (c == Long.class || c == long.class) {
                    toValue = Long.parseLong(value);
                } else if (c == BigDecimal.class) {
                    if(value==null){
                        value = "0";
                    }
                    toValue = new BigDecimal(value);
                } else if (c == Date.class) {
                    if (!"".equals(value))
                        toValue = DateUtils.parseDate(value);
                    else {
                        toValue = null;
                    }
                } else if (c == Integer.class  || c == int.class) {
                    toValue = Integer.parseInt(value);
                } else if (c == Boolean.class  || c == boolean.class) {
                    if (!"".equals(value))
                        toValue = Boolean.valueOf(value);
                    else {
                        toValue = null;
                    }

                }
                setter.invoke(obj, toValue);
            }  
        }  
  
        return obj;  
    }    
}
