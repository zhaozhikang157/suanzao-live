package com.longlian.third.service;

import com.huaxin.util.weixin.ParamesAPI.WechatMessageInfo;
import com.longlian.model.Course;

/**
 * Created by Administrator on 2017/2/10.
 */
public interface WechatOfficiaService {
    void followWechatSendCustomMessageByOpenId(WechatMessageInfo wechatMessageInfo) throws Exception;
    void cannelFollowWechat(WechatMessageInfo wechatMessageInfo) throws Exception;

}
