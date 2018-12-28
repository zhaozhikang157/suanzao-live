package com.huaxin.util.weixin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信分享类型
 * Created by Administrator on 2017/2/27.
 */
public enum WechatShareType {
    circle_of_friend("circle_of_friend","朋友圈分享"),
    friend("friend","好友分享"),
    qq("qq","qq分享"),
    weibo("weibo","微博分享")
    ;
    private String value;
    private String text;
    WechatShareType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据key get Text
     * @param value
     * @return
     * @throws Exception
     */
    public static String getNameByValue(String value) {
        WechatShareType[] types = WechatShareType.values();
        for (WechatShareType ws : types) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        WechatShareType[] types = WechatShareType.values();
        for (WechatShareType ws : types) {
            Map map = new HashMap();
            map.put("value" , ws.getValue());
            map.put("text" , ws.getText());
            list.add(map);
        }
        return list;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
