package com.longlian.live.util.cmd;

/**
 * Created by liuhan on 2017-12-23.
 */
public class TimeUtils {
    public static long Test(String str){
        long t=Integer.parseInt(str.substring(1,3))*60*60 * 1000 +Integer.parseInt(str.substring(4,6))*60 * 1000
                +Integer.parseInt(str.substring(7,9)) * 1000 ;

        if (str.indexOf(".") > -1 && str.length() >= 12) {
            t += Integer.parseInt(str.substring(10,12))  * 10 ;
        }
        return t;
    }


    public static void main(String[] args) {
        String str = "Duration: 00:00:04.56, bitrate: 5 kb/s";
        str = str.substring(str.indexOf(":")+1,str.indexOf(","));
        System.out.println(Test(str));
    }
}
