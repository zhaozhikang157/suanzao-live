package com.longlian.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类型 0 -老师授课奖励 1-推介老师奖励 2-老师粉丝关注奖励
 * Created by Administrator on 2016/8/9.
 */
public enum LongLianRewardType {
    teach_course("0","老师授课奖励"),
    invitation_teach("1","推荐老师奖励"),
    teach_follow("2","老师粉丝关注奖励");

    private String value;
    private String text;

    LongLianRewardType(String value, String text) {
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
        LongLianRewardType[] bankTypes = LongLianRewardType.values();
        for (LongLianRewardType ws : bankTypes) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        LongLianRewardType[] bankTypes = LongLianRewardType.values();
        for (LongLianRewardType ws : bankTypes) {
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
