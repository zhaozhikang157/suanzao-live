package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-06-20.
 */
public enum Button {
    INDEX_CREATE_BUTTON("001","开课按钮",true)
    , NVITATION_BUTTON("002","邀请卡按钮",true)
    , CHANGE_INVITATION_BUTTON("003","更换邀请卡按钮")
   , SHARE_BUTTON("004","分享按钮",true)
    , SHARE_FRINEDS_BUTTON("005","分享朋友按钮",true)
    , SHARE_FRINEDS_CIRCLE_BUTTON("006","分享朋友圈按钮",true)
    , SHARE_INVITATION_BUTTON("007","分享邀请卡按钮",true)
    , BUY_COURSE_BUTTON("008","购买课程按钮")
    , REWARD_BUTTON("009","打赏按钮")
    , REWARD_MONEY_BUTTON("010","打赏金额按钮")
    , USER_CHARGE_BUTTON("011","用户充值按钮")
    , COURSE_WARE_BUTTON("012","课程课件按钮")
    , LIVE_BACK_BUTTON("013","直播间返回按钮")
    , LIVE_MORE_USER_BUTTON("014","直播间更多按钮")
    , LIVE_REWARD_LIST_BUTTON("015","打赏排行榜按钮")
    , SPEAKING_NOT_ALLOWED_BUTTON("016","禁言按钮")
    , LIVE_END_BUTTON("017","直播结束按钮")
    , ADD_MANAGER_BUTTON("018" ,"添加管理员按钮")
    , SET_MANAGER_BUTTON("019" ,"设置管理员按钮")
    , JOIN_WEIXINHAO_BUTTON("020" ,"对接公众号按钮")
    , OPEN_REWARD_BUTTON("021" ,"打开直播间打赏页按钮")
    , AVATAR_CLICK_BUTTON("022" ,"头像长按按钮")
    , MSG_CLICK_BUTTON("023" ,"对话点击按钮")
    , DOWN_CLICK_BUTTON("024" ,"下载按钮")
    , PLAY_CLICK_BUTTON("025","播放按钮")
    ,CDETAIL_OUT_BUTTON("026","课程详情退出按钮")
    ;
    private final String name;
    private String type;
    private boolean hasChild = false;

    Button(String type, String name , boolean  hasChild) {
        this.name = name;
        this.type = type;
        this.hasChild = hasChild;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    Button(String type, String name ) {
        this.name = name;
        this.type = type;
    }

    public static Button getButtonByType(String type) {
        Button[] countTypes = Button.values();
        for (Button ms : countTypes) {
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
        Button[] countTypes = Button.values();
        for (Button ms : countTypes) {
            if (ms.getType().equalsIgnoreCase(type)) {
                return ms.getName();
            }
        }
        return "";
    }

    public static List<Button> getList() {
        List list = new ArrayList();
        Button[] countTypes = Button.values();
        for (Button ms : countTypes) {
            list.add(ms);
        }
        return list;
    }
}
