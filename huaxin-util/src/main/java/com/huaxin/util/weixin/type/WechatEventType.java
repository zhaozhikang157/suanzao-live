package com.huaxin.util.weixin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信接受事件
 * Created by Administrator on 2017/2/27.
 */
public enum WechatEventType {
    subscribe("subscribe","订阅"),
    unsubscribe("unsubscribe","取消订阅"),
    SCAN("SCAN","订阅(已关注过的)"),
    LOCATION("LOCATION","上报地理位置事件"),
    CLICK("CLICK","自定义菜单事件"),
    VIEW("VIEW","点击菜单跳转链接时的事件推送");
    private String value;
    private String text;

    WechatEventType(String value, String text) {
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
        WechatEventType[] types = WechatEventType.values();
        for (WechatEventType ws : types) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        WechatEventType[] types = WechatEventType.values();
        for (WechatEventType ws : types) {
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
