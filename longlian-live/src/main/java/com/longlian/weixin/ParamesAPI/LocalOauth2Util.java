package com.longlian.weixin.ParamesAPI;

import com.longlian.live.util.weixin.LocalOauth2Url;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by syl on 2016/10/21.
 * WeChat authorized local needs URl
 */
public class LocalOauth2Util{
    public static Set urlSet = new HashSet();
   public static String weixin = "/weixin/";//微信根目录
   public static String aprilFoolsDay = weixin + "toFoolsDayPage/";//使用愚人节活动分享页面
   public static String foolsDay = weixin + "foolsDay/";//记录人数
    /**
     * 添加路径
     */
     static {
        LocalOauth2Util.urlSet.add(LocalOauth2Url.weixin);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.personalCenter);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.liveBackstage);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.dataTotal);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.userFollow);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.liveRoom);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.courseStatistics);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.singleCourseIncome);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.createLiveRoom);

        LocalOauth2Util.urlSet.add(LocalOauth2Url.courseInfo);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.inviCard);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.toShare);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.liveRoomSet);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.teachercourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.shareInviCard);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.createSingleCourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.directBroadcast);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.report);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.wallet);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.evaluation);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.courseBuy);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.myYention);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.bankCard_manage);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.welfareIndex);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.rules);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.recruit);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.fans);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.livereward);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.auth2);

        LocalOauth2Util.urlSet.add(LocalOauth2Url.systemMessage);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.curriCulum);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.complaint);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.submitComment);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.myLiveIncome);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.disIncome);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.platIncome);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.proxyIncome);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.messageDetails);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.member);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.add_bankCard);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.carryCash_user);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.detailsMoneyPage);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.selectedBank);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.bankCard_details);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.toSetPwd);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.toResetPwd);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.toDetailsMoneyList);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.toMoneyList);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.aboutUs);

        LocalOauth2Util.urlSet.add(LocalOauth2Url.moreClass);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.navIgation);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.forgetTradePwd_user);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.new_index);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.recharge);

        LocalOauth2Util.urlSet.add(LocalOauth2Url.learncoinAccount);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.courseRevenuedetails);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.feedBack);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.setUp);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.createGood);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.live_index);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.reward);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.rewardFordetails);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.editSeriesCourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.createSeriesCourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.createSerieSingleCourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.editSeriesSingleCourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.teacherSeries);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.teacherSeriescourse);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.teacherSeriesdetails);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.contributionList);
        LocalOauth2Util.urlSet.add(LocalOauth2Url.notFound);

       LocalOauth2Util.urlSet.add(LocalOauth2Url.fitnessLive);
       LocalOauth2Util.urlSet.add(LocalOauth2Url.inviCode);
       LocalOauth2Util.urlSet.add(aprilFoolsDay);
       LocalOauth2Util.urlSet.add(foolsDay);
    }

    /**
     * 判断是否存在此路径
     * @param url
     * @return
     */
    public static  boolean existsUrl(String url){
        boolean exists =  urlSet.contains(url);
        if(!exists){
            exists =  urlSet.contains(url + "/");
        }
        return exists;
    }


}
