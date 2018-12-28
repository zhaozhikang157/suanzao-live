package com.longlian.type;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志类型
 */
public enum LogType {
    
    info(0,"消息"),
    third_pay(1, "第三方支付"),
    learn_coinpay(2, "学币支付"),
    third_pay_thirdPayCallBack(3, "第三方支付回调"),
    console_appuser_reset_pwd(10,"console端重置会员密码"),
    console_bankout_audit(100, "后台提现审核"),
    console_bankout(101, "提现"),
    console_course_visitcount(102, "后台课程修改访问人数"),
    console_course_up(104, "后台课程上架"),
    console_course_down(105, "后台课程下架"),
    console_course_del(106, "后台课程删除"),
    console_course_restore(107, "后台课程恢复"),
    console_course_joincount(103, "后台课程修改参加人数"),
    console_course_autoclosetime(108, "后台课程修改访问人数"),
    console_liveroom_autoclosetime(120, "后台直播间修改自动关闭时间"),
    console_course_recovery(109, "后台课程恢复"),
    course_user_end(110,"课程被老师结束"),
    course_console_end(111,"课程被后台结束"),

    app_version_update_remind(112,"客户端版本升级提醒"),
    handler_out_js(113, "外部攻击广告处理"),
    get_invitation_card_fail(114, "获取邀请卡失败"),
    get_weixin_img_url_file(115, "微信上传附件拉取图片失败"),
    course_console_end_by_recordtime(116,"课程被后台结束根据录播时间"),
    recharge_flow(117,"流量充值成功"),
    del_flow_record(118,"删除充值记录成功"),
    console_course_Sortcount(119, "后台课程人工修改评分"),
    updateVideo_count(11,"修改视频播放量"),
    app_log(12,"客户端app日志"),
    log_repeat_count(13,"用户重复注册"),

    bug_flow(14,"购买流量"),
    system_colse_course(15,"老师未在规定时间内直播该课程,已被系统下架"),
    add_bank(16,"添加银行卡"),
    del_bank(17,"解除银行卡");

    private final String name;
    private int type;
    
    LogType(int type , String name){
        this.name = name;
        this.type = type;
    }

    public  static List<Map> getList(){
        List list = new ArrayList();
        LogType[] logTypes = LogType.values();
        for (LogType lt : logTypes) {
            Map map = new HashMap();
            map.put("type" , lt.getType());
            map.put("name" , lt.getName());
            list.add(map);
        }
        return list;
    }

    public static String getNameByValue(String value) {
        LogType[] logTypes = LogType.values();
        for (LogType lt : logTypes) {
            if ((lt.getType()+"").equalsIgnoreCase(value)) {
                return lt.getName();
            }
        }
        return "";
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    
     
    
}
