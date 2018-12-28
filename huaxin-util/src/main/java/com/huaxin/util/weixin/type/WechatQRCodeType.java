package com.huaxin.util.weixin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信二维码自带参数类型
 * Created by syl on 2017/3/15.
 * 最大为4294967295 32位二进制  前两位为分类详细见
 */
public enum WechatQRCodeType {
    third_wechat_or_course_param("42","第三方授权的公众号二维码课程或者带渠道、等课程"), //带redis key

    room("41","直播间"),
    //course("40","课程"),
    third_wechat_live_room_or_course_param_pop_lose("40","第三方授权的公众号二维码课程或者带渠道、等课程"), //带redis key  ，主要做微信弹出关注公众号，60秒有效

    ;
    private String value;
    private String text;

    WechatQRCodeType(String value, String text) {
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
        WechatQRCodeType[] types = WechatQRCodeType.values();
        for (WechatQRCodeType ws : types) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        WechatQRCodeType[] types = WechatQRCodeType.values();
        for (WechatQRCodeType ws : types) {
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
