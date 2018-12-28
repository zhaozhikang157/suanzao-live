package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/5.
 */
public enum LlAccountFromType {
    default_value(0,"充值"),
    buy_course(1,"学币购买课"),
    a_reward(2,"打赏"),
    buy_replay(3,"学币转播课"),
    replay_profit(4,"转播课分成");
    private int value;
    private String text;

    LlAccountFromType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据key get Text
     * @param value
     * @return
     * @throws Exception
     */
    public static String getNameByValue(int value) {
        LlAccountFromType[] bankTypes = LlAccountFromType.values();
        for (LlAccountFromType ws : bankTypes) {
            if (ws.getValue() == value) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        LlAccountFromType[] bankTypes = LlAccountFromType.values();
        for (LlAccountFromType ws : bankTypes) {
            Map map = new HashMap();
            map.put("value" , ws.getValue());
            map.put("text" , ws.getText());
            list.add(map);
        }
        return list;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
