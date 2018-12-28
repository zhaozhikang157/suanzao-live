package com.longlian.type;

/**
 * Created by Administrator on 2016/8/9.
 */
public enum FlowManagementType {
    DescribeLiveStreamsOnlineList("DescribeLiveStreamsOnlineList","查询直播流列表"),
    DescribeLiveStreamsBlockList("DescribeLiveStreamsBlockList","查询直播流黑名单列表"),
    ForbidLiveStream("ForbidLiveStream","加入黑名单"),
    ResumeLiveStream("ResumeLiveStream","恢复"),
    DescribeLiveStreamOnlineUserNum("DescribeLiveStreamOnlineUserNum","在线人数");
    private String value;
    private String text;

    FlowManagementType(String value, String text) {
        this.value = value;
        this.text = text;
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
