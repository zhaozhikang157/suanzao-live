package com.longlian.type;


/**
 * Created by Administrator on 2016/8/8.
 */
public enum ReturnMessageType {
    CODE_MESSAGE_TRUE("000000", "操作成功"),
    CODE_LOGIN_FALSE("000001", "手机号或密码错误"),
    CODE_UPDATE_FALSE("000002", "修改失败"),
    CODE_UPDATE_RETURN("000003", "没有找到所修改的用户"),
    CODE_PARAM_RETURN("000004", "参数为空"),
    CODE_PARAM_TYPE_ERROR("000014","参数类型错误"),
    CODE_PARAM_VALUE_ERROR("000024","参数值错误"),
    CODE_USERNAME_ISEXIST_TRUE("000005", "该用户已存在"),
    CODE_MOBILE_FALSE("000006", "请输入正确的手机号"),
    CODE_USER_FALSE("000007", "注册失败"),
    CODE_VERIFICATION_TRUE("000008", "验证码错误"),
    CODE_USERNAME_DISABLE_FALSE("000009", "该用户禁止登录"),
    CODE_MOBILE_ERROR("000010", "请输入正确手机号"),
    NOT_FIND_COURSE("000011", "没有相关课程"),
    CODE_MOBILE_ISEXIST_TRUE("000012", "该手机号已被注册,请更换其他手机号码"),
    NOT_BANK_CARD_APP_USER("000013", "您还没有绑定银行卡"),
    INSERT_BANK_CARD_ERROR("000014", "绑定银行卡失败"),
    GET_BANK_CARD_INFO_ERROR("000015", "获取银行卡信息失败"),
    DEL_BANK_CARD_ERROR("000016", "解绑银行卡失败"),
    NO_LIVE_CHANNEL_RESOURCE("000017", "没有直播通道资源,请联系技术支持"),
    LIVE_NO_START("000018", "直播还未开始,请稍后"),
    LIVE_COURSE_IS_END("000019", "直播课程已经结束"),
    NOT_IS_TEACHER("000020", "身份验证失败"),
    GET_RQCODE_ERROR("000021", "获取二维码信息失败"),
    NOT_EXIT_APP_USER("000022", "该手机号未绑定,可先使用微信登录后绑定"),
    APP_SHARE_ERROR("000023", "分享失败"),
    INVICARD_ERROR("000024", "获取邀请卡信息失败"),
    BANK_CARDNO_INVALID("000025", "请输入正确的银行卡号"),
    CODE_TOURIST_LOGIN_FALSE("000111", "游客账号不能为空"),

    NO_ZB_TRANS_XB("000090","抱歉，枣币兑换学币 暂时不能兑换"),
    UPLOAD_NOT_SUPPORT("000093", "抱歉,出于安全的考虑,不支持此文件类型"),
    APP_MUST_UPDATE("000095", "发现新版本"),
    VERIFICATION_CODE_TIMEOUT("000096", "验证码超时,请重新获取"),
    UPLOAD_NO_FILE("000097", "没有文件"),
    DATABASE_ERR("000098", "系统错误"),
    ERROR_404("000099", "404 - 页面不存在"),
    ERROR_401("000100", "401 - 认证失败"),
    ERROR_403("000101", "403 - 无权访问资源"),
    ERROR_500("000102", "500 - 系统内部发生错误"),
    THIRD_PAY_ERROR("000103", "暂不支持该支付类型"),
    NO_FOLLOW_ROOM("000104", "暂无关注的直播间"),
    NO_COURSE("000105", "没有相关课程"),
    APP_USER_COMMENT_ERROR("000106", "意见提交反馈失败"),
    NO_JOIN_COURSE("000107", "未报名该课程，请报名参加"),
    NO_LOGIN("000108", "请登录"),
    ERROR_LIVE_PWD("000109", "加密密码错误"),
    NO_DATA("000110", "没有更多数据"),
    LIVE_ROOM_ALREADY_FOLLOW("000111", "您已关注过该直播间"),
    IS_UPDATE_MOBILE("000123", "是否更换绑定手机号"),

    NO_COURSE_WARE("000112", "暂无相关图片"),
    CODE_MESSAGE_ERROR("000113", "操作失败"),
    LIVE_ROOM_HEADED("000114", "用户已经存在直播间"),
    COURSE_NOT_MODIFY("000115", "无权限修改该课程"),
    LIVEROOM_NOT_MODIFY("000116", "无权限修改该直播间"),
    LIVE_ROOM_CANNOT_FOLLOW_OWNER("000117", "自已不能关注自己的直播间"),
    THIRD_USER_DISABLE("000018", "未授权用户登录"),
    COURSE_DOWN_ERROR("000019", "课程下架失败"),
    COURSE_HAVE_PAY("000020", "课程有付费人数，不支持下架"),
    COURSE_ENDTIME_NO("000021", "课程未开课，不支持下架"),
    PASSWORD_ERROR("000022", "密码输入错误"),
    LIMIT_COURSE_PLAN_COUNT("000023", "您的新建系列单节课数量已达上限"),
    COURSE_PLAN_NOT_LESS_CURRENT_COUNTS("000024", "输入的排课计划不能小于当前已创建课节数"),
    SERVER_ERROR_RETY("000125", "服务器异常，请稍后重试"),

    //支付和提现等信息提示已100打头
    NOT_EXISTS_MONEYBAG_CODE("100001", "您没有账户"),
    MONEYBAG_BALANCE_NOT_ENOUGN("100002", "您的账户学币余额不足"),
    MONEYBAG_BALANCE_TRANSFOR("100012","将枣币转换为学币"),
    TRADE_PASSWORD_IS_NULL("100003", "交易密码为空,请先设置交易密码"),
    TRADE_PASSWORD_IS_ERROR("100004", "交易密码输入错误,请重新输入"),
    IOS_PAY_FRAIL("100005", "苹果支付失败，请与客服人员联系"),
    THIRD_PAY_CANCEL_ORDER_ERRER("100006", "取消订单，订单为必填项"),
    COURSE_ORDER_PAIDING("100007", "该课程您已经付款中，请等待"),
    COURSE_ORDER_PAID("100008", "该课程您已经支付"),
    NO_TRAD_PASSWORD("100009", "您还没有设置交易密码"),
    BANK_OUT_MONEY("100010", "请输入正确的提现金"),
    BANK_OUT_CARD_NUMBER("100011", "请输入要转入的银行卡号"),
    BANK_OUT_MONRY_ERROR("100012", "一天内申请提现金额大于最高提现金额"),
    BANK_OUT_NOT_MONRY("100013", "您所提现金额大于钱包余额"),
    NOT_BANK_CARD("100014", "请先绑定银行卡"),
    BANK_OUT_ERROR("100015", "提现金额不能低于3元"),
    BANK_CARD_ALREADY_EXISTS("100101","该银行卡已被绑定"),
    ACCOUNT_NOT_USE("100016", "您的钱包已被禁止提现"),
    ACCOUNT_FREEZE("100017", "您的钱包已被冻结"),
    NOT_BANK_OUT_MONEY("100018", "你所申请中提现的金额已大于您当前钱包的金额"),
    NOT_COURSE_INOIC("100019", "没有相关的课程收益详情"),
    GET_COURSE_MONEY_ERROR("100020", "该课程支付金额获取失败"),
    BANK_CARD_ID_ERROR("100021", "请选择银行卡"),
    BANK_CARD_AUTHENTICATION_FAIL("100022", "您提交的姓名与身份证号不匹配，请核实后再输入"),
    LEARN_COINPAY_PAY_TYPE_NO_SUPPORT("100023", "购买学币暂不支持该类型"),
    LEARN_COINPAY_PAY_TYPE_NO_EXIST("100024", "购买学币暂不支持该类型"),
    LEARN_COINPAY_PAY_SUPPORT("100025", "学币支付金额足够"),
    LEARN_COINPAY_PAY_COURSE_NO_NULL("100026", "该课程未找到或已下架"),
    WITH_OPT_OVERRUN("100027", "您这个月的提现次数已超过上限"),
    STUDENT_NOT_LOGIN("100028", "该手机号未绑定,可先使用微信登录后绑定"),
    USER_REWARD_TYPE_NOT_NULL("100029", "请选择打赏类别"),
    SERIES_OFF_THE_SHELF("100030", "该系列课下架失败"),
    BUY_SERIES_COUNT("100031", "该系列课已有人购买,不能下架"),
    NOT_COLSE_COURSE("100032", "该系列课只能老师处理下架"),
    NO_MORE_COURSE_MANAGER("100033", "没有更多管理员信息"),
    NO_COURSE_MANAGER_INFO("100034", "没有该管理员信息"),
    ALREADY_EXIT_COURSE_MANAGER("100035", "该用户已是该直播间的管理员"),
    CREATE_MANAGER_ERROR("100036", "添加管理员失败"),
    USER_NOT_USE("100037", "该用户已被禁止访问"),
    NOT_USE_SELF_MANAGER("100038", "您不能设置自己为管理员"),
    DEL_COURSE_MANAGER_REAL_ERROR("100039", "解除管理员操作失败"),
    NO_JURISDICTION_GAG("100040", "您没有权限对该用户进行禁言"),
    NO_JURISDICTION_DEL_GAG("100041", "您没有权限对该用户解除禁言"),
    weixin_h5_pay_openid_not_match("100042", "请确定是否用同一个微信支付"),
    USER_NOT_EXIST("100043", "该用户不存在"),
    WEB_STUDENT_NOT_LOGIN("100044", "此用户不是该平台老师,禁止登录"),
    INVI_CODE_ERROR("100045", "请输入正确的邀请码"),
    INVI_CODE_ALREADY_USE("100046", "邀请码已被使用"),
    INVI_CODE_NOT_USE("100047", "未到使用时间"),
    GET_INVI_CODE_INFO_ERROR("100048", "邀请码获取失败"),
    NOT_IN_USE_TIME("100049", "邀请码已过期"),
    CREATE_COURSE_MANAGER_ING("100050", "操作太快啦"),
    GET_DATA_CHANGE_LEVEL_ERROR("100051", "请选择需要充值的流量"),
    NO_LIVE_ROOM("100052", "请先创建直播间"),
    NEED_BUY_FLOW("100053", "流量不足，暂时无法提现，请前往微信公众号充值"),
    NEED_BUY_FLOW_APP("100054", "流量不足，暂时无法提现，请及时充值"),
    CREATE_COURSE_TIME_SHORT("100055", "创建课程间隔时间太短，建议10分钟之后继续创建"),
    EDIT_COURSE_TIME_SHORT("100056", "编辑课程间隔时间太短，建议10分钟之后继续创建"),
    ROOM_FORBIDDEN_NOT_CREATE("100057", "您直播内容涉及到不良内容，已被用户投诉，如有疑问请咨询:400-116-9269"),
    COURSE_DELETE("100058", "该课程已下架或删除,您不能进行撤回操作"),
    Msg_CANCEL_ERROR("100059", "消息撤回失败"),
    SEND_FILE_FAIL("100060", "文件发送失败"),
    ONLY_TEACHER_OPTION("100061", "只有老师才能操作"),
    TEACHER_NO_STREAM("100062", "直播流正在接通中，请稍后！"),
    USER_NOT_LOGIN("100063", "您的账号已被禁用,请联系客服"),
    NO_PASSWORD_NO_LOGIN("100064", "请第三方登录后设置登录密码"),
    CONNECT_ERROR_MSG("100065", "连麦请求失败原因为空"),
    UPDATE_CONNECT_ERROR("100067", "请求失败"),
    COURSE_IS_DEL("100068", "该课程已下架"),
    GET_ROOM_TOKEN_ERROR("100069", "获取roomToken失败"),
    MSG_IS_EMPTY("100070", "消息已全部清空"),
    COURSE_START_TIME_BEFORE_NOW("100080", "课程开始时间不能早于当前时间"),

    ERROR_PAY_BY_IOS("100081", "充值系统异常，请稍后重试"),
    weixin_pay_fail("100082", "微信支付失败，请与客服人员联系"),

    RELAY_COURSE_TRANSMITTED("10500", "已经转播此课程"),
    TEACHER_CAN_NOY_RELAY_COURSE("10501", "老师不可以转播自己的课程"),
    NOT_RELAY_COURSE("10502", "此课程不是转播课程"),
    CAN_NOT_RELAY_SERIES_COURSE("10503", "不能转播系列课"),
    CAN_NOT_RELAY_VOICE_COURSE("10504", "不能转播语音课"),
    WAIT_RELAY_END("10505", "需要等待上一节转播课直播结束"),
    CAN_NOTRELAY_COURSE_UPDATE("10506", "已设置转播课程不可更改转播价格及分成比例"),
    PALYING_COURSE("10507", "当前存在正在直播的课程"),
    CHECK_RELAY_SCALE("10508", "分成比例不能小于0或大于100"),


    PARAMETER_NULL_ERROR("1002","参数不能为空"),
    PARAMETER_VALUE_ERROR("1005","参数值不对");


    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public static ReturnMessageType get(String code) {
        ReturnMessageType[] vals = ReturnMessageType.values();
        for (ReturnMessageType lt : vals) {
            if (code.equals(lt.getCode())) {
                return lt;
            }
        }
        return null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private ReturnMessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

