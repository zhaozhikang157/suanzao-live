package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2016/8/10.
 */

public enum MobileVersionType {

    ios("0","app_ios应用"),
    android("1","app_android应用"),
    
    boss_ios("2","boss_ios应用"),
    boss_android("3","boss_android应用");
    

    private String value;
    private String text;

    MobileVersionType(String value, String text) {
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
        MobileVersionType[] bankTypes = MobileVersionType.values();
        for (MobileVersionType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        MobileVersionType[] bankTypes = MobileVersionType.values();
        for (MobileVersionType ws : bankTypes) {
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
