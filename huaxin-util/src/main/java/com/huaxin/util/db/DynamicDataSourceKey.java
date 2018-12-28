package com.huaxin.util.db;

/**
 * Created by syl on 2017/1/10.
 * 以后可以采用redis读取数据源方式，实现自动检测数据源是否有效
 */
public class DynamicDataSourceKey {
    public  final  static String DS_MASTER = "ds_master";//主数据库

    public  final static String DS_SLAVE = "ds_slave";//从数据库
    public  final static String DS_SLAVE2 = "ds_slave2";//从数据库 第二个

    public  final static String DS_LOG = "ds_log";//日志数据库

    public  final static String DS_APP_MSG = "ds_app_msg";//APP用户消息数据库

    public  final static String DS_CHAT_ROOM_MSG = "ds_chat_room_msg";//聊天室数据库

}
