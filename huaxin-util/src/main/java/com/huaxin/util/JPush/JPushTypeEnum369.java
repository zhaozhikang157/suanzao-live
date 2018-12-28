package com.huaxin.util.JPush;

/**
 * Created by Administrator on 2016/4/18.
 */
public enum JPushTypeEnum369 {
    JPUSH_DEFAULT("0","发送通知"),
    JPUSH_VIP_PAYMENTS("1","VIP续费"),
    JPUSH_MONEYBAG_BANKOUT("2","钱包提现"),
    JPUSH_PROXY_REWRAD("3","代理返钱"),
    JPUSH_MEMBER_LEVEL("4","会员升级"),
    JPUSH_OTHER_PLACE_LOGIN("5","账号异地登录"),
    JPUSH_BANK_OUT("6","提现"),
    JPUSH_VIDEOGROUP_ONLINE("7", "视频集上线提醒"),
    JPUSH_SEND_PROXY_REMIND("8", "按手机号分发提醒"),
    JPUSH_PROXY_HAND_OUT_REMIND("9", "代理升级提醒"),
    JPUSH_OPEN_PROXY_REMIND("10", "开通代理提醒"),
    JPUSH_BUY_PROXY_REMIND("11", "激活名额被购买提醒");
    private String code;
    private String describe;

    private JPushTypeEnum369(String code, String describe) {
        this.describe = describe;
        this.code = code;
    }

    public static String getDescByLevel(String  level){
        JPushTypeEnum369[] list = JPushTypeEnum369.values();
        for (JPushTypeEnum369 temp : list){
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
