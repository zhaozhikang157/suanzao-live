package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-06-21.
 */
public enum ButtonRefererType {
    INDEX_CREATE_REF("001001","首页开课按钮")
    , COURSE_INFO_CREATE_REF("001002","课程详情开课按钮")
    , ROOM_SINGLE_CREATE_REF("001003","直播间单节课开课按钮")
    , ROOM_SERIES_CREATE_REF("001004","直播间系列课开课按钮")

    , USER_CENTER_INVITATION_REF("002001","个人中心邀请卡按钮")
    , ROOM_UPDATE_INVITATION_REF("002002","直播间邀请卡按钮")
    , LIVE_RUOM_INVITATION_REF("002003","直播间后台邀请卡按钮")

    , COURSE_INFO_SHARE_REF("004001","课程详情分享按钮")
    , LIVE_ROOM_SHARE_REF("004002","直播间分享按钮")

    , ROOM_SHARE_FRINEDS_REF("005001","我的直播间分享朋友按钮")
    , COURSE_INFO_SHARE_FRINEDS_REF("005002","课程详情分享朋友按钮")
    ,  LIVE_ROOM_INFO_SHARE_FRINEDS_REF("005003","直播间分享朋友按钮")
    ,INVITATION_INFO_SHARE_FRINEDS_REF("005004","更换邀请卡分享朋友按钮")
    , TEACHER_LIVE_SHARE_FRINEDS_REF("005005","老师视频直播分享朋友按钮")
    , CREATE_COURSE_SHARE_FRINEDS_REF("005006","老师创建课分享朋友按钮")

    , ROOM_SHARE_FRINEDS_CIRCLE_REF("006001","我的直播间分享朋友圈按钮")
    , COURSE_INFO_SHARE_FRINEDS_CIRCLE_REF("006002","课程详情分享朋友圈按钮")
    , LIVE_ROOM_INFO_SHARE_FRINEDS_CIRCLE_REF("006003","直播间分享朋友圈按钮")
    ,INVITATION_INFO_SHARE_FRINEDS_CIRCLE_REF("006004","更换邀请卡分享朋友按钮")
    , TEACHER_LIVE_SHARE_FRINEDS_CIRCLE_REF("006005","老师视频直播分享朋友圈按钮")
    , CREATE_COURSE_SHARE_FRINEDS_CIRCLE_REF("006006","老师创建课分享朋友圈按钮")

    , SHARE_INVITATION_REF("007001","我的直播间邀请卡分享按钮")
    , COURSE_INFO_SHARE_INVITATION_REF("007002","课程详情分享邀请卡按钮")
    , LIVE_ROOM_INFO_SHARE_INVITATION_REF("007003","直播间分享邀请卡按钮")
    , CREATE_COURSE_SHARE_INVITATION_REF("007004","老师创建课分享邀请卡按钮")

    , TAIL_BUY_COURSE_BUTTON_REF("008001","购买课程按钮")
    , HEAD_BUY_COURSE_BUTTON_REF("008002","封面图购买课程按钮")


    , CONSOLE_ADD_MANAGER_BUTTON_REF("018001" ,"后台添加管理员按钮")
    , LIVE_ADD_MANAGER_BUTTON_REF("018002" ,"直播页面添加管理员按钮")

    ;



    private final String name;
    private String type;

    ButtonRefererType(String type, String name) {
        this.name = name;
        this.type = type;
    }


    public static ButtonRefererType getButtonRefererType(String type) {
        ButtonRefererType[] countTypes = ButtonRefererType.values();
        for (ButtonRefererType ms : countTypes) {
            if (ms.getType().equalsIgnoreCase(type)) {
                return ms;
            }
        }
        return  null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public static String getNameByValue(String type) {
        ButtonRefererType[] countTypes = ButtonRefererType.values();
        for (ButtonRefererType ms : countTypes) {
            if (ms.getType().equalsIgnoreCase(type)) {
                return ms.getName();
            }
        }
        return "";
    }

    public String getParent() {
        return this.getType().substring(0 , 3);
    }

    public static List<ButtonRefererType> getListByParent(String parentCode) {
        List list = new ArrayList();
        ButtonRefererType[] countTypes = ButtonRefererType.values();
        for (ButtonRefererType ms : countTypes) {
            if ( ms.getType().startsWith(parentCode)) {
                list.add(ms);
            }
        }
        return list;
    }


    public static List<Map> getList() {
        List list = new ArrayList();
        ButtonRefererType[] countTypes = ButtonRefererType.values();
        for (ButtonRefererType ms : countTypes) {
            Map map = new HashMap();
            map.put("type", ms.getType());
            map.put("name", ms.getName());
            list.add(map);
        }
        return list;
    }

}
