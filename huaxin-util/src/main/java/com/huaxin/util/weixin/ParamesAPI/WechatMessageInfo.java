package com.huaxin.util.weixin.ParamesAPI;

/**
 * 微信消息对象
 * Created by Administrator on 2017/2/27.
 */
public class WechatMessageInfo {
    private String appId;//微信公众号ID wx2322343434
    private String toUser ;//接受人,原始的
    private String formUserName;//发送人
    private String createTime ;//创建时间 long
    private String msgType;//消息类型 详细见WechatMessageType
    private String event;//事件类型 详细见WechatEventType
    private String eventKey;//事件KEY值，qrscene_为前缀，后面为二维码的参数值
    private String ticket;//二维码的ticket，可用来换取二维码图片
    private String latitude;//地理位置纬度
    private String longitude;//地理位置经度
    private String precision;//地理位置精度

    private String content;//文本消息内容
    private String msgId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToUser() {

        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFormUserName() {
        return formUserName;
    }

    public void setFormUserName(String formUserName) {
        this.formUserName = formUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
