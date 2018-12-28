package com.huaxin.util.weixin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信素材类型
 * Created by Administrator on 2017/2/27.
 */
public enum WechatMedieType {
    image("image","图片（image）: 2M，支持PNG、JPEG、JPG、GIF格式"),
    voice("voice","语音（voice）：2M，播放长度不超过60s，支持AMR、MP3格式"),
    video("video","视频（video）：10MB，支持MP4格式"),
    thumb("thumb","缩略图（thumb）：64KB，支持JPG格式")
    ;
    private String value;
    private String text;

    WechatMedieType(String value, String text) {
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
        WechatMedieType[] types = WechatMedieType.values();
        for (WechatMedieType ws : types) {
            if (ws.getValue().equalsIgnoreCase(value)) {
                return ws.getText();
            }
        }
        return "";
    }
    public  static List<Map> getList(){
        List list = new ArrayList();
        WechatMedieType[] types = WechatMedieType.values();
        for (WechatMedieType ws : types) {
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
