package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */
public enum FuncCode {
    live_connect("LIVE_CONNECT","连麦"),
    live_watermark("LIVE_WATERMARK","添加水印");
    private String value;
    private String text;

    FuncCode(String value, String text) {
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
        FuncCode[] bankTypes = FuncCode.values();
        for (FuncCode ws : bankTypes) {
            if (ws.getValue().equals(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        FuncCode[] bankTypes = FuncCode.values();
        for (FuncCode ws : bankTypes) {
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
