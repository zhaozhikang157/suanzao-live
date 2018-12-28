import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class Ognl {
    public Ognl() {
    }

    @SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
        if(o == null) {
            return true;
        } else {
            if(o instanceof String) {
                if(((String)o).length() == 0) {
                    return true;
                }
            } else if(o instanceof Collection) {
                if(((Collection)o).isEmpty()) {
                    return true;
                }
            } else if(o.getClass().isArray()) {
                if(Array.getLength(o) == 0) {
                    return true;
                }
            } else {
                if(!(o instanceof Map)) {
                    return false;
                }

                if(((Map)o).isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }
    public static boolean isNotSeries(Object o){
        if(!isEmpty(0)){ //不为空
            if("0".equals(o+"")){
                return true;
            }
        }else{ //为空
            return true;
        }
        return false;
    }
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }
    public static boolean course(Object o) {
        if (!isEmpty(o) && Long.valueOf(o.toString())!=2){
            return true;
        }else {
            return false;

        }
    }

    public static boolean isBlank(String s) {
        return StringUtils.isBlank(s);
    }

    public static boolean isNotBlank(String s) {
        return StringUtils.isNotBlank(s);
    }

    /**
     * 资金池：判断多选服务类型
     * @param serviceType
     * @param id
     * @return
     */
    public static boolean findServiceType(String serviceType , String id){
        return  serviceType.indexOf(id)!=-1;
    }

    public static boolean findServiceTypeNE(String serviceType , String id){
        return  serviceType.indexOf(id)==-1;
    }

    /**
     * 判断是否选用综合排序--0是
     * @param sort
     * @return
     */
    public static boolean isSortComprehensive(String sort){
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(sort)){
            if("0".equals(sort)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否选用人气排序--1是
     * @param sort
     * @return
     */
    public static boolean isSortPopularity(String sort){
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(sort)){
            if("1".equals(sort)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否选用时间排序--2是
     * @param sort
     * @return
     */
    public static boolean isSortTime(String sort){
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(sort)){
            if("2".equals(sort)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否选用价格排序--3是
     * @param sort
     * @return
     */
    public static boolean isSortAmt(String sort){
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(sort)){
            if("3".equals(sort)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否选用正序--0是
     * @param asc
     * @return
     */
    public static boolean isSortAsc(String asc){
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(asc)){
            if("0".equals(asc)){
                return true;
            }
        }
        return false;
    }  /**
     * 判断是否选用倒序--1是
     * @param desc
     * @return
     */
    public static boolean isSortDesc(String desc){
        if(cn.jpush.api.utils.StringUtils.isNotEmpty(desc)){
            if("1".equals(desc)){
                return true;
            }
        }
        return false;
    }
}
