package com.longlian.live.service;

import com.huaxin.util.weixin.pay.WeiXinOrder;
import com.longlian.dto.ActResultDto;
import com.longlian.token.AppUserIdentity;

import javax.servlet.http.HttpServletRequest;

public interface WeiXinService {
	
	public ActResultDto unifiedorder(WeiXinOrder wx_order, String ip);

	public ActResultDto unifiedRelayorder(WeiXinOrder wx_order, String ip);

	public ActResultDto unifiedorderH5(WeiXinOrder wx_order, String ip);

	public ActResultDto unifiedRelayorderH5(WeiXinOrder wx_order, String ip);

	public ActResultDto buyFlowOrderH5(WeiXinOrder wx_order, String ip);

	public  ActResultDto callBankUrl(String orderNo, String payType) throws  Exception;

	public  ActResultDto callBankRelayUrl(String orderNo, String payType) throws  Exception;

	public  ActResultDto callBankUrlByH5BuyFlow(String orderNo, String payType) throws  Exception;

	public  ActResultDto callBankUrlByH5BuyFlowError(String orderNo, String payType) throws  Exception;

	public  ActResultDto orderquery(WeiXinOrder wx_order) throws  Exception;


	void handlerUserNavivationRecord(HttpServletRequest request ,  AppUserIdentity appUserIdentity , String uri);

	public ActResultDto buyFlowOrderApp(WeiXinOrder wx_order, String ip);
}
