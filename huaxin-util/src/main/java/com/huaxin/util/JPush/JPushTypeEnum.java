package com.huaxin.util.JPush;

/**
 * Created by Administrator on 2016/4/18.
 */
public enum JPushTypeEnum {
    JPUSH_PAYMENTS("1","待付订单通知"),
    JPUSH_ZENGKA("2","赠卡通知"),
    JPUSH_NEITHER("3","领卡通知"),
    JPUSH_MEMBERCARD("4","会员卡有效期"),
    JPUSH_OTHER_PLACE_LOGIN("5","账号异地登录"),
    ;
    private String code;
    private String describe;

    private JPushTypeEnum(String code, String describe) {
        this.describe = describe;
        this.code = code;
    }

    public static String getDescByLevel(String  level){
        JPushTypeEnum[] list = JPushTypeEnum.values();
        for (JPushTypeEnum temp : list){
            if(temp.code.equals(level)){
                return temp.getDescribe();
            }
        }
        return "";
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
