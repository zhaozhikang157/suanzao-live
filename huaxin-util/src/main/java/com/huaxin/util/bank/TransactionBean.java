package com.huaxin.util.bank;


public class TransactionBean {
	private String MerId;
	
	private String ResponseCode;
	private String MerAmt;
	
	private String Data;
	
	/*s	成功	交易成功	状态码为小写字母s
	2	处理中	交易已接受	
	3	处理中	财务已确认	
	4	处理中	财务处理中	
	5	处理中	已发往银行	ChinaPay已将代付交易发往银行。后续若银行返回结果，该状态会相应更新。
	6	失败	银行已退单	银行退单，交易失败。
	7	处理中	重汇已提交	
	8	处理中	重汇已发送	ChinaPay已将代付交易发往银行。后续若银行返回结果，该状态会相应更新。
*/
	private String stat;

	public String getMerId() {
		return MerId;
	}

	public void setMerId(String merId) {
		MerId = merId;
	}


	public String getResponseCode() {
		return ResponseCode;
	}

	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}

	public String getMerAmt() {
		return MerAmt;
	}

	public void setMerAmt(String merAmt) {
		MerAmt = merAmt;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
	



}
