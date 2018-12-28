package com.huaxin.util.weixin.ParamesAPI;

/**
 * 微信通用接口凭证
 * @author syl
 * @date 2016.10.13
 */
public class AccessToken {
	// 获取到的凭证
	private String token;
	//用户刷新access_token
	private String refreshToken;
	// 凭证有效时间，单位：秒
	private int expiresIn;

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
}
