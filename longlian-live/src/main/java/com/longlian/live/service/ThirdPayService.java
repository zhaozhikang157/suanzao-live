package com.longlian.live.service;

import com.longlian.dto.ActResultDto;
import com.longlian.model.Course;
import com.longlian.model.DataChargeLevel;
import com.longlian.model.JoinCourseRecord;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ThirdPayDto;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/2/18.
 */
public interface ThirdPayService  {
    /*支付  start */
    ActResultDto weixinPay(AppUserIdentity appUserIdentity, String ip , ThirdPayDto thirdPayDto)throws Exception;

    ActResultDto weixinPayRelayBuy(AppUserIdentity appUserIdentity, String ip , ThirdPayDto thirdPayDto)throws Exception;

    ActResultDto weixinPayRelay(AppUserIdentity appUserIdentity, String ip , ThirdPayDto thirdPayDto)throws Exception;

    ActResultDto weixinH5Pay(AppUserIdentity appUserIdentity, String ip , ThirdPayDto thirdPayDto)throws Exception;

    ActResultDto weixinH5PayBuyReplay(AppUserIdentity appUserIdentity, String ip , ThirdPayDto thirdPayDto)throws Exception;

    ActResultDto weixinH5PayReplay(AppUserIdentity appUserIdentity, String ip , ThirdPayDto thirdPayDto)throws Exception;

   // ActResultDto aliPay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto)throws Exception;

    ActResultDto moneybagLearnCoinPay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto)throws Exception;
    ActResultDto moneybagLearnCoinPayByRelay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto)throws Exception;
    ActResultDto moneybagRelayCoinPay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto,Course course)throws Exception;
    ActResultDto InviCodePay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto)throws Exception;
    ActResultDto InviCodePayRelay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto)throws Exception;



     ActResultDto iosPay4Recharge (AppUserIdentity appUserIdentity ,ThirdPayDto thirdPayDto) throws Exception ;
    /*支付  end */

    ActResultDto cancelThirdPay(AppUserIdentity appUserIdentity, long orderId);//取消订单

    ActResultDto userRewardPay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto)throws Exception;

    JoinCourseRecord handlerJoinCourseRecord(AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto ,ActResultDto resultDto , String  joinType ,  long roomId)throws  Exception;

    public  JoinCourseRecord handlerJoinCourseRecordRelay(AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto ,    ActResultDto resultDto , String  joinType , long roomId)throws  Exception;

    ActResultDto  thirdPay(HttpServletRequest request , String thirdOrderNo) throws Exception;

    ActResultDto weixinH5BuyFlow(AppUserIdentity appUserIdentity, String ip , Long levelId)throws Exception;

    ActResultDto buyFlowApp (AppUserIdentity appUserIdentity , String ip  ,Long levelId) throws Exception;
}
