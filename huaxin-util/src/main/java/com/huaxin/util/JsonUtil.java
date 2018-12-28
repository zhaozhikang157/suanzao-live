package com.huaxin.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huaxin.util.stereotype.JsonFilter;
import com.huaxin.util.type.OutPutEnum;

/**
 * 
 * @author 
 *
 */
public class JsonUtil {
    static class FastJsonFilter implements PropertyPreFilter {
        private  int type;
        public  FastJsonFilter(){
            type = 0; //0 from web 1 from client
        }
        public  FastJsonFilter(int type){
            this.type=type;
        }


        public boolean apply(JSONSerializer jsonSerializer, Object o, String s) {
            try {
                Field field = o.getClass().getDeclaredField(s);
                if(field == null) return true;
                JsonFilter jf = field.getAnnotation(JsonFilter.class);
                if(jf != null){
                    OutPutEnum oen = jf.value();
                    if(oen == OutPutEnum.ALL_NO_WRITE)
                        return false;
                    if(type == 0)    {
                        if(oen == OutPutEnum.WEB_NO_WRITE)
                            return false;
                    }
                    else{
                        if(oen == OutPutEnum.CLIENT_NO_WRITE)
                            return false;
                    }
                }
            } catch (NoSuchFieldException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return true;
            }
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    

    public static String toJsonString(Object obj,int type) {
        return JSON.toJSONString(obj, new FastJsonFilter(type),
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat);
    }
    
	/**
	 * @param obj
	 * @return
	 */
	public static String toJsonString(Object obj) {
       
        return JSON.toJSONString(obj, new FastJsonFilter(),
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat);
      
    }
	
	/**
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
        return JSON.toJSONString(obj, new FastJsonFilter(),
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat);
      
    }

    /**
     * 转对象
     * @param jsonObject
     * @param pojoClass
     * @param <T>
     * @return
     */
    public static <T> T  getJavaObject(JSONObject jsonObject, Class<T> pojoClass) {
        try{
            return JSON.toJavaObject(jsonObject ,pojoClass);
        }catch(Exception e){
            return null;
        }

    }
	
	/***@param jsonString
	 * @param pojoClass
	 * @return
	 */
	public static <T> T  getObject(String jsonString, Class<T> pojoClass) {
		try{
			 return JSON.parseObject(jsonString, pojoClass);
		}catch(Exception e){
			return null;
		}

	}
	public static JSONObject  getObject(String jsonString) {
		try{
			 return JSON.parseObject(jsonString);
		}catch(Exception e){
			return null;
		}

	}
	public static <T> List<T>  getList(String jsonString, Class<T> pojoClass) {
        return JSON.parseArray(jsonString, pojoClass);

	}
	/***
	 * @param jsonString
	 * @return
	 */
	public static  Map<String,Object> getMap4Json(String jsonString) {
        return  JSON.parseObject(jsonString,new TypeReference<Map<String, Object>>() {});
	}

}
