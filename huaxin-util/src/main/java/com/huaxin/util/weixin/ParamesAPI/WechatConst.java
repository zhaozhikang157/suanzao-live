package com.huaxin.util.weixin.ParamesAPI;

/**
 * Created by Administrator on 2017/3/16.
 */
public class WechatConst {
    public final static int ll_live_default_wechat_qrcode_use_time= 2592000;//默认带参数的公众号二维码（直播间、课程）有效时间

    public final static int ll_live_default_third_wechat_qrcode_use_time= 70;//带参数的第三方公众号二维码关注有效时间秒

    public final static int ll_live_third_auth_wechat_qrcode_max_num= 94967295;//授权第三方授权的二维码生成带参数二维码最大值 课程

    public final static int ll_live_third_auth_wechat_access_token_min_use_time= 1210;//授权第三方公众号accessToken最小有效时间

}
