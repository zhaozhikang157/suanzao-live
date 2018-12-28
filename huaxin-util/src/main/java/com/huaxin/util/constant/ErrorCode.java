package com.huaxin.util.constant;

public enum ErrorCode {

	noLogin(-1),ticketError(-2),ticketOverdue(-3),loginError_noUserName(-4),loginErrorNoMatch(-5),loginErrorLocked(-6);
	
	private ErrorCode(int code) {
		this.code = code;
	}

	private int code;

	public int getCode() {
		return code;
	}
	
	
}
