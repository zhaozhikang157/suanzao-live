package com.longlian.console.util;

import com.huaxin.util.DateUtil;
import com.longlian.exception.MobileGlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by liuhan on 2017-11-07.
 */
public class LogDealUtil {
    private static Logger log = LoggerFactory.getLogger(LogDealUtil.class);

    public static Pattern pattern = Pattern.compile("(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})(\\s)(\\S+|-)(\\s)(\\S+|-)(\\s\\[)([0-9a-zA-Z\\+-:/ ]+)(\\]\\s\")(\\S+)(\\s)(\\S+)(\\s)(\\S+)(\"\\s)(\\d+|-)(\\s)(\\d+|-)(\\s)(\\d+|-)(\\s\")(\\S+)(\"\\s\")(.+)(\"\\s\")(\\S+)(\"\\s\")(\\S+)(\"\\s\")(\\S+)(\"\\s\")(\\S+)(\"\\s\")(\\S+)(\"\\s\")(\\S+)(\"\\s\")(\\S+)(\"\\s)(\\d+|-)(\\s)(\\d+|-)(\\s\")(\\S+)(\"\\s)(\\d+|-)(\\s\")(\\S+)(\"\\s)(\\S+|-)(\\s\")(\\S+)(\"\\s\")(\\S+)(\")");

    public static int dayBefore = -1;

    public static Date getDate(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            log.error("时间{}转换错误：" , str , e);
            //取时间前一天
            Date now = new Date();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE , -1);
            return c.getTime();
        }
    }
    public static String getBeferDay() {
        return DateUtil.format(getBeferDayDate(), "yyyy-MM-dd");
    }

    public static Date getBeferDayDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, dayBefore);
        date = calendar.getTime();
        return date;
    }

    public static List<String> downLoadFromUrl(String urlStr) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        BufferedReader br = null;
        List<String> result = new ArrayList<>();
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();
            //获取自己数组
            br = new BufferedReader(new InputStreamReader(inputStream,"utf8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                result.add(s);
            }
            log.info("下载日志文件：{}" , urlStr);
        } catch (MalformedURLException e) {
            log.error("下载日志出错：" ,e);
            MobileGlobalExceptionHandler.sendEmail(e , "下载日志时出错:" + urlStr );
        } catch (IOException e) {
            log.error("下载日志出错：" ,e);
            MobileGlobalExceptionHandler.sendEmail(e , "下载日志时出错:" + urlStr );
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static String[] split(String str , String slot) {
        StringTokenizer token=new StringTokenizer(str,slot);

        int count = token.countTokens();
        String[] arra = new String[count];
        for (int i = 0 ;i < count ;i++) {
            arra[i] = token.nextToken();
        }
        return arra;
    }
}
