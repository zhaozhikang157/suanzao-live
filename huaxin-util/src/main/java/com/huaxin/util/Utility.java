package com.huaxin.util;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	// log
	private static Logger log = null;
	// 三个一分组，保留两位小数

	public static final int WITHGROUP = 0;
	// 不分组，保留两位小数
	public static final int WITHOUTGROUP = 1;

	// 数字串的格式（在多用户的环境中使用静态是不合适的）

	// private static DecimalFormat numFormat = new DecimalFormat("#0.00");
	private static DecimalFormat numFormat = new DecimalFormat("#0.00");
	private static DecimalFormat numFormatG = new DecimalFormat("#,##0.00");
	public static String DATE_FORMAT_NOSPLIT = "yyyyMMddHHmmssSSS";

	private static final String[] enSmallNumber = {"", "ONE", "TWO", "THREE",
			"FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN",
			"TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN",
			"SEVENTEEN", "EIGHTEEN", "NINETEEN"};
	private static final String[] enLargeNumber = {"TWENTY", "THIRTY",
			"FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"};
	private static final String[] enUnit = {"", "THOUSAND", "MILLION",
			"BILLION", "TRILLION"};

	/**
	 * 判断是否为空串
	 *
	 * @param str
	 * @return true: null/"" false: 其它
	 */
	public static boolean isNullorEmpty(String str) {
		if (str == null) {
			return true;
		}
		str = str.trim();
		return str.length() < 1;
	}

	/**
	 * 判断BigDecimal是否为null 或者为 0
	 *
	 * @param str
	 * @return true: null/"" false: 其它
	 */
	public static boolean isNullorEmptyBigDecimal(BigDecimal str) {
		if (str == null) {
			return true;
		}
		BigDecimal bigDecimal = new BigDecimal(0);
		if (str.compareTo(bigDecimal) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @author syl
	 * @createTime 2014-1-20
	 * @editTime 下午09:50:30
	 * @desc
	 */
	public static boolean isNullorEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		return "".equals(obj.toString());
	}

	/**
	 * 处理空值的字符串
	 *
	 * @param str
	 * @return
	 */
	public static String null2Empty(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	/**
	 * 处理空值的字符串
	 *
	 * @param str        源字符串
	 * @param defaultStr 默认字符串
	 * @return
	 */
	public static String empty2Default(String str, String defaultStr) {

		if (isNullorEmpty(str)) {
			return defaultStr;
		}
		return str;
	}

	/**
	 * 取得格式化后的字符串
	 *
	 * @param num
	 * @return
	 */
	public static String getFormatedStr(BigDecimal num, int pattern) {
		if (pattern == WITHOUTGROUP) {
			return numFormat.format(num);
		}
		return numFormatG.format(num);
	}

	/**
	 * 取得格式化后的字符串
	 *
	 * @param num
	 * @return
	 */
	public static String getFormatedStr(double num, int pattern) {
		if (pattern == WITHOUTGROUP) {
			return numFormat.format(num);
		}
		return numFormatG.format(num);
	}

	/**
	 * 取得格式化后的字符串
	 *
	 * @return
	 */
	public static String getFormatedStr(String numStr, int pattern) {
		String rtStr = "0.00";
		try {
			double dValue = parseDouble(numStr);
			if (pattern == WITHOUTGROUP) {
				rtStr = numFormat.format(dValue);
			} else {
				rtStr = numFormatG.format(dValue);
			}
		} catch (Exception ex) { // 非数字

		}
		return rtStr;
	}

	/**
	 * 取得当前的时间，格式为 yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getCurDateTimeStr() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 设置一天当最最小的时间  例如 2013-10-1 00:00:00.0
	 * 获取 Calendar yyyy-MM-dd (设置时分秒为0)
	 *
	 * @param cal
	 * @return
	 * @author syl
	 * @date 2014-1-9
	 */
	public static Calendar getMinTimeOfDayCalendar(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		if (cal != null) {
			calendar = cal;
		}
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.DECEMBER, 0);//
		return calendar;
	}


	/**
	 * 设置一天当最最大的时间  例如 2013-10-1 23:59:59.0
	 * 获取 Calendar yyyy-MM-dd (设置时分秒为0)
	 *
	 * @param cal
	 * @return
	 * @author syl
	 * @date 2014-1-9
	 */
	public static Calendar getMaxTimeOfDayCalendar(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		if (cal != null) {
			calendar = cal;
		}
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.DECEMBER, 0);//
		return calendar;
	}

	/***
	 * 根据格式获取字符串
	 * @author syl
	 * @date 2014-1-9
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateStrByFormat(Date date, SimpleDateFormat format) {
		if (date == null) {
			date = new Date();
		}
		if (format == null) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return format.format(date);
	}


	/**
	 * 取得当前的时间，格式为 yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getCurDateTimeStr(String pattern) {
		if (pattern == null) {
			return getCurDateTimeStr();
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}

	/**
	 * 取得当前的时间，格式为 yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getCurDateTimeStr(SimpleDateFormat format) {
		if (format == null) {
			return getCurDateTimeStr();
		}
		return format.format(new Date());
	}


	/**
	 * 取得的时间串，
	 *
	 * @return
	 */
	public static String getDateTimeStr(Date date, String pattern) {
		if (date == null || Utility.isNullorEmpty(pattern)) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 取得的时间串，格式为 yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getDateTimeStr(Date date, SimpleDateFormat format) {
		if (date == null) {
			return "";
		}
		if (format == null) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}
		return format.format(date);
	}

	/**
	 * 取得的时间串，格式为 yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getDateTimeStr(Date date) {
		if (date == null) {
			return getCurDateTimeStr();
		}
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	/**
	 * 取得的日期串，格式为 yyyy-MM-dd
	 *
	 * @return
	 */
	public static String getDateYMDStr(Date date) {
		if (date == null) {
			return getCurDateTimeStr();
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	/**
	 * 取得的英文习惯的时间串，格式为 dd-MMM-yyyy
	 *
	 * @return
	 */
	public static String getDateTimeStrEn(Date date) {
		SimpleDateFormat dateFormatEn = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		if (date == null) {
			return getCurDateTimeStr(dateFormatEn);
		}
		return dateFormatEn.format(date);
	}

	/**
	 * 取得的中文习惯的时间串，格式为 yyyy年MM月dd日
	 *
	 * @return
	 */
	public static String getDateTimeStrCn(Date date) {
		SimpleDateFormat dateFormatCn = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
		if (date == null) {
			return getCurDateTimeStr(dateFormatCn);
		}
		return dateFormatCn.format(date);
	}

	/**
	 * 取得的英文习惯的时间串，格式为 dd-MMM-yyyy
	 *
	 * @return
	 */
	public static String getDateTimeStrEn(String dateStr) throws Exception {
		SimpleDateFormat dateFormatEn = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		if (dateStr == null) {
			return getCurDateTimeStr(dateFormatEn);
		}
		return getDateTimeStrEn(parseDate(dateStr));
	}

	/**
	 * 取得的中文习惯的时间串，格式为 yyyy年MM月dd日
	 *
	 * @return
	 */
	public static String getDateTimeStrCn(String dateStr) throws Exception {
		SimpleDateFormat dateFormatCn = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
		if (dateStr == null) {
			return getCurDateTimeStr(dateFormatCn);
		}
		return getDateTimeStrCn(parseDate(dateStr));
	}

	/**
	 * 取得的英文习惯的时间串，格式为 dd-MMM-yyyy
	 *
	 * @return
	 */
	public static String getDateTimeStrYMEn(Date date) {
		SimpleDateFormat dateFormatYMEn = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
		if (date == null) {
			return getCurDateTimeStr(dateFormatYMEn);
		}
		return dateFormatYMEn.format(date);
	}

	/**
	 * 取得的中文习惯的时间串，格式为 yyyy年MM月dd日
	 *
	 * @return
	 */
	public static String getDateTimeStrYMCn(Date date) {
		SimpleDateFormat dateFormatYMCn = new SimpleDateFormat("yyyy年MM月", Locale.CHINESE);
		if (date == null) {
			return getCurDateTimeStr(dateFormatYMCn);
		}
		return dateFormatYMCn.format(date);
	}

	/**
	 * 取得的英文习惯的时间串，格式为 dd-MMM-yyyy
	 *
	 * @return
	 */
	public static String getDateTimeStrYMEn(String dateStr) throws Exception {
		SimpleDateFormat dateFormatYMEn = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
		if (dateStr == null) {
			return getCurDateTimeStr(dateFormatYMEn);
		}
		return getDateTimeStrYMEn(parseDate(dateStr));
	}

	/**
	 * 取得的中文习惯的时间串，格式为 yyyy年MM月dd日
	 *
	 * @return
	 */
	public static String getDateTimeStrYMCn(String dateStr) throws Exception {
		SimpleDateFormat dateFormatYMCn = new SimpleDateFormat("yyyy年MM月", Locale.CHINESE);
		if (dateStr == null) {
			return getCurDateTimeStr(dateFormatYMCn);
		}
		return getDateTimeStrYMCn(parseDate(dateStr));
	}

	/**
	 * 取得的英文习惯的时间串，格式为 dd-MMM-yyyy
	 *
	 * @return
	 */
	public static String getDateTimeStrMDEn(Date date) {
		SimpleDateFormat dateFormatMDEn = new SimpleDateFormat("dd-MMM", Locale.ENGLISH);
		if (date == null) {
			return getCurDateTimeStr(dateFormatMDEn);
		}
		return dateFormatMDEn.format(date);
	}

	/**
	 * 取得的中文习惯的时间串，格式为 yyyy年MM月dd日
	 *
	 * @return
	 */
	public static String getDateTimeStrMDCn(Date date) {
		SimpleDateFormat dateFormatMDCn = new SimpleDateFormat("MM-dd", Locale.CHINESE);
		if (date == null) {
			return getCurDateTimeStr(dateFormatMDCn);
		}
		return dateFormatMDCn.format(date);
	}

	/**
	 * 取得的英文习惯的时间串，格式为 dd-MMM-yyyy
	 *
	 * @return
	 */
	public static String getDateTimeStrMDEn(String dateStr) throws Exception {
		SimpleDateFormat dateFormatMDEn = new SimpleDateFormat("dd-MMM", Locale.ENGLISH);
		if (dateStr == null) {
			return getCurDateTimeStr(dateFormatMDEn);
		}
		return getDateTimeStrMDEn(parseDate(dateStr));
	}

	/**
	 * 取得的中文习惯的时间串，格式为 yyyy年MM月dd日
	 *
	 * @return
	 */
	public static String getDateTimeStrMDCn(String dateStr) throws Exception {
		SimpleDateFormat dateFormatMDCn = new SimpleDateFormat("MM-dd", Locale.CHINESE);
		if (dateStr == null) {
			return getCurDateTimeStr(dateFormatMDCn);
		}
		return getDateTimeStrMDCn(parseDate(dateStr));
	}

	/**
	 * Date清零
	 *
	 * @param date
	 * @param clearNum 1=毫秒, 2=秒, 3=分钟, 4=小时, 5=天, 6=月份
	 * @return
	 */
	private static Calendar clearDate(Date date, int clearNum) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		// 毫秒
		if (clearNum > 0) {
			cal.set(Calendar.MILLISECOND, 0);
		}
		// 秒

		if (clearNum > 1) {
			cal.set(Calendar.SECOND, 0);
		}
		// 分钟
		if (clearNum > 2) {
			cal.set(Calendar.MINUTE, 0);
		}
		// 小时
		if (clearNum > 3) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
		}
		// 天

		if (clearNum > 4) {
			cal.set(Calendar.DATE, 0);
		}
		// 月份
		if (clearNum > 5) {
			cal.set(Calendar.MONTH, 0);
		}
		return cal;
	}

	/**
	 * 取得指定日期的起始时间串
	 *
	 * @return
	 */
	public static String[] getDateLimitStr() throws Exception {
		return getDateLimitStr(new Date());
	}

	/**
	 * 取得指定日期当周的起始时间串
	 *
	 * @return
	 */
	public static String[] getWeekLimitStr() throws Exception {
		return getWeekLimitStr(new Date());
	}

	/**
	 * 取得指定日期当月的起始时间串
	 *
	 * @return
	 */
	public static String[] getMonthLimitStr() throws Exception {
		return getMonthLimitStr(new Date());
	}

	/**
	 * 取得指定日期当年的起始时间串
	 *
	 * @return
	 */
	public static String[] getYearLimitStr() throws Exception {
		return getYearLimitStr(new Date());
	}

	/**
	 * 取得指定日期的起始时间串
	 *
	 * @param date
	 * @return
	 */
	public static String[] getDateLimitStr(Date date) throws Exception {
		Date[] rtDateArray = getDateLimit(date);
		return new String[]{getDateTimeStr(rtDateArray[0]),
				getDateTimeStr(rtDateArray[1])};
	}

	/**
	 * 取得指定日期当周的起始时间串
	 *
	 * @param date
	 * @return
	 */
	public static String[] getWeekLimitStr(Date date) throws Exception {
		Date[] rtDateArray = getWeekLimit(date);
		return new String[]{getDateTimeStr(rtDateArray[0]),
				getDateTimeStr(rtDateArray[1])};
	}

	/**
	 * 取得指定日期当月的起始时间串
	 *
	 * @param date
	 * @return
	 */
	public static String[] getMonthLimitStr(Date date) throws Exception {
		Date[] rtDateArray = getMonthLimit(date);
		return new String[]{getDateTimeStr(rtDateArray[0]),
				getDateTimeStr(rtDateArray[1])};
	}

	/**
	 * 取得指定日期当年的起始时间串
	 *
	 * @param date
	 * @return
	 */
	public static String[] getYearLimitStr(Date date) throws Exception {
		Date[] rtDateArray = getYearLimit(date);
		return new String[]{getDateTimeStr(rtDateArray[0]),
				getDateTimeStr(rtDateArray[1])};
	}

	/**
	 * 取得指定日期的起始时间
	 *
	 * @param date
	 * @return
	 */
	public static Date[] getDateLimit(Date date) throws Exception {
		Calendar cal = clearDate(date, 4);
		Date date1 = cal.getTime();

		cal.add(Calendar.DATE, 1);
		cal.add(Calendar.SECOND, -1);
		Date date2 = cal.getTime();

		return new Date[]{date1, date2};
	}

	/**
	 * 取得当前日期所在周的第一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = clearDate(date, 4);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		return c.getTime();
	}

	/**
	 * 取得当前日期所在周的最后一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = clearDate(date, 4);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 000);
		return c.getTime();
	}

	/**
	 * 取得指定日期的当周的起始时间
	 *
	 * @param date
	 * @return
	 */
	public static Date[] getWeekLimit(Date date) throws Exception {
		Date date1 = getFirstDayOfWeek(date);
		Date date2 = getLastDayOfWeek(date);
		return new Date[]{date1, date2};
	}

	/**
	 * 取得指定日期的当月起始时间
	 *
	 * @param date
	 * @return
	 */
	public static Date[] getMonthLimit(Date date) throws Exception {
		Calendar cal = clearDate(date, 5);
		cal.set(Calendar.DATE, 1);
		Date date1 = cal.getTime();

		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.SECOND, -1);
		Date date2 = cal.getTime();

		return new Date[]{date1, date2};
	}

	/**
	 * 取得指定日期的当年起始时间
	 *
	 * @param date
	 * @return
	 */
	public static Date[] getYearLimit(Date date) throws Exception {
		Calendar cal = clearDate(date, 6);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		Date date1 = cal.getTime();

		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.SECOND, -1);
		Date date2 = cal.getTime();

		return new Date[]{date1, date2};
	}

	/**
	 * 取得前一天的时间
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date getDayBefore(String dateStr, int dayCnt)
			throws Exception {
		return getDayBefore(parseDate(dateStr), dayCnt);
	}

	/**
	 * 取得前一天的时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayBefore(Date date, int dayCnt) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, 0 - dayCnt);
		return cal.getTime();
	}

	/**
	 * 取得后一天的时间字
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date getDayAfter(String dateStr, int dayCnt) throws Exception {
		return getDayAfter(parseDate(dateStr), dayCnt);
	}

	/**
	 * 取得后一天的时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayAfter(Date date, int dayCnt) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, dayCnt);
		return cal.getTime();
	}

	/**
	 * 取得指定天数差的时间字
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date getDayDiff(String dateStr, int dayCnt) throws Exception {
		return getDayDiff(parseDate(dateStr), dayCnt);
	}

	/**
	 * 取得指定天数差的时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayDiff(Date date, int dayCnt) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, dayCnt);
		return cal.getTime();
	}

	/**
	 * 取得前一天的时间字
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date getYestday(String dateStr) throws Exception {
		return getYestday(parseDate(dateStr));
	}

	/**
	 * 取得前一天的时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getYestday(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 取得前一天的时间字符串
	 *
	 * @param dateStr
	 * @return
	 */
	public static String getYestdayStr(String dateStr) throws Exception {
		return getYestdayStr(parseDate(dateStr));
	}

	/**
	 * 取得前一天的时间字符串
	 *
	 * @param date
	 * @return
	 */
	public static String getYestdayStr(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		return getDateTimeStr(cal.getTime());
	}

	/**
	 * 取得前一月的时间字符串
	 *
	 * @param dateStr
	 * @param diff
	 * @return
	 */
	public static String getMonthBefore(String dateStr, int diff)
			throws Exception {
		return getMonthBefore(parseDate(dateStr), diff);
	}

	/**
	 * 取得前一月的时间字符串
	 *
	 * @param date
	 * @return
	 */
	public static String getMonthBefore(Date date, int diff) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 0 - diff);
		return getDateTimeStr(cal.getTime());
	}

	/**
	 * 取得前一月的时间字符串
	 *
	 * @param dateStr
	 * @param diff
	 * @return
	 */
	public static String getMonthAfter(String dateStr, int diff)
			throws Exception {
		return getMonthAfter(parseDate(dateStr), diff);
	}

	/**
	 * 取得前一月的时间字符串
	 *
	 * @param date
	 * @return
	 */
	public static String getMonthAfter(Date date, int diff) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 0 + diff);
		return getDateTimeStr(cal.getTime());
	}

	/**
	 * 从以“逗号”分隔的数串取得double类型的值
	 *
	 * @param numStr
	 * @return
	 */
	public static double getDoubleFromStr(String numStr) {
		double rtValue = 0.00d;
		try {
			rtValue = numFormatG.parse(numStr).doubleValue();
		} catch (Exception ex) {
		}
		return rtValue;
	}

	/**
	 * 从以“逗号”分隔的数串取得long类型的值
	 *
	 * @param numStr
	 * @return
	 */
	public static long getLongFromStr(String numStr) {
		long rtValue = 0;
		try {
			rtValue = numFormatG.parse(numStr).longValue();
		} catch (Exception ex) {
		}
		return rtValue;
	}

	/**
	 * 从以“逗号”分隔的数串取得int类型的值
	 *
	 * @param numStr
	 * @return
	 */
	public static int getIntFromStr(String numStr) {
		int rtValue = 0;
		try {
			rtValue = numFormatG.parse(numStr).intValue();
		} catch (Exception ex) {
		}
		return rtValue;
	}

	/**
	 * 取得文件的精确的大小，单位是字节
	 *
	 * @return
	 */
	public static long getFileSize(String fileName) {
		long rtSize = 0;
		File file = new File(fileName);
		rtSize = file.length();
		return rtSize;
	}

	public static String getRequestPath(String uri) {
		String rtStr = null;
		int tmpIndex = uri.lastIndexOf("/");
		if (tmpIndex >= 0) {
			rtStr = uri.substring(0, tmpIndex);
		}
		return rtStr;
	}

	/**
	 * 取得指定长度的数字
	 *
	 * @param str    原始的数串
	 * @param length 长度
	 * @return length <= str.length str length > str.length 字符串前面补零到指定长度的字符串
	 */
	public static String getFixLengthNum(String str, int length) {
		if (str == null) {
			str = "";
		}
		String preFix = "";
		for (int i = 0; i < length - str.length(); i++) {
			preFix += "0";
		}
		return preFix + str;
	}

	/**
	 * 10进制转换为36进制字符串
	 *
	 * @param num    10进制数字
	 * @param length 返回字符串长度
	 * @return 36进制字符串
	 * @throws Exception
	 */
	public static String get36BaseStr(long num, int length) throws Exception {
		String numStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		long[] tmpArray = get10ArrayFromNum(num, 36, new long[]{});
		String tmpStr = "";
		for (int i = 0; i < tmpArray.length; i++) {
			tmpStr += String.valueOf(numStr.charAt((int) tmpArray[i]));
		}
		String rtStr = getFixLengthNum(tmpStr, length);
		return rtStr;
	}

	/**
	 * 把任意进制的数转换为十进制数的数组
	 *
	 * @param num     十进制表示的任意进制的数
	 * @param baseNum 基数
	 * @param num10
	 * @return
	 * @throws Exception
	 */
	public static long[] get10ArrayFromNum(long num, int baseNum, long[] num10)
			throws Exception {
		long[] tmpArray = new long[num10.length + 1];
		tmpArray[0] = num % baseNum;
		if (num10.length > 0) {
			System.arraycopy(num10, 0, tmpArray, 1, num10.length);
		}
		if (num / baseNum == 0) {
			return tmpArray;
		} else {
			return get10ArrayFromNum(num / baseNum, baseNum, tmpArray);
		}
	}

	/**
	 * 把字符串前面补“0”到length长度
	 *
	 * @param str    需要补足位数的字符串
	 * @param length 返回字符串的长度
	 * @return str是null： null str的长度小于length： 前面补“0”到length长度的字符串
	 * str的长度大于或者等于length： str本身
	 */
	public static String getFixLengthStringFront(String str, int length) {
		String rtStr = str;
		if (rtStr == null) {
			return rtStr;
		}
		if (rtStr.length() >= length) {
			return rtStr;
		}
		for (int i = rtStr.length(); i < length; i++) {
			rtStr = "0" + rtStr;
		}
		return rtStr;
	}

	/**
	 * 把金额格式化为英文大写
	 *
	 * @param val 金额数
	 * @return
	 */
	public static String number2En(double val) {
		String moneyString = getFormatedStr(val, WITHOUTGROUP);
		String[] tmpString = moneyString.split("\\.");
		// 默认为整数

		String intString = moneyString;
		// 保存小数部分字串
		String decString = "";
		// 保存英文大写字串
		String engCapital = "";
		StringBuffer strBuff1 = null;
		StringBuffer strBuff2 = null;
		StringBuffer strBuff3 = null;
		int curPoint = 0;
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		int k = 0;
		int n = 0;

		if (tmpString.length > 1) {
			intString = tmpString[0]; // 取整数部分

			decString = tmpString[1]; // 取小数部分

		}
		decString += "00";
		decString = decString.substring(0, 2); // 保留两位小数位

		try {
			// 以下处理整数部分
			curPoint = intString.length() - 1;
			if (curPoint >= 0 && curPoint < 15) {
				k = 0;
				while (curPoint >= 0) {
					strBuff1 = new StringBuffer("");
					strBuff2 = new StringBuffer("");
					strBuff3 = new StringBuffer("");
					if (curPoint >= 2) {
						// n = Integer.parseInt(intString.substring(curPoint -
						// 2, 3));
						n = Integer.parseInt(intString.substring(curPoint - 2,
								curPoint + 1));
						if (n != 0) {
							i1 = n / 100; // 取佰位数值

							i2 = (n - i1 * 100) / 10; // 取拾位数值

							i3 = n - i1 * 100 - i2 * 10; // 取个位数值

							if (i1 != 0) {
								strBuff1.append(enSmallNumber[i1] + " HUNDRED ");
							}
							if (i2 != 0) {
								if (i2 == 1) {
									strBuff2.append(enSmallNumber[i2 * 10 + i3]
											+ " ");
								} else {
									strBuff2.append(enLargeNumber[i2 - 2] + " ");
									if (i3 != 0) {
										strBuff3.append(enSmallNumber[i3] + " ");
									}
								}
							} else {
								if (i3 != 0) {
									strBuff3.append(enSmallNumber[i3] + " ");
								}
							}
							engCapital = strBuff1.toString()
									+ strBuff2.toString() + strBuff3.toString()
									+ enUnit[k] + " " + engCapital;
						}
					} else {
						n = Integer.parseInt(intString.substring(0,
								curPoint + 1));
						if (n != 0) {
							i2 = n / 10; // 取拾位数值

							i3 = n - i2 * 10; // 取个位数值

							if (i2 != 0) {
								if (i2 == 1) {
									strBuff2.append(enSmallNumber[i2 * 10 + i3]
											+ " ");
								} else {
									strBuff2.append(enLargeNumber[i2 - 2] + " ");
									if (i3 != 0) {
										strBuff3.append(enSmallNumber[i3] + " ");
									}
								}
							} else {
								if (i3 != 0) {
									strBuff3.append(enSmallNumber[i3] + " ");
								}
							}
							engCapital = strBuff2.toString()
									+ strBuff3.toString() + enUnit[k] + " "
									+ engCapital;
						}
					}

					++k;
					curPoint -= 3;
				}
				engCapital = engCapital.trim();
			}

			// 以下处理小数部分
			strBuff2 = new StringBuffer();
			strBuff3 = new StringBuffer();
			n = Integer.parseInt(decString);
			if (n != 0) {
				i2 = n / 10; // 取拾位数值

				i3 = n - i2 * 10; // 取个位数值

				if (i2 != 0) {
					if (i2 == 1) {
						strBuff2.append(enSmallNumber[i2 * 10 + i3] + " ");
					} else {
						strBuff2.append(enLargeNumber[i2 - 2] + " ");
						if (i3 != 0) {
							strBuff3.append(enSmallNumber[i3] + " ");
						}
					}
				} else {
					if (i3 != 0) {
						strBuff3.append(enSmallNumber[i3] + " ");
					}
				}

				// 将小数字串追加到整数字串后

				if (engCapital.length() > 0) {
					engCapital = engCapital + " AND CENTS " + strBuff2
							+ strBuff3; // 有整数部分时
				} else {
					engCapital = "CENTS " + strBuff2 + strBuff3; // 只有小数部分时

				}
			}

			engCapital = engCapital.trim();
			return engCapital;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ""; // 含非数字字符时，返回零长字串
		}
	}

	/**
	 * 判断是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return Pattern.matches("-?\\d*\\.?\\d+(E\\d+)?", str);
	}

	/**
	 * 判断是否是金额数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isAmtNumber(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return Pattern.matches("-?\\d{1,3}(\\,\\d{3})*\\.\\d{0,8}", str);
	}

	/**
	 * 判断是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return Pattern.matches("(-?[1-9]\\d*)|0", str);
	}

	/**
	 * 判断是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isPositiveInteger(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return Pattern.matches("[1-9]\\d*", str);
	}

	/**
	 * 判断是否符合帐务年月的格式
	 *
	 * @param str
	 * @return
	 */
	public static boolean isAcctYMFormat(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return Pattern.matches("\\d{4}-\\d{2}", str);
	}

	/**
	 * 判断是否符合系统规定的日期格式
	 *
	 * @param str
	 * @return
	 */
	public static boolean isSysDateFormat(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		if (str.length() < 10) {
			return false;
		}
		if (str.length() == 10) {
			return Pattern.matches("\\d{4}-\\d{2}-\\d{2}", str);
		} else {
			return Pattern.matches(
					"\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}", str);
		}
	}

	/**
	 * 判断是否符合系统规定的日期格式
	 *
	 * @param str
	 * @return
	 */
	public static boolean isDay(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		if (str.length() < 10) {
			return false;
		}
		return Pattern.matches("\\d{4}-\\d{2}-\\d{2}", str);
	}

	/**
	 * 判断是否符合系统规定的日期格式
	 *
	 * @param str
	 * @return
	 */
	public static boolean isDayTime(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		if (str.length() < 19) {
			return false;
		}
		return Pattern.matches(
				"\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}", str);
	}

	/**
	 * 把数字转换位英文字母
	 *
	 * @param num
	 * @return
	 */
	public static String num2Charactor(int num) {
		if (num < 10 || num > 35) {
			return String.valueOf(num);
		}
		return String.valueOf((char) (55 + num));
	}

	/**
	 * A-0;B-1;....;AA;AB
	 *
	 * @param charactor
	 * @return
	 */
	public static int str2Num(String charactor) {
		if (charactor == null || charactor.equals("")) {
			return 0;
		}
		int rtInt = 0;
		String tmpStr = charactor.toUpperCase();
		for (int i = 0; i < tmpStr.length(); i++) {
			char c = tmpStr.charAt(tmpStr.length() - 1 - i);
			rtInt += ((c - 65 + 1) * (int) Math.pow(26, i));
		}
		return rtInt - 1;
	}

	/**
	 * 把字符串转化为Date
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date parseDate(String formatStr, String dateStr)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return format.parse(dateStr);
	}

	/**
	 * 把字符串转化为Date
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date parseDate(String dateStr) throws ParseException {
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat format = null;
		if (Pattern.matches("\\d{4}-\\d{1,2}-\\d{1,2}", dateStr)) {
			format = new SimpleDateFormat("yyyy-MM-dd");
		} else if (Pattern.matches("\\d{4}\\d{2}\\d{2}", dateStr)) {
			format = new SimpleDateFormat("yyyyMMdd");
		} else if (Pattern.matches("\\d{4}年\\d{2}月\\d{2}日", dateStr)) {
			format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		} else if (Pattern.matches("\\d{4}年\\d{1,2}月\\d{1,2}日", dateStr)) {
			format = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		} else if (Pattern.matches("\\d{1,2}\\w{3}\\d{4}", dateStr)) {
			format = new SimpleDateFormat("dMMMyyyy", Locale.ENGLISH);
		} else if (Pattern.matches("\\d{1,2}-\\w{3}-\\d{4}", dateStr)) {
			format = new SimpleDateFormat("d-MMM-yyyy", Locale.ENGLISH);
		} else if (dateStr.length() > 20) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		} else {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		return format.parse(dateStr);
	}

	/**
	 * 当前日期转化成java.sql.Date对象
	 *
	 * @return
	 */
	public static java.sql.Timestamp parseTimeStamp() throws ParseException {
		return parseTimeStamp(null);
	}

	/**
	 * 把字符串转化为java.sql.Date对象
	 *
	 * @param longDate
	 * @return
	 */
	public static java.sql.Timestamp parseTimeStamp(long longDate)
			throws ParseException {
		Date utilDate = new Date(longDate);
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
		return sqlDate;
	}

	/**
	 * 把字符串转化为java.sql.Date对象
	 *
	 * @param dateStr
	 * @return
	 */
	public static java.sql.Timestamp parseTimeStamp(String dateStr)
			throws ParseException {
		Date utilDate = null;
		if (dateStr == null) {
			utilDate = new Date();
		} else {
			utilDate = Utility.parseDate(dateStr);
		}
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
		return sqlDate;
	}

	/**
	 * 当前日期转化成java.sql.Date对象
	 *
	 * @return
	 */
	public static java.sql.Date parseSqlDate() throws ParseException {
		return parseSqlDate(null);
	}

	/**
	 * 把字符串转化为java.sql.Date对象
	 *
	 * @param dateStr
	 * @return
	 */
	public static java.sql.Date parseSqlDate(String dateStr)
			throws ParseException {
		Date utilDate = null;
		if (dateStr == null) {
			utilDate = new Date();
		} else {
			utilDate = Utility.parseDate(dateStr);
		}
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return sqlDate;
	}

	/**
	 * 解析数值
	 *
	 * @param numStr
	 * @return
	 */
	public static int parseInt(String numStr) throws ParseException {
		if (isNullorEmpty(numStr)) {
			return 0;
		}
		return Integer.parseInt(numStr);
	}

	/**
	 * 解析数值
	 *
	 * @param numStr
	 * @return
	 */
	public static long parseLong(String numStr) throws ParseException {
		if (isNullorEmpty(numStr)) {
			return 0;
		}
		return Long.parseLong(numStr);
	}

	/**
	 * 解析数值
	 *
	 * @param numStr
	 * @return
	 */
	public static double parseDouble(String numStr) throws ParseException {
		if (isNullorEmpty(numStr)) {
			return 0.00;
		}
		if (numStr.indexOf(",") > 0) {
			return numFormatG.parse(numStr).doubleValue();
		}
		return Double.parseDouble(numStr);
	}


	/**
	 * 是否是long 类型
	 *
	 * @param str
	 * @return
	 */
	public static boolean isValidLong(String str) {
		try {
			long _v = Long.parseLong(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 转换编码
	 *
	 * @return
	 */
	public static String transferCode(String srcStr, String srcCode,
									  String desCode) throws UnsupportedEncodingException {
		if (srcStr == null) {
			return null;
		}
		return new String(srcStr.getBytes(srcCode), desCode);
	}

	/**
	 * 把编码为ISO-8859-1的转换为GBK
	 *
	 * @param srcStr
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String iso88591ToGbk(String srcStr)
			throws UnsupportedEncodingException {
		return transferCode(srcStr, "ISO-8859-1", "GBK");
	}

	/**
	 * 把编码为ISO-8859-1的转换为UTF-8
	 *
	 * @param srcStr
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String iso88591ToUTF8(String srcStr)
			throws UnsupportedEncodingException {
		return transferCode(srcStr, "ISO-8859-1", "UTF-8");
	}

	/**
	 * 判断金额是否相等，这里考虑舍入因素，并非严格数学意义上的相等
	 *
	 * @param amt1     金额1
	 * @param amt2     金额2
	 * @param amtScale 金额的小数位
	 * @return
	 */
	public static boolean isEqualAmt(double amt1, double amt2, int amtScale) {

		if (amt1 == amt2) {
			return true;
		}

		return Math.abs(amt1 - amt2) < Math.pow(10, (0 - amtScale));
	}

	/**
	 * 取得中文空格的缩进
	 *
	 * @param identCnt 缩进层次数
	 * @param identStr 缩进字符串
	 * @return
	 */
	private static String getIdentStr(int identCnt, String identStr) {
		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < identCnt; i++) {
			buff.append(identStr);
		}
		return buff.toString();
	}

	// read an input-stream into a String
	public static String loadStream(InputStream in) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int readLen = 0;
			while ((readLen = in.read(buff)) > 0) {
				out.write(buff, 0, readLen);
			}
		} catch (Exception ex) {
			// log.debug(ex.getMessage(), ex);
			throw ex;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception ex) {
			}
		}
		return out.toString();
	}

	/**
	 * 避免取得计数法的String形式的双精度型字符串
	 *
	 * @param bgd
	 * @return
	 */
	public static String decimal2DoubleStr(BigDecimal bgd) {
		return getFormatedStr(bgd.doubleValue(), WITHOUTGROUP);
	}

	/**
	 * 带精度要求的四则运算-加法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal add(double opt1, double opt2, int scale) {
		BigDecimal bgd1 = BigDecimal.valueOf(opt1);
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return add(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-加法
	 *
	 * @param bgd1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal add(BigDecimal bgd1, double opt2, int scale) {
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return add(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-加法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal add(BigDecimal opt1, BigDecimal opt2, int scale) {
		if (opt1 == null) {
			opt1 = BigDecimal.valueOf(0.00);
		}
		if (opt2 == null) {
			opt2 = BigDecimal.valueOf(0.00);
		}
		opt1 = opt1.setScale(scale, BigDecimal.ROUND_HALF_UP);
		opt2 = opt2.setScale(scale, BigDecimal.ROUND_HALF_UP);

		return opt1.add(opt2);
	}

	/**
	 * 带精度要求的四则运算-减法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal subtract(double opt1, double opt2, int scale) {
		BigDecimal bgd1 = BigDecimal.valueOf(opt1);
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return subtract(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-减法
	 *
	 * @param bgd1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal bgd1, double opt2, int scale) {
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return subtract(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-减法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal opt1, BigDecimal opt2,
									  int scale) {
		if (opt1 == null) {
			opt1 = BigDecimal.valueOf(0.00);
		}
		if (opt2 == null) {
			opt2 = BigDecimal.valueOf(0.00);
		}
		opt1 = opt1.setScale(scale, BigDecimal.ROUND_HALF_UP);
		opt2 = opt2.setScale(scale, BigDecimal.ROUND_HALF_UP);

		return opt1.subtract(opt2);
	}

	/**
	 * 带精度要求的四则运算-乘法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal multiply(double opt1, double opt2, int scale) {
		BigDecimal bgd1 = BigDecimal.valueOf(opt1);
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return multiply(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-乘法
	 *
	 * @param bgd1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal bgd1, double opt2, int scale) {
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return multiply(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-乘法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal opt1, BigDecimal opt2,
									  int scale) {
		if (opt1 == null) {
			opt1 = BigDecimal.valueOf(0.00);
		}
		if (opt2 == null) {
			opt2 = BigDecimal.valueOf(0.00);
		}
		opt1 = opt1.setScale(scale, BigDecimal.ROUND_HALF_UP);
		opt2 = opt2.setScale(scale, BigDecimal.ROUND_HALF_UP);

		return opt1.multiply(opt2);
	}

	/**
	 * 带精度要求的四则运算-除法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal divide(double opt1, double opt2, int scale) {
		BigDecimal bgd1 = BigDecimal.valueOf(opt1);
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);

		return divide(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-除法
	 *
	 * @param bgd1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal divide(BigDecimal bgd1, double opt2, int scale) {
		BigDecimal bgd2 = BigDecimal.valueOf(opt2);
		bgd2.setScale(scale, BigDecimal.ROUND_HALF_UP);

		return divide(bgd1, bgd2, scale);
	}

	/**
	 * 带精度要求的四则运算-除法
	 *
	 * @param opt1  操作数1
	 * @param opt2  操作数2
	 * @param scale 小数位精度
	 * @return
	 */
	public static BigDecimal divide(BigDecimal opt1, BigDecimal opt2, int scale) {
		if (opt1 == null) {
			opt1 = BigDecimal.valueOf(0.00);
		}
		if (opt2 == null) {
			opt2 = BigDecimal.valueOf(1.00);
		}
		opt1 = opt1.setScale(scale, BigDecimal.ROUND_HALF_UP);
		opt2 = opt2.setScale(scale, BigDecimal.ROUND_HALF_UP);

		return opt1.divide(opt2, scale * 2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 取得双精度型字符串，解决科学计数法问题
	 *
	 * @param bg
	 * @return
	 */
	public static String parseDoubleStr(BigDecimal bg, int format) {
		return Utility.getFormatedStr(bg.doubleValue(), format);
	}

	/**
	 * 解析双精度型字符串，解决科学计数法问题
	 *
	 * @param bg
	 * @return
	 */
	public static double parseDouble(BigDecimal bg) {
		if (bg == null) {
			return 0.00;
		}
		return Double.parseDouble(parseDoubleStr(bg, WITHOUTGROUP));
	}

	/**
	 * 用系统时间和随机数产生系统唯一的文件名
	 *
	 * @return
	 */
	public static String getRandomName() {
		String currTimeStr = Utility
				.getCurDateTimeStr(Utility.DATE_FORMAT_NOSPLIT);
		String randomStr = String.valueOf((int) Math.random() * 10000);
		return currTimeStr + randomStr;
	}

	/**
	 * 把字符串转换为属性哈希表
	 *
	 * @param propStr
	 * @param rtMap
	 */
	public static void str2Map(String propStr, Map rtMap) {
		String[] propArray = propStr.split("\\\r*\\\n{1}");
		for (int i = 0; i < propArray.length; i++) {
			String tmpStr = propArray[i].trim();
			if (tmpStr.length() < 1 || tmpStr.startsWith("#")) {
				continue;
			}
			int tmpIndex = tmpStr.indexOf("=");
			if (tmpIndex < 0) {
				continue;
			}
			String key = tmpStr.substring(0, tmpIndex).trim();
			String value = tmpStr.substring(tmpIndex + 1).trim();
			if (key.length() < 1 || value.length() < 1) {
				continue;
			}
			rtMap.put(key, value);
		}
	}

	/**
	 * Copy 哈希表
	 *
	 * @param fromMap
	 * @param toMap
	 */
	public static void copyMap(Map fromMap, Map toMap) {
		Iterator iKeys = fromMap.keySet().iterator();
		while (iKeys.hasNext()) {
			Object key = iKeys.next();
			Object value = fromMap.get(key);
			toMap.put(key, value);
		}
	}

	/**
	 * 清理指定前缀的属性
	 *
	 * @param srcMap
	 * @param preFix
	 */
	public static void clearMapPre(Map<String, String> srcMap, String preFix) {
		List<String> keyList = new ArrayList<String>();

		Iterator<String> iKeys = srcMap.keySet().iterator();
		while (iKeys.hasNext()) {
			String key = iKeys.next();
			if (key.startsWith(preFix)) {
				keyList.add(key);
			}
		}
		for (int i = 0; i < keyList.size(); i++) {
			srcMap.remove(keyList.get(i));
		}
	}

	/**
	 * 清理指定后缀的属性
	 *
	 * @param srcMap
	 * @param postFix
	 */
	public static void clearMapPost(Map<String, String> srcMap, String postFix) {
		List<String> keyList = new ArrayList<String>();

		Iterator<String> iKeys = srcMap.keySet().iterator();
		while (iKeys.hasNext()) {
			String key = iKeys.next();
			if (key.endsWith(postFix)) {
				keyList.add(key);
			}
		}
		for (int i = 0; i < keyList.size(); i++) {
			srcMap.remove(keyList.get(i));
		}
	}


	/**
	 * 处理\ " '
	 *
	 * @param srcStr
	 * @return
	 * @throws Exception
	 */
	public static String encodeSpecial(String srcStr) {
		if (srcStr == null) {
			return "";
		}
		return srcStr.replace("\\", "\\\\").replace("\"", "\\\"")
				.replace("\'", "\\\'").replace("\r\n", "").replace("\n", "")
				.replace("\r", "");
	}

	/**
	 * 处理\ " '
	 *
	 * @param srcStr
	 * @return
	 * @throws Exception
	 */
	public static String encodeSpecialJson(String srcStr) {
		if (srcStr == null) {
			return "";
		}
		return srcStr.replace("\r\n", "").replace("\n", "")
				.replace("\r", "");
	}

	/**
	 * 处理' % _
	 *
	 * @param srcStr
	 * @return
	 * @throws Exception
	 */
	public static String encodeLike(String srcStr) {
		if (srcStr == null) {
			return "";
		}
		return srcStr.replace("\\", "\\\\").replace("'", "''")
				.replace("%", "\\%").replace("_", "\\_");
	}

	/**
	 * 获取Key前面部分相同的子哈希表
	 *
	 * @param srcMap
	 * @param prefix
	 * @return
	 */
	public static Map startsWithMap(Map srcMap, String prefix) {
		Map rtMap = new HashMap<String, Object>();

		if (isNullorEmpty(prefix)) {
			return rtMap;
		}
		Iterator<String> iKeys = srcMap.keySet().iterator();
		while (iKeys.hasNext()) {
			String key = iKeys.next();
			if (key.startsWith(prefix)) {
				rtMap.put(key, srcMap.get(key));
			}
		}
		return rtMap;
	}

	/**
	 * 获取Key后面部分相同的子哈希表
	 *
	 * @param srcMap
	 * @return
	 */
	public static Map endsWithMap(Map srcMap, String postfix) {
		Map rtMap = new HashMap<String, Object>();

		if (isNullorEmpty(postfix)) {
			return rtMap;
		}
		Iterator<String> iKeys = srcMap.keySet().iterator();
		while (iKeys.hasNext()) {
			String key = iKeys.next();
			if (key.endsWith(postfix)) {
				rtMap.put(key, srcMap.get(key));
			}
		}
		return rtMap;
	}

	/**
	 * 去掉所有html标签
	 *
	 * @param html
	 * @return
	 */
	public static String cutHtml(String html) {
		String result = "";
		result = html.replaceAll("(<[^/\\s][\\w]*)[\\s]*([^>]*)(>)", "$1$3")
				.replaceAll("<[^>]*>", "");
		return result;
	}


	/**
	 * 日期转换函数封装
	 *
	 * @return
	 */
	private static String wrapDateField(String fieldName, String dbms) throws SQLException {
		if (dbms.equals("sqlserver")) {
			return "CONVERT(varchar, " + fieldName + ", 20)";
		} else if (dbms.equals("mysql")) {
			return "date_format(" + fieldName + ", \'%Y-%m-%d %H:%i:%S\')";
		} else if (dbms.equals("oracle")) {
			return "to_char(" + fieldName + ", \'yyyy-MM-dd hh24:mi:ss\')";
		} else {
			throw new SQLException("not accepted dbms");
		}
	}

	/**
	 * 获取字符串
	 *
	 * @param str
	 * @return
	 */
	public static String getString(Object str) {
		if (str == null) {
			return "";
		}
		return String.valueOf(str);
	}

	/**
	 * author syl
	 * 2013-11-13
	 * 判断ip是否在指定ip段中
	 *
	 * @param ip
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean betweenIP(String ip, String start, String end) {
		long ipL = ip2Long(ip);
		long startL = ip2Long(start);
		long endL = ip2Long(end);

		//三个ip中有不合法的,则无法判断

		if (ipL == 0l || startL == 0l || endL == 0l) {
			return false;
		}

		return ipL >= startL && ipL <= endL;
	}

	/**
	 * author syl
	 * 2013-11-13
	 * ip地址转换为long型数字 127.0.0.1->127 000 000 001
	 * 当ip含有除了.和[0-9]之外的字符则返回 0
	 */
	public static long ip2Long(String ip) {
		DecimalFormat df = new DecimalFormat("000");
		String str = "0";
		for (String s : ip.split("\\.")) {
			try {
				str += df.format(Integer.parseInt(s));
			} catch (NumberFormatException e) {
				return 0l;
			}
		}
		return Long.parseLong(str);
	}


	/**
	 * 装饰字符串 在字符串前后加上 ,
	 *
	 * @author zhp
	 * @createTime 2014-2-9
	 * @editTime 下午06:30:53
	 * @desc
	 */
	public static String decorationCommaUtil(String str) {
		if (!str.startsWith(",")) {
			str = "," + str;
		}
		if (!str.endsWith(",")) {
			str = str + ",";
		}
		return str;
	}

	// 产生随机长度码
	public static String getNumCode(int intiCode, int endCode) {
		String checkCode = "";
		for (int i = intiCode; i < endCode; i++) {
			String code = (int) Math.floor(Math.random() * 10) + "";
			checkCode += code;
		}
		return checkCode;
	}

	/**
	 * 位数补位 “0”
	 *
	 * @param id
	 * @param length
	 * @return
	 */
	public static String getAutoDigitComplement(String id, int length) {
		StringBuffer temp = new StringBuffer("");
		if (Utility.isNullorEmpty(id)) return id;
		if (id.length() >= length) return id;
		for (int i = 0; i < length - id.length(); i++) {
			temp.append("0");
		}
		temp.append(id);
		return temp.toString();
	}

	//产生随机范围的随机数
	public static String getRandom(int min, int max) {
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return String.valueOf(s);
	}

	/**
	 * 获取有效的金额 --四舍五入
	 *
	 * @param bigDecimal
	 * @param pointNum
	 * @return
	 */
	public static BigDecimal parseUsePointNumMoney(BigDecimal bigDecimal, int pointNum) {
		return bigDecimal.setScale(pointNum, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 获取有效的金额 --四舍五入 保留两位小数点
	 *
	 * @param bigDecimal
	 * @return
	 */
	public static BigDecimal parseUseTwoPointNumMoney(BigDecimal bigDecimal) {
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static boolean findById(String ss, String id) {
		if (StringUtils.isEmpty(ss)) {
			return false;
		}
		String[] s = ss.split(",");
		for (String t : s) {
			if (t.equals(id)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 生成随机数字和字母,
	 *
	 * @param length 长度
	 * @return
	 */
	public static String getStringRandom(int length) {
		StringBuffer val = new StringBuffer();
		Random random = new Random();
		//参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			//输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) {
				//输出是大写字母还是小写字母
				//int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				//输出是大写字母
				int temp = 65;
				val.append((char) (random.nextInt(26) + temp));
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val.append(String.valueOf(random.nextInt(10)));
			}
		}
		return val.toString();
	}

	/**
	* 生成随机数字和字母,
	*
	* @param length 长度
	* @return
	*/
	public static String getIntRandom(int length) {
		StringBuffer val = new StringBuffer();
		Random random = new Random();
		//参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {
			val.append(String.valueOf(random.nextInt(10)));
		}
		return val.toString();
	}

	private static final String split = "{}";

	/**
	 * 替换model字符串中的{}符号
	 *
	 * @param model
	 * @param args
	 * @return
	 */
	public static String replace(String model, String... args) {
		StringTokenizer st = new StringTokenizer(model, split);
		int b = st.countTokens();
		//解决{}在头部或者尾部时
		if (b != 1) {
			b -= 1;
		}
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (st.hasMoreElements()) {
			sb.append(st.nextToken());
			if (i < args.length && i < b)
				sb.append(args[i]);
			i++;
		}
		return sb.toString();
	}

	/**
	 * 从count中，取num个随机数，不重复
	 *
	 * @param count
	 * @param num
	 * @return
	 */
	public static int[] getRandoms(int count, int num) {
		int result[] = new int[num];
		Random random = new Random();
		for (int i = 0; i < num; i++) {
			while (true) {
				int ran = random.nextInt(count);
				;
				for (int j = 0; j < i; j++) {
					if (result[j] == ran) {
						ran = -1;
						break;
					}
				}
				if (ran != -1) {
					result[i] = ran;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 判断版本大小
	 * @param maxVersion
	 * @param nowVersion
	 * @return
	 */
	public static  boolean isGreaterThan(String maxVersion , String nowVersion) {
		if (StringUtils.isEmpty(maxVersion) || StringUtils.isEmpty(nowVersion)) {
			return false;
		}

		String[] maxVers = maxVersion.split("\\.");
		String[] nowVers = nowVersion.split("\\.");

		for (int i = 0 ;i < maxVers.length ;i++) {
			int maxV = Integer.parseInt(maxVers[i]);

			int nowV = 0 ;
			if (nowVers.length > i) {
				nowV = Integer.parseInt(nowVers[i]);
			}
			if (maxV > nowV) {
				return true;
			} else if (maxV < nowV) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 提取字符串上的电话号码
	 * @param num
	 * @return
	 */
	public static String getCheckNum(String num){
		if(num == null || num.length() == 0){return "";}
		Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[3578]\\d{9})|(?:861[358]\\d{9}))(?!\\d)");
		Matcher matcher = pattern.matcher(num);
		while (matcher.find()) {
			String oldStr = matcher.group();
			String newStr = oldStr.replaceAll(oldStr.substring(3, 7), "****");
			num = num.replace(oldStr,newStr);
		}
		return num;
	}

	public static void main(String[] args) throws  Exception{
		System.out.println(isGreaterThan("1.4.5","1.5.0"));
	}
}
