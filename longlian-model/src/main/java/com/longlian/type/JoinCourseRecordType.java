package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */
public enum JoinCourseRecordType {
    general("0","正常类型"),
    contrl_user("1","场控人员"),
    virtual_user("2","虚拟用户"),
    invi_code("3","邀请码购买"),
    super_user("4","超级管理员");
    private String value;
    private String text;

    JoinCourseRecordType(String value, String text) {
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
        JoinCourseRecordType[] bankTypes = JoinCourseRecordType.values();
        for (JoinCourseRecordType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        JoinCourseRecordType[] bankTypes = JoinCourseRecordType.values();
        for (JoinCourseRecordType ws : bankTypes) {
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
