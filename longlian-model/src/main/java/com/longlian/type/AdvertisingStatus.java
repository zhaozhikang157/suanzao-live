package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2016/5/18.
 */
public enum AdvertisingStatus {


    type_putong("0","非链接"),
    type_qita("1","链接"),
    type_course("2","链接到单节课"),
    type_room("3","链接到直播间");
    private String value;
    private String text;

    AdvertisingStatus(String value, String text) {
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
        AdvertisingStatus[] bankTypes = AdvertisingStatus.values();
        for (AdvertisingStatus ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        AdvertisingStatus[] bankTypes = AdvertisingStatus.values();
        for (AdvertisingStatus ws : bankTypes) {
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
