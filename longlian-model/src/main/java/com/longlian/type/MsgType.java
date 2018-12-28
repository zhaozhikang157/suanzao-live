package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-02-23.
 */
public enum MsgType {

     NEW_COURSE_TEACHER(1,"开课成功提醒"  , false, true)//courseid
    //, FOLLOW_LIVE_ROOM(2,"学员关注直播间提醒" , false)
    , PAY_REMIND(3,"学员付费提醒" , false, false) //
    , COMMENT_REMIND(4,"学员评价",  false) //course
    , SHARE_REMIND(5,"学员分享" , false,  false)

    , NEW_COURSE_STUDENT(11,"老师开课提醒" , false , true)//courseid
    //,FOLLOW_LIVE_ROOM_STUDENT(12,"关注直播间学员提醒" , false , false)
    , LIVE_ROOM_APPLY(13,"直播间申请" , false , false) //roomid
    , BUY_COURSE_SUCCESS(14,"购买课程成功提醒", false , true)//orderid courseid
    , COURSE_START_REMIND(15,"购买课程开课提醒", false , true)//course
    , INVITATION_REWARD_CONTENT(16,"分销课程提醒", false , true) //orderid courseid
    , BUY_RELAY_COURSES(17,"购买转播课提醒", false , false) //
    , RELAY_COURSES_REMIND(18,"转播课程提醒", false , false) //

    , LEARD_COINPAY_RECHARGE(88,"账户充值", false , false)
    , MONEY_RECHARGE(89,"枣币充值", false , false)
    , SYS_AWARD(90,"平台奖励" , false)
    , SYS_ACTIVE(91,"平台活动", false)
    , SYS_UPDATE(92,"平台更新" , false)
    , SYS_OTHER_PLACE_LOGIN(93,"账号异地登录" , false)
    , withdraw_money_wait(94,"提现审核", false, false)
    , withdraw_money_ok(98,"提现成功", false, false)
    , withdraw_money_not(102,"提现失败", false, false)
    , YELLOW_RESULT_REMIND(95,"直播涉黄提醒", false)
    , TEACHER_WITHDRAW_RERUEN_PROXY_REMIND(96,"机构旗下老师提现返点", false)
    , SYS_VERSION_UPDATE(97,"系统版本更新", false)
    , SYS_OTHER(99,"默认消息", false)
    , SYS_OTHER_RELAY(105,"转播提醒",false, false)
    , BUY_FLOW(100,"流量充值提醒", false)
    //103极光消息类型
    , H5_PUSH_MESSAGE(103,"推送通知", false)
    , COURSE_MSG_CANCEL(101,"消息撤回",false)  ;


    private final String name;
    private Integer type;
    //是否发送推送
    private boolean isPush;
    //是否是课程消息
    private boolean isCourseMsg;


    public boolean isCourseMsg() {
        return isCourseMsg;
    }

    MsgType(Integer type, String name , boolean isCourseMsg) {
        this.name = name;
        this.type = type;
        this.isPush = true;
        this.isCourseMsg = isCourseMsg;
    }
    MsgType(Integer type, String name , boolean isPush , boolean isCourseMsg) {
        this.name = name;
        this.type = type;
        this.isPush = isPush;
        this.isCourseMsg = isCourseMsg;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeStr() {
        return String.valueOf(type);
    }


    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isPush() {
        return this.isPush;
    }

    public static String getNameByValue(Integer type) {
        MsgType[] msgTypes = MsgType.values();
        for (MsgType ms : msgTypes) {
            if (ms.getType() == type) {
                return ms.getName();
            }
        }
        return "";
    }

    public static MsgType getMsgTypeByValue(Integer type) {
        MsgType[] msgTypes = MsgType.values();
        for (MsgType ms : msgTypes) {
            if (ms.getType() == type) {
                return ms;
            }
        }
        return null;
    }

    public static List<Map> getList() {
        List list = new ArrayList();
        MsgType[] msgTypes = MsgType.values();
        for (MsgType ms : msgTypes) {
            Map map = new HashMap();
            map.put("type", ms.getType());
            map.put("name", ms.getName());
            list.add(map);
        }
        return list;
    }
}
