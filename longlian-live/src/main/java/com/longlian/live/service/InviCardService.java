package com.longlian.live.service;

import com.longlian.model.Course;
import com.longlian.model.InviCard;
import com.longlian.model.LiveRoom;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
public interface InviCardService {

    InviCard findOneCardByCode(String code,String type);

    List<InviCard> getCourseOrRoomCard(String type);

    BufferedImage courseDrawImage(Long appId, Course course, String contextUrl, InviCard inviCard,Long loginAppId);

    BufferedImage roomDrawImage(Long appId, LiveRoom liveRoom, String contextUrl, InviCard inviCard,Long loginAppId);

    /**
     * 生成个人邀请卡
     * @param appId
     * @param course
     * @param contextUrl
     * @param inviCard
     * @param loginAppId
     * @return
     */
    BufferedImage createPrivateDrawImage(Long appId, Course course, String contextUrl,InviCard inviCard,Long loginAppId);
}
