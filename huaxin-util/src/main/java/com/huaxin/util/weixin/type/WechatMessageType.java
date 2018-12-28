package com.huaxin.util.weixin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信接受事件
 * Created by Administrator on 2017/2/27.
 */
public enum WechatMessageType {
    event("event","事件消息"),
    text_("text","文本消息"),
    image("image","图片消息"),
    voice("voice","语音消息"),
    shortvideo("video","小视频消息"),
    location("location","地理位置消息"),
    link("link","链接消息");
    private String value;
    private String text;

    WechatMessageType(String value, String text) {
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
        WechatMessageType[] types = WechatMessageType.values();
        for (WechatMessageType ws : types) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        WechatMessageType[] types = WechatMessageType.values();
        for (WechatMessageType ws : types) {
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
