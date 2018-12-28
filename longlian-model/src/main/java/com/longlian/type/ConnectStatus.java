package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2018-03-26.
 */
public enum ConnectStatus {
 //0-申请中 1-连接中 -3-已断开 -1-连接失败 -2-连接超时 -4-连接失效 -5取消连接
    applying("0","申请中"),
    connecting("1","连接中"),
    disconnected("-3" , "已断开"),
    connect_fail("-1","连接失败"),
    connect_overtime("-2" , "连接超时"),
    connect_lose("-4" , "连接失效"),
    connect_cancel("-5" , "取消连接");
    private String value;
    private String text;

    ConnectStatus(String value, String text) {
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
        ConnectStatus[] bankTypes = ConnectStatus.values();
        for (ConnectStatus ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        ConnectStatus[] bankTypes = ConnectStatus.values();
        for (ConnectStatus ws : bankTypes) {
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
