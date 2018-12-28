package com.huaxin.util.weixin.ParamesAPI;

import java.util.Map;

/**
 * 公众号授权 第三方平台 token对象
 * @author syl
 * @date 2016.10.13
 */
public class WechatAuthorizationToken {
	private String appid;//公众号Id
	// 获取到的凭证
	private String token;
	//用户刷新access_token
	private String refreshToken;
	// 凭证有效时间，单位：秒
	private int expiresIn;
	//公众号授权给开发者的权限集列表，ID为1到15时分别代表
	/*消息管理权限
	用户管理权限
	帐号服务权限
	网页服务权限
	微信小店权限
	微信多客服权限
	群发与通知权限
	微信卡券权限
	微信扫一扫权限
	微信连WIFI权限
	素材管理权限
	微信摇周边权限
	微信门店权限
	微信支付权限
	自定义菜单权限*/
	private Map funcInfo;


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}
}
