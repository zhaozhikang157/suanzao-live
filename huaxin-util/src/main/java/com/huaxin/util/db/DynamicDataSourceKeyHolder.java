package com.huaxin.util.db;

/**
 * 动态数据源 安全线程设置
 * @author syl 2017-1-10
 */
public class DynamicDataSourceKeyHolder {
	
	private static final ThreadLocal<String> dataSourceHolder = new ThreadLocal<String>();

	/**
	 * 设置
	 * @param key
	 */
	public static void setDataSourceKey(String key) {
		dataSourceHolder.set(key);
	}

	/**
	 * 获取
	 * @return
	 */
	public static String getDataSourceKey() {
		return dataSourceHolder.get();
	}

	/**
	 * 清空
	 */
	public static void clearDataSourceKey(){
		dataSourceHolder.remove();
	}
}
