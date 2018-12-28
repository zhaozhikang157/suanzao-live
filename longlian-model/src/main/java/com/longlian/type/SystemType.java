package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统类型
 * Created by syl on 2016/5/14.
 */

public enum SystemType {
    web("0","WEB端"),
    customer_app("1","客户端APP"),
    boss_app("2","老板端APP"),
    technician_app("3","技师APP");
    private String value;
    private String text;

    SystemType(String value, String text) {
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
        SystemType[] bankTypes = SystemType.values();
        for (SystemType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        SystemType[] bankTypes = SystemType.values();
        for (SystemType ws : bankTypes) {
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
