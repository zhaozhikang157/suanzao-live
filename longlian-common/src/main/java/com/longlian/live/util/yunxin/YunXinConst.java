package com.longlian.live.util.yunxin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云信相关 常量
 * Created by lh on 2017-02-13.
 */
public class YunXinConst {
    private static Logger log = LoggerFactory.getLogger(YunXinConst.class);

    public final  static  String CREATE_USER_URL = "https://api.netease.im/nimserver/user/create.action";

    public final  static  String UPDATE_USER_URL ="https://api.netease.im/nimserver/user/updateUinfo.action";

    public final  static  String GET_USER_INFO_URL ="https://api.netease.im/nimserver/user/getUinfos.action";

    public final  static  String REFRESH_TOKEN_URL = "https://api.netease.im/nimserver/user/refreshToken.action";

    public final  static  String CREATE_CHAT_ROOM_URL ="https://api.netease.im/nimserver/chatroom/create.action";

    public final  static  String CHAT_ROOM_ADDR_URL ="https://api.netease.im/nimserver/chatroom/requestAddr.action";

    public final  static  String CLOSE_CHAT_ROOM_URL ="https://api.netease.im/nimserver/chatroom/toggleCloseStat.action";

    public final  static  String SET_CHAT_ROOM_MANAGER_URL ="https://api.netease.im/nimserver/chatroom/setMemberRole.action";

    public final static  String SEND_CHAT_ROOM_MSG_URL = "https://api.netease.im/nimserver/chatroom/sendMsg.action";

    public final static  String ADD_ROBOT_URL = "https://api.netease.im/nimserver/chatroom/addRobot.action";

    public final static  String REMOVE_ROBOT_URL = "https://api.netease.im/nimserver/chatroom/removeRobot.action";

    public final static  String UPLOAD_URL = "https://api.netease.im/nimserver/msg/upload.action";

    public final static  String SEND_CHAT_MSG_URL = "https://api.netease.im/nimserver/msg/sendMsg.action";

}
