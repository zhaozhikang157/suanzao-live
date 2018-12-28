package com.huaxin.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 字符串工具类
 * @author syl
 *
 */
public class StringUtil {
	public static int getInteger(Object str,int def){
		String s = getString(str);
		int dot = s.indexOf('.');
		if(dot!=-1){
			s = s.substring(0, dot);
		}
		if(!"".equals(s)){
			try{
				return Integer.parseInt(s);
			}catch(Exception e){
				return def;
			}
		}
		return def;
	}
	
	public static String format2ArrayStr(Object objs[]){
		StringBuffer sb = new StringBuffer();
		if(objs!=null){
			for(int i=0;i<objs.length;i++){
				sb.append(StringUtil.getString(objs[i]));
				if(i!=objs.length-1){
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}
	
	public static Date getDate(Object str){
		String s = getString(str);
		if(!"".equals(s)){
			try{
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.parse(s);
			}catch(Exception e){
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 是否存在某个整型
	 * @param array
	 * @param element
	 * @return
	 */
	public static boolean existInt(int array[],int element){
		if(array==null){
			return false;
		}
		for(int tmp:array){
			if(tmp==element){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否存在某个字符串
	 * @param array
	 * @param element
	 * @return
	 */
	public static boolean existString(String array[],String element){
		if(array==null){
			return false;
		}
		for(String tmp:array){
			if(tmp.equals(element)){
				return true;
			}
		}
		return false;
	}
	
	public static Date getDate(Object str,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = getString(str);
		if(!"".equals(s)){
			try{
				return sdf.parse(s);
			}catch(Exception e){
				return null;
			}
		}
		return null;
	}
	
	public static String getString(Object str){
		if(str==null){
			return "";
		}
		return String.valueOf(str);
	}
	
	public static String getString(String str,String str1){
		if(str==null || "".equals(str)){
			return str1;
		}
		return str;
	}
	
	public static String getString(Object obj,String str1){
		if(obj==null || "".equals(obj.toString())){
			return str1;
		}
		return obj.toString();
	}
	public static int[] parseIntegerArray(Object str){
		List<Integer> list = new ArrayList<Integer>();
		if(str==null || "".equals(str.toString())){
			return new int[0];
		}
		String sp[]= getString(str).split(",");
		int l[] = new int[0];
		int tmp = 0;
		for(int i=0;i<sp.length;i++){
			tmp = getInteger(sp[i], 0);
			list.add(tmp);
		}
		l = new int[list.size()];
		for(int i=0;i<list.size();i++){
			l[i] = list.get(i);
		}
		return l;
	}
	
	public static String[] parseStringArray(Object str){
		String sp[]= getString(str).split(",");
		return sp;
	}
	
	public static String getString4Null(Object str){
		if(str==null){
			return null;
		}
		return getString(str);
	}
	/**
	 * 过滤空的字符串 如果为空 则返回后面的值
	 * @param str1 要过滤的字符串
	 * @param str2 空串的时候被替换的字符串
	 * @return
	 */
	public static String fileEmptyString(String str1,String str2){
		if(str1==null || "".equals(str1)){
			return str2;
		}
		return str1;
	}
	public static long getLong(Object str,long def){
		String s = getString(str);
		if(!"".equals(s)){
			try{
				return Long.parseLong(s);
			}catch(Exception e){
				return def;
			}
		}
		return def;
	}
	
	public static Float getFloat(Object str,Float def){
		String s = getString(str);
		if(!"".equals(s)){
			try{
				return Float.parseFloat(s);
			}catch(Exception e){
				return def;
			}
		}
		return def;
	}
	
	public static Byte getByte(Object str,Byte def){
		String s = getString(str);
		if(!"".equals(s)){
			try{
				return Byte.parseByte(s);
			}catch(Exception e){
				return def;
			}
		}
		return def;
	}
	
	
	public static double getDouble(Object str,double def){
		String s = getString(str);
		if(!"".equals(s)){
			try{
				return Double.parseDouble(s);
			}catch(Exception e){
				return def;
			}
		}
		return def;
	}
	
	/**
	 * 将字符串转换为新字符串，处理查询sql语句（以逗号分隔，添加单引号）
	 * @param str
	 * @return
	 */
	public static String getSqlStringParse(String str){
		String[] strArray = str.split(",");
		String newStr = "";
		for (int i = 0; i < strArray.length; i++) {
			if(Utility.isNullorEmpty(strArray[i])){
				continue;
			}
			if(newStr.equals("")){
				newStr = "'" + strArray[i]+  "'" ;
			}else{
				
				newStr = newStr + ",'" + strArray[i]  + "'" ;
			}
		}
		return newStr;
	}
	
	/**
	 * 格式化ID字符串数组
	 * @param ids
	 * @return
	 */
	public static String formatIdsQuote(String ids){
		String arr[] = parseStringArray(ids);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<arr.length;i++){
			sb.append(arr[i]);
			if(i!=arr.length-1){
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 大字段转换string
	 * @param obj
	 * @return
	 */
	public static String clob2String(Object obj){
		if(obj==null){
			return "";
		}
		if(obj instanceof String){
			return getString(obj);
		}else{
			String reString = "";
	        Reader is = null;
	        BufferedReader br = null;
	        Clob clob = (Clob) obj;
			try {
				is = clob.getCharacterStream();
				br = new BufferedReader(is);
		        String s = br.readLine();
		        StringBuffer sb = new StringBuffer();
		        while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
		            sb.append(s);
		            s = br.readLine();
		        }
		        reString = sb.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(br!=null){
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return reString;
		}
	}

	/**
	 * 对象属性转换为字段  例如：userName to user_name
	 * @param property 字段名
	 * @return
	 */
	public static String propertyToField(String property) {
		if (null == property) {
			return "";
		}
		char[] chars = property.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			if (CharUtils.isAsciiAlphaUpper(c)) {
				sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 字段转换成对象属性 例如：user_name to userName
	 * @param field
	 * @return
	 */
	public static String fieldToProperty(String field) {
		if (null == field) {
			return "";
		}
		char[] chars = field.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '_') {
				int j = i + 1;
				if (j < chars.length) {
					sb.append(StringUtils.upperCase(CharUtils.toString(chars[j])));
					i++;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	* @Title: readStream
	* @Description: TODO(读取输入流转成byte[])
	* @param inStream
	* @return
	* @throws Exception
	* @return byte[]    返回类型
	 */
	public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len=inStream.read(buffer))!=-1){
            outStream.write(buffer,0,len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
	/**
	 * 替换手机号中间4位
	 * @param mobile
	 * @return
	 */
	public static String mobileReplce4Str(String mobile) {
	    if (mobile == null) {
	        return null;
	    }
	   return  mobile.substring(0,mobile.length()-(mobile.substring(3)).length())+"****"+mobile.substring(7);
	}

	/**
	 * 数字超过10000 用X.X万显示
	 * @return
	 */
	public static String numberStr(BigDecimal bd){
		String nuberStr = "0";
		if(bd.compareTo(new BigDecimal(10000))==1||bd.compareTo(new BigDecimal(10000))==0){
			BigDecimal divide = bd.divide(new BigDecimal(10000));
			nuberStr = divide.setScale(1,BigDecimal.ROUND_HALF_UP).toString()+"万";
		}else{
			return bd.toString();
		}
		return nuberStr;
	}
	public static void main(String[] args){
		System.out.println(numberStr(new BigDecimal(156781)));
	}
}
