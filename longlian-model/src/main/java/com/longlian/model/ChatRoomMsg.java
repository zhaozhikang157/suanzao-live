package com.longlian.model;

import java.util.Date;
import java.util.Map;

/**
 *
 * tablename:chat_room_msg
 */
public class ChatRoomMsg {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 消息发送者
     *
     * field:fromAccount  column:FROM_ACCOUNT
     * 
     */
    private Long fromAccount;

    /**
     * 客户端类型： AOS、IOS、PC、WINPHONE、WEB、REST，字符串类型
     *
     * field:fromClientType  column:FROM_CLIENT_TYPE
     * default:WEB
     * 
     */
    private String fromClientType;

    /**
     * 客户端消息ID
     *
     * field:msgidClient  column:MSGID_CLIENT
     * 
     */
    private String msgidClient;

    /**
     * 发送者身份 0-普通学员 1-老师
     *
     * field:fromExt  column:FROM_EXT
     * default:0
     * 
     */
    private String fromExt;

    /**
     * 发送者头像
     *
     * field:fromAvator  column:FROM_AVATOR
     * 
     */
    private String fromAvator;

    /**
     * 发送者名称
     *
     * field:fromNick  column:FROM_NICK
     * 
     */
    private String fromNick;

    /**
     * 消息发送的时间戳
     *
     * field:msgTimestamp  column:MSG_TIMESTAMP
     * 
     */
    private Date msgTimestamp;

    /**
     * 重发标记：0不是重发, 1是重发
     *
     * field:resendFlag  column:RESEND_FLAG
     * default:0
     * 
     */
    private String resendFlag;

    /**
     * 消息类型： 
     *             TEXT、 
     *             PICTURE、 
     *             AUDIO、 
     *             VIDEO、 
     *             LOCATION 、 
     *             NOTIFICATION、 
     *             FILE、 //文件消息 
     *             NETCALL_AUDIO、 //网络电话音频聊天 
     *             NETCALL_VEDIO、 //网络电话视频聊天 
     *             DATATUNNEL_NEW、 //新的数据通道请求通知 
     *             TIPS、 //提醒类型消息 
     *             CUSTOM //自定义消息
     *
     * field:msgType  column:MSG_TYPE
     * default:TEXT
     * 
     */
    private String msgType;

    /**
     * 聊天室id
     *
     * field:chatRoomId  column:CHAT_ROOM_ID
     * 
     */
    private Long chatRoomId;

    /**
     * 是否是提问&回复 0-正常消息 1-提问 2-提问回复
     *
     * field:quizOrReply  column:QUIZ_OR_REPLY
     * default:0
     * 
     */
    private String quizOrReply;

    /**
     * 回复谁
     *
     * field:replyId  column:REPLY_ID
     * 
     */
    private Long replyId;

    /**
     * 回复人姓名
     *
     * field:replyName  column:REPLY_NAME
     * 
     */
    private String replyName;

    /**
     * 回复人头像
     *
     * field:replyAvator  column:REPLY_AVATOR
     * 
     */
    private String replyAvator;

    /**
     * 自定义消息类型
     *
     * field:customMsgType  column:CUSTOM_MSG_TYPE
     *
     */
    private String customMsgType;

    private String isGarbage ; //0:通过  1:不通过
    
    private Long courseId;

    private String liveTopic;

    public String getIsGarbage() {
        return isGarbage;
    }

    public void setIsGarbage(String isGarbage) {
        this.isGarbage = isGarbage;
    }

    public String getCustomMsgType() {
        return customMsgType;
    }

    public void setCustomMsgType(String customMsgType) {
        this.customMsgType = customMsgType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getFromClientType() {
        return fromClientType;
    }

    public void setFromClientType(String fromClientType) {
        this.fromClientType = fromClientType == null ? null : fromClientType.trim();
    }

    public String getMsgidClient() {
        return msgidClient;
    }

    public void setMsgidClient(String msgidClient) {
        this.msgidClient = msgidClient == null ? null : msgidClient.trim();
    }

    public String getFromExt() {
        return fromExt;
    }

    public void setFromExt(String fromExt) {
        this.fromExt = fromExt == null ? null : fromExt.trim();
    }

    public String getFromAvator() {
        return fromAvator;
    }

    public void setFromAvator(String fromAvator) {
        this.fromAvator = fromAvator == null ? null : fromAvator.trim();
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick == null ? null : fromNick.trim();
    }

    public Date getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(Date msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public String getResendFlag() {
        return resendFlag;
    }

    public void setResendFlag(String resendFlag) {
        this.resendFlag = resendFlag == null ? null : resendFlag.trim();
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType == null ? null : msgType.trim();
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getQuizOrReply() {
        return quizOrReply;
    }

    public void setQuizOrReply(String quizOrReply) {
        this.quizOrReply = quizOrReply == null ? null : quizOrReply.trim();
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName == null ? null : replyName.trim();
    }

    public String getReplyAvator() {
        return replyAvator;
    }

    public void setReplyAvator(String replyAvator) {
        this.replyAvator = replyAvator == null ? null : replyAvator.trim();
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getLiveTopic() {
        return liveTopic;
    }

    public void setLiveTopic(String liveTopic) {
        this.liveTopic = liveTopic;
    }

    /**
     * 消息内容
     *
     * field:attach  column:ATTACH
     *
     */
    private String attach;

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach == null ? null : attach.trim();
    }

    public ChatRoomMsg mapToValue(Map map) {
        //{attach=胡锦涛, eventType=4, fromAccount=4, fromAvator=http://wx.qlogo.cn/mmopen/OKFxVklgvu1ibibicd1KZoljBTqe2ukQc7DsyAqql0ryqQyNmGRyibvLBmEdxxlqQiaJT9hBxqQ8SpnYkicBK5jqYzIreibs87GQkmS/0, fromClientType=WEB, fromExt=, fromNick=4Name, msgTimestamp=1487318267159, msgType=TEXT, msgidClient=8658fe4d5ce5ffb37a976afdc2b7cdbe, resendFlag=0, roleInfoTimetag=1487316718447, roomId=7332502}
        this.attach = (String)map.get("attach");
        this.fromAccount = Long.valueOf((String)map.get("fromAccount"));
        this.fromAvator =  (String)map.get("fromAvator");
        this.fromClientType =  (String)map.get("fromClientType");
        this.fromExt =  (String)map.get("fromExt");
        this.fromNick =  (String)map.get("fromNick");
        this.msgTimestamp =  new Date( Long.valueOf((String)map.get("msgTimestamp")));
        this.msgType =  (String)map.get("msgType");
        this.msgidClient =  (String)map.get("msgidClient");
        this.resendFlag =  (String)map.get("resendFlag");
        this.chatRoomId =  Long.valueOf((String)map.get("roomId"));
        this.id =  Long.valueOf((String)map.get("id"));

        Object courseId = map.get("courseId");
        if (courseId instanceof java.lang.Integer) {
            this.courseId =  Long.valueOf((Integer)map.get("courseId"));
        } else {
            this.courseId =  Long.valueOf((String)map.get("courseId"));
        }

        this.liveTopic =  (String)map.get("liveTopic");
        return this;
    }
}