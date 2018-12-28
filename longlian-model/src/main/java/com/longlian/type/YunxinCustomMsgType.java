package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云信自定义消息类型
 * Created by liuhan on 2017-02-21.
 */
public enum YunxinCustomMsgType {

       LIVE_CHANNEL_CLOSE(9,"直播流关闭")
    ,  LIVE_CHANNEL_OPEN(8,"直播流打开")
    ,  LIVE_CHANNEL_ADDRESS_PUSH(7,"直播流地址推送")//直播间打开后的地址
    ,  CHAT_REQUEST(5,"聊天室学生问")
    ,  CHAT_RESPONSE(6,"老师回答")
    ,  CHAT_ROOM_CLOSE(10,"聊天室关闭")
    ,  LIVE_COST(11,"直播打赏")
    ,  COURSE_WARE_PAGE(12,"课件页码")
    ,  ADD_MANAGER(13,"增加管理员") //{"data":{"value":"1"},"type":13} , value:管理员ID
    ,  CANCEL_MANAGER(14,"取消管理员")//{"data":{"value":"1"},"type":14} , value:管理员ID
    ,  JOIN_USER_CHANGE(15,"用户加入消息")//{"data":{"users":[{"id":22,"photo":"fdsafdsafds","photo":"http://dfdsafds.jpg"},{"id":23,"photo":"fdsafdsafds","photo":"http://dfdsafds.jpg"},{"id":24,"photo":"fdsafdsafds","photo":"http://dfdsafds.jpg"}]},"type":15} , value:管理员ID
    ,  JOIN_USER_COUNT_CHANGE(16,"用户加入数量变更消息")//{"data":{"value":"10000"},"type":16} , value:在线人数
    ,  USER_COURSE_INCOME_CHANGE(17,"用户课程收益变更消息")//{"data":{"value":"10000"},"type":17} , value:用户课程收益
    ,  CLEAN_SCREEN_CMD(18,"清屏消息")//{"data":{"value":"清屏时间戳","clearUserId":111},"type":18} , value:清屏时间戳
    ,  TEARCHER_INPUTING(19,"老师正在输入中")//{"data":{"value":"老师正在输入中"},"type":19}
    ,  TEARCHER_DISABLE_INPUT(20,"老师取消输入")//{"data":{"value":"老师取消输入"},"type":20}
    ,  CHAT_MSG_CANCEL(21,"聊天消息撤销")//{"data":{"msgidClient":"ssssss-ssss-wsww" , "id":3333},"type":21}
    ,  SET_COURSE_CAN_CONNECT(22,"允许该课程连麦")
    ,  SET_COURSE_NO_CANNECT(23,"不允许该课程连麦")
    ,  TEARCHER_OUT_OF_ROOM(24,"老师离开直播间")//{"data":{"value":studentID , "courseId":3333},"type":24}
    ;
    private final String name;
    private Integer type;

    YunxinCustomMsgType(Integer type, String name) {
        this.name = name;
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }


}
