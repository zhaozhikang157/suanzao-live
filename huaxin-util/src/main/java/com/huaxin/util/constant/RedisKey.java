package com.huaxin.util.constant;

/**
 * Created by Administrator on 2016/4/1.
 */
public class RedisKey {

    public final static String user_manage_login_prefix = "m_login_";//管理系统登录用户
    public final static String user_app_login_prefix = "app_login_";//移动端登录用户

    public final static String user_info_key = "app_user_info_key_";//获取个人信息

    public final static String user_login_res_prefix = "login_emp_res_";//登录用户资源暂未使用
    
    public final static String user_res_access = "user_res_access_";//用户access_token
    //管理系统所有资源菜单

    public final static int user_login_valid_time = 24 * 60 * 60;//登录用户有效时间
    //用户对应机器码缓存
    public final static String jpush_user_code_key = "jpush_user_code";
    public final static String ll_update_course_name = "ll_update_course_name";
    //课程用户客户端类型
    public static  final String course_user_client_type = "course:user:client:type:";
    //老师在直播间输入状态
    public static final String course_teacher_inputing = "course:teacher:inputing:";
    public static final String create_course_request_uuid = "create:course:request:uuid:";
    public static final String  course_is_end = "course:is:end:";//课程已经结束状态
    public static String connect_room  = "connect:room:";//课程连麦房间meetingname;
    public static String join_room_user = "join:room:user:";//加入聊天室人员
    public static String write_student_connect_online_status = "write:connect:onl:status";//写学生的在线状态的

    public final static String longlian_console_system_menu = "longlian_console_system_menu";    //龙链console系统所有资源菜单

    public final static String longlian_console_system_menu_time = "longlian_console_system_menu_time"; //龙链console系统所有资源菜单更新时间

    public final static String longlian_res_system_menu_time = "longlian_res_system_menu_time"; //龙链res系统所有资源菜单更新时间


    public final static String jpush_369user_code_key = "jpush_369user_code";     //用户对应机器码缓存



    //2016年抽奖开始
    public final static  String annual_meeting_draw_for_2016_phone = "annual_meeting_draw_for_2016_phone";//2016年抽奖所有手机号 list
    public final static  String annual_meeting_draw_for_2016_select_phone = "annual_meeting_draw_for_2016_select_phone";//2016年抽奖抽中手机号 list
    //2016年抽奖结束
    public final static  String annual_meeting_draw_for_2017_phone = "annual_meeting_draw_for_2017_phone";//2016年抽奖所有手机号 list
    public final static  String annual_meeting_draw_for_2017_select_phone = "annual_meeting_draw_for_2017_select_phone";//2016年抽奖抽中手机号 list
    //2017年抽奖开始

    //2017年抽奖结束
    //第三方用户登录
    public final static String ll_live_third_login_prefix = "ll_live_third_login_";//第三方用户登录

    //龙链直播start
    public final static String ll_live_weixin_login_prefix = "ll_live_app_login_";//app和微信用户登录时间
    //用户登录有效时间
    public final static int  ll_live_app_user_login_valid_time= 5 * 24 * 60 * 60;//登录用户有效时间

    public  final  static String ll_live_component_verify_ticket = "ll_live_component_verify_ticket";//微信第三方平台component_verify_ticket

    public final static String ll_live_mobile_register_sms_tra = "ll_live_mobile_register_sms_tra";//369.交易密码验证码
    public final static String ll_live_mobile_register_sms = " ll_live_mobile_register_sms_";//手机注册:短信验证码

    public final static String ll_live_third_component_access_token = "ll_live_third_component_access_token";//微信第三方平台component_access_token
   // public final static String ll_live_weixin_access_token = "ll_live_weixin_access_token";//微信token
    public final static String ll_live_weixin_jsapi_ticket = "ll_live_jsapi_ticket";//微信js 授权票据
    public final static int ll_live_access_token_use_time =  7000 ;//微信accessToken有效时间，微信为7200秒
    public final static String ll_live_appid_use_authorizer_room_info= "ll_live_appid_use_authorizer_room_info";//授权公众号微信(有效的) 信息--采用散列形式 key-直播间Id   value-wechat_official appid
    public final static String ll_live_appid_access_token_pre= "ll_live_appid_access_token_";//授权公众号微信accessToken前缀
    public final static String ll_live_third_auth_wechat_qrcode_next_num = "ll_live_third_auth_wechat_qrcode_next_num";//关注第三方授权的二维码生成带参数二维码最大值
    public final static String ll_live_third_auth_wechat_qrcode_pre= "ll_live_third_auth_wechat_qrcode_pre_";//关注第三方授权的二维码生成带参数二维码前缀
    //public final static int ll_live_third_auth_wechat_qrcode_use_time= 1 * 60 * 60;//第三方授权的二维码生成带参数二维码有效时间 一个小时

    public final static String ll_live_app_advertising = "ll_live_app_advertising";//广告 list 首页
    public final static String ll_live_course_type = "ll_live_app_course_type";//课程类型 首页

    //龙链直播老师登录
    public final static String ll_live_teacher_app_login_prefix = "ll_live_teacher_app_login_prefix";

    //龙链直播老师web登录
    public final static String ll_live_teacher_web_login_prefix = "ll_live_teacher_web_login_prefix";

    public final static String ll_chat_room_msg_wait2db = "ll_chat_room_msg_wait2db";//聊天室消息(存储队列)

    public final static String ll_chat_room_msg_By_chatRoomId = "ll_chat_room_msg_By_chatRoomId";
    public final static int   ll_chat_room_msg_By_chatRoomId_valid_time =24*60*60;


    public final static String ll_live_system_param = "ll_live_system_param";//视频直播系统参数 散列
    public final static String ll_log_sync_save_key = "ll_log_sync_save_key";//异步保存日志

    public final static String ll_live_invi_card = "ll_live_invi_card"; //邀请卡模板

    public final static String ll_live_user_distribution_record = "ll_live_user_distribution_record"; //分销记录 散列类型
    public final static String ll_live_teach_course_last_reward_time= "ll_live_teach_course_last_reward_time"; //老师讲课最后一次奖励时间 ，散列类型 key-老师ID value-最后一次奖励时间
    public final static String ll_live_reward_success_invitation_teach_record = "ll_live_reward_success_invitation_teach_record"; //推荐老师 成功过的老师记录--> set 存放
    public final static String ll_live_follow_reward_rule="ll_live_follow_reward_rule";//老师粉丝关注奖励规则
    public final static String ll_live_follow_reward_record_="ll_live_follow_reward_record_";//老师粉丝关注奖励记录（set ，每个老师一个key）
    public final static String ll_user_follow_key = "ll_user_follow_";        //关注直播间
    public final static String ll_live_create_course_send_wechat_messsage = "ll_live_create_course_send_wechat_messsage";//创建课程发送微信模板消息库存储队列

    public final static String ll_live_follow_Liveroom_send_wechat_messsage = "ll_live_follow_Liveroom_send_wechat_messsage";//用户关注存储队列


    public final static String ll_live_share_course_or_room_card_send_wechat_messsage = "ll_live_share_course_or_room_card_send_wechat_messsage";//分享直播间或者课程邀请卡发送微信模板消息库存储队列

    public final static String ll_user_follow_wait2db = "follow_user_follow_wait2db";    //关注直播间数据库存储队列

    public final static String ll_app_msg_wait2db = "ll_app_msg_wait2db";//消息库存储队列

    public final static String ll_join_course_user_key = "ll_join_course_user_";//报名加入课程的人员列表

    public final static String ll_visit_course_user_key = "ll_visit_course_user_";//学习加入课程的人员列表、针对学习记录（系列课里面的单节课）

    public final static String ll_live_visit_course_record_wait2db= "ll_live_visit_course_record_wait2db"; //观看直播记录数据库存储队列

    public final static String ll_live_visit_course_record = "ll_live_visit_course_record"; //观看直播记录

    public final static String ll_cache_visit_record_record = "ll_visit_record_record_"; //缓存查看详情记录

    public final static String ll_jpush_user_code_key = "ll_jpush_user_code";     //用户对应机器码缓存

    public final static String ll_user_machine_info_wait2db = "ll_user_machine_info_wait2db";     //用户对应机器情况

    public final static String ll_user_active_zset = "ll_user_active_zset";     //用户活动zset时间


    public final static String ll_course_end_event_key = "ll_course_end_event";     //结束课程事件

    public final static String ll_live_trade_password_sms = "ll_live_trade_password_sms";//忘记交易密码:短信验证码

    //老师开课时间提醒
    public final static String ll_live_teacher_course_remind="ll_live_teacher_course_remind";
    //学生开课时间提醒
    public final static String ll_live_student_course_remind="ll_live_teacher_course_remind";
    //直播通道
    public final static String ll_live_channel="ll_live_channel";
    //可以直播的通道
    public final static String ll_live_channel_can_use="ll_live_channel_can_use";
    //已经用了的直播的通道
    public final static String ll_live_channel_using="ll_live_channel_using";
    //直播通道统计key
    public final static String ll_live_channel_using_counts="ll_live_channel_using_count";
    public final static int   ll_live_channel_using_counts_valid_time =3*24*60*60;//直播通道统计key有效时间
    
    //直播ID和通道code关联
    public final static String ll_live_channel_rel="ll_live_channel_rel_";
    //直播ID和通道code反关联
    public final static String ll_ID_live_channel_rel="ll_ID_live_channel_rel_";

    public final static String ll_study_record_wait2db = "ll_study_record_wait2db";//学习记录数据库存储队列

    public final static String ll_channel_visit_record_wait2db = "ll_channel_visit_record_wait2db";//渠道访问记录数据库存储队列

    public final static String ll_button_visit_record_wait2db = "ll_button_visit_record_wait2db";//按钮点击数据库存储队列

    public final static String ll_url_visit_record_wait2db = "ll_url_visit_record_wait2db";//页面地址点击数据库存储队列


    public final static String ll_join_count_wait2db = "ll_join_count_wait2db";//报名数最数据库存储队列

    public final static String ll_visit_count_wait2db = "ll_visit_count_wait2db";//访问直播间人数最数据库存储队列、针对学习记录（系列课里面的单节课）

    //public final static String ll_new_user_count_key = "ll_new_user_count_"; //新增用户记数
    public final static String ll_new_user_key = "ll_new_user_"; //新增用户
    //public final static String ll_new_teacher_count_key = "ll_new_teacher_count_"; //新增老师记数
    public final static String ll_new_teacher_key = "ll_new_teacher_"; //新增老师记数
    public final static String ll_active_user_key = "ll_active_user_"; //活跃用户
    public final static String ll_pv_count_key = "ll_pv_count_"; //总pv记数
    public final static String ll_pay_user_key = "ll_pay_user_"; //付费人数
    public final static String ll_pay_amt_count_key = "ll_pay_amt_count_"; //付费金额
    public final static String ll_new_course_key = "ll_new_course_"; //新课程记录
    public final static String ll_new_pay_course_key = "ll_new_pay_course_"; //付费课程记录
    public final static String ll_new_platform_course_key = "ll_new_platform_course_key_"; //平台开课数

    public final static String ll_live_img_shot_wait2db = "ll_live_img_shot_wait2db";//直播流截图事件
    public final static String ll_userfollow_readed_sync_key = "ll_userfollow_readed_sync_key";//关注我的直播间设为已看状态

    public final static String ll_live_pre_teacher_course_remind = "ll_live_pre_teacher_course_remind";// 课程提醒key
    public final static int   ll_live_pre_teacher_course_valid_time =10 * 60;//课程提醒key有效时间

    public final static String ll_console_count_key = "ll_console_yesterday_count_key";// 统计昨天key
    public final static int   ll_console_count_valid_time =2*24*60*60;//统计有效时间

//    public final static String ll_console_week_count_key = "ll_console_week_count_key";// 统计周key
//    public final static int   ll_console_week_count_valid_time =2*24*60*60;//统计周有效时间
//
//    public final static String ll_console_month_count_key = "ll_console_month_count_key";// 统计月key
//    public final static int   ll_console_month_count_valid_time =2*24*60*60;//统计月有效时间
//
//    public final static String ll_console_year_count_key = "ll_console_year_count_key";// 统计年key
//    public final static int   ll_console_year_count_valid_time =2*24*60*60;//统计年有效时间
    /**
     * 用户使用客户端的版本号和type(android或ios)
     */
    public final static String ll_app_type_version = "ll_app_type_version";

    public final static String ll_binding_mobile = "ll_binding_mobile"; //绑定手机号

    public final static String sz_wangji_mobile = "sz_wangji_mobile"; //忘记密码

    public final static String ll_ios_pay_type = "ll_ios_pay_type"; //IOS支付信息

    public final static String ll_live_user_reward_type = "ll_live_user_reward_type"; //用户打赏类型列表 list

    public final static String ll_course_base_join_num = "ll_course_base_join_num"; //加入课程基数

    public final static String ll_course_base_visit_num = "ll_course_base_visit_num"; //访问课程详情基数

    public final static String android_pay_type = "android_pay_type"; //安卓支付信息

    public final static String ll_img_upload_sys_compress = "ll_img_upload_sys_compress"; //图片上传压缩民步处理

    public final static String ll_series_course_update_time_wait2db= "ll_series_course_update_time_wait2db"; //系列课时间更新

    public final static String ll_course_class_index = "ll_course_class_index_"; //根据课程id获取该课程的相关课件页码

    public final static String ll_page_url = "ll_page_url";//页面地下存方

    public final static String ll_page_visit_user = "ll_page_visit_user_";//页面访问人员

    //app用户存入redis opendid  map散列 key--> openid value-->id
    public final static String ll_live_app_user_by_openid="ll_live_app_user_by_openid";
    //app用户存入redis unionid  map散列 key--> unionid value-->id
    public final static String ll_live_app_user_by_unionid="ll_live_app_user_by_unionid";

    public  final static String ll_create_yunxin_user = "ll_create_yunxin_user"; //创建用户队列

    public final static String ll_lock_pre="ll_lock_";//分布式锁前缀

    public final static String ll_join_lock_pre="ll_join_lock_";//分布式加入课程锁前缀

    public final static String ll_load_user_reward_lock_pre = "ll_load_user_reward_lock_" ;//分布式加载打赏锁前缀

    public  final static String ll_yunxin_token_temp = "ll_yunxin_token_temp_"; //创建用户yuntoken临时存储

    public final static String ll_user_reward = "ll_user_reward_" ;//打赏数据存储

    public final static String ll_user_info = "ll_user_info" ;//缓存用户信息hash

    public final static String ll_course_join_user_deal = "ll_course_join_user_deal";//课程人员处理

    public final static String ll_course_visit_user_deal = "ll_course_visit_user_deal";//课程人员处理 、针对学习记录（系列课里面的单节课）

    public final static String ll_join_course_first_user = "ll_join_course_first_user_";//报名加入课程的第一个人

    public final  static String ll_course_show_users = "ll_course_show_users_";//课程直播间显示的用户

    public final  static String  ll_course_show_all_users  = "ll_course_show_all_users_";//课程直播间显示其它的用户

    public final static String ll_course_user_income_queue = "ll_course_user_income_queue";//课程打赏收益变化

    public final static String ll_live_web_user_navigation_record="ll_live_user_navigation_record";//web端用户引导记录 三列 key-appId value-WebNavigationType.val的字符串，以逗号分隔，如,1,2，

    public final static String ll_live_join_course_record_pre = "ll_live_join_course_record_";//购买课程记录存入 redis，每节课一个，设置过期时间5天 ，散列 key-appId ,value-购买状态SIGN_UP_STATUS 0-购买中1-购买成功 2-失败
    public final static String ll_live_join_relay_course_record_pre = "ll_live_join_relay_course_record_";//转播课程记录存入 redis，每节课一个，设置过期时间5天 ，散列 key-appId ,value-购买状态SIGN_UP_STATUS 0-购买中1-购买成功 2-失败
    public final static int   ll_live_join_course_record_use_time =5*24*60*60;//购买课程记录存入redis ,设置过期时间5天

    public final static String ll_course_manager_real = "ll_course_manager_real_"; //课程场控人员信息

    public final static String ll_course_share_title = "ll_course_share_title_"; //课程分享

    public final static String ll_add_virtual_user = "ll_add_virtual_user"; //创建课程增加虚拟用户队列

    public final static String ll_all_virtual_userid = "ll_all_virtual_userid";//所有的虚拟用户

    public final static String ll_live_manager_appId = "ll_live_manager_appId_"; //直播间的管理人员

    public final static String ll_course_live_connected = "ll_course_live_connected_";//直播流连接情况

    public final static String course_live_slave_connected = "course:live:slave:connected:";//直播流连接情况

    public final static String ll_channel_visit_record = "ll_channel_visit_record_";//

    public final static String ll_course_ware = "ll_course_ware_" ;//课程课件

    public final static String ll_course_ware_lock_pre = "ll_course_ware_lock_" ;//课程课件锁

    public final static String ll_set_vertical_coverss_img = "ll_set_vertical_coverss_img";//后台处理竖屏直播背景图

    public static final String ll_course = "ll_course_";//课程详情缓存

    public static final String ll_chatroom_address = "ll_chatroom_address_";//课程聊天室地址

    public static final String ll_course_manager_wait2db = "ll_course_manager_wait2db_";//课程场控使用

    public static final String ll_live_create_course_roominfo = "ll_live_create_course_roominfo" ;//创建课程，设置roominfo

    public static final String ll_create_chatroom_lock = "ll_create_chatroom_lock_"; //创建课程，创建聊天室，锁，避免后台任务同时创建

    public static final String  ll_chat_room_temp = "ll_chat_room_temp_";//创建聊天室临时存储

    public static final String ll_live_room_manager = "ll_live_room_manager_";//直播间后台场控设置

    public static final String ll_live_push_notify = "ll_live_push_notify";

    public static final String ll_live_room_create = "ll_live_room_create_"; //直播间后台添加场控人员

    public static final String ll_room_manager_lock = "ll_room_manager_lock_";//直播间后台添加场控人员 redis 锁

    public static final String ll_room_manager_lock_temp = "ll_room_manager_lock_temp_";//直播间后台添加场控人员 redis 锁

    public static final String ll_set_user_gag = "ll_set_user_gag_"; //设置用户禁言

    public static final String ll_set_user_gag_wait2db = "ll_set_user_gag_wait2db_"; //设置用户禁言 mq

    public static final String ll_del_user_gag_wait2db = "ll_del_user_gag_wait2db_"; //取消用户禁言 mq

    public static final String ll_class_img_wait2db = "ll_class_img_wait2db"; //编辑课程图片 mq

    public  static final String  ll_is_open_qr = "ll_is_open_qr";//是否打开二维码功能

    public static final String ll_invi_code = "ll_invi_code";   //邀请码去重

    public static final String ll_get_invi_code_course = "ll_get_invi_code_course_";   //已经抢到的课程的邀请码 ，散列 key:appid value：邀请码

    public static final String ll_get_no_invi_code_course = "ll_get_no_invi_code_course";   //未被抢的邀请码 队列

    public static final String ll_invi_code_use_time = "ll_invi_code_use_time"; //邀请码使用时间

    public static final String ll_get_use_invi_code_course_wait2db = "ll_get_use_invi_code_course_wait2db_";//抢到的邀请码去mq处理  .list队列

    public static final String ll_create_video_wait2db = "ll_create_video_wait2db";//创建课程视频 .list队列

    public static final String ll_video_convert_pre = "ll_video_convert_"; //视频转换结果缓存

    public static final String ll_course_garbage = "ll_course_garbage";//课程图文签别

    public static final String ll_clear_chatmsg = "ll_clear_chatmsg";//清理聊天消息

    public static final String ll_is_create_course ="ll_is_create_course_";//创建课标志

    public static final String ll_series_course_join_user = "ll_series_course_join_user";//系列课加入人员后，将下面的单节课加入人员

    public static final String ll_create_course_manager_user = "ll_create_course_manager_user_"; //多次添加管理员,系统还在操作中,需要做原子操作

    public static final String ll_del_course_manager_user = "ll_del_course_manager_user"; //多次接触管理员,系统还在操作中,需要做原子操作

    public static final String ll_day_log_live_room_id = "ll_day_log_live_room_id"; //直播每天流量

    public static final String ll_oss_log_course_id = "ll_oss_log_course_id_"; //回放流量

    public static final String ll_cdn_log_course_id = "ll_cdn_log_course_id_"; //cdn直播流量

    public static final String ll_oss_log_live_room_id = "ll_oss_log_live_room_id_"; //回放流量

    public static final String ll_cdn_log_live_room_id = "ll_cdn_log_live_room_id_"; //cdn直播流量

    public static final String ll_login_mobile_code = "ll_login_mobile_code"; //老师登录验证码

    public final static String ll_data_charge_level = "ll_data_charge_level"; //直播间的管理人员

    public final static String ll_live_notify_cation = "ll_live_notify_cation_"; //直播流监控

    public  final static String ll_cdn_visit_wait2db = "ll_cdn_visit_wait2db";//写cdn_visit日志到数据库

    public static final String ll_liveroom_usedata_lock = "ll_liveroom_usedata_lock_"; //直播间扣流量时，加锁

    public static final String ll_use_start_time = "ll_use_start_time_"; //开始时间

    public static final String ll_use_end_time = "ll_use_end_time_"; //结束时间

    public final static String ll_user_follow_wechat_wait2db = "ll_user_follow_wechat_wait2db";//关注公众号消息库存储队列

    public final static String ll_user_follow_wechat_cache = "ll_user_follow_wechat_cache_";//关注公众号缓存

    public static final String ll_user_follow_wechat_lock = "ll_user_follow_wechat_lock_"; //关注公众号消息保存，加锁

//    public final static String ll_course_start_cation = "ll_course_start_cation_"; //开课监控

//    public final static String ll_course_start_cation_Timer = "ll_course_start_cation_Timer_"; //开课监控Timer

    public final static String ll_course_live_notify_cation = "ll_course_live_notify_cation_"; //存放timer,用于课程直播流的监控

    public final static String ll_set_course_vr_user = "ll_set_course_vr_user_";    //添加直播间虚拟用户

    public final static String ll_msg_cancel = "ll_msg_cancel_"; //撤回消息链表

    public final static String ll_msg_cancel_locak = "ll_msg_cancel_locak_"; //撤回消息锁

    public final static String ll_course_msg_cancel = "ll_course_msg_cancel"; //所有撤回消息

    public final static String ll_room_fun = "ll_room_fun_"; //直播间权限

    public final static String ll_room_study = "ll_room_follow_study_"; //直播间学习人数

    public final static String ll_room_course_num = "ll_room_course_num_"; //直播间课程数

    public final static String ll_set_series_course = "ll_set_series_course_"; //系列课

    public final static String course_live_weekly_selection_id = "course:live:weekly:selection:id"; //存放每周精选课程ID
    public final static String course_live_weekly_selection_id_cc = "course:live:weekly:selection:id:cc"; //存放每周精选课程ID 用作排除
    public final static String live_chat_room_create = "live:chat:room:create:";//预先创建聊天室ID

    public final static String live_chat_room = "live:chat:room:";//直播间 : 聊天室

    public final static String chat_room_msg = "chat:room:msg:";//聊天室信息

    public final static String chat_room_msg_img = "chat:room:msg:img:";//图片信息

    public final static String chat_room_msg_audio = "chat:room:msg:audio:";//语音信息

    public final static String weixin_last_access_token = "weixin:last:access_token:";//缓存最新access_token

    public final static String chat_room_msg_task = "chat:room:msg:task:"; //云信消息处理

    public final static String chat_room_msg_max_id = "chat:room:msg:max:id:"; //云信消息处理 在live初始化,id自加1

    public final static String course_img = "course:img:"; //课程简介图片

    public final static String series_course_count = "series:course:count:"; //系列课数量

    public final static String teacher_course_count = "teacher:course:count:"; //老师课程数

    public final static String course_is_connection = "course:is:connection"; //该课程是否正在直播

    public final static String push_object_cache_count = "push:object:cache:count:";//预热数量

    public final static String push_object_cache_course_address = "push:object:cache:course:address:"; //课程预热

    public final static String push_object_cache_chat_room_msg = "push:object:cache:chat:room:msg:"; //消息预热

    public final static String update_chat_room_msg_audio_catalog = "update:chat:room:msg:audio:catalog"; //转换地址存放的目录

    public final static String app_msg_max_id = "app:msg:max:id:"; //appMsg最大ID

    public static final String ll_create_connectrequest_lock = "connect:request:lock:"; //创建连麦请求，锁，避免后台任务同时创建

    public final static String ll_live_course_type_new = "live:course:type:";//课程类型 1.6.4新版 首页
}
