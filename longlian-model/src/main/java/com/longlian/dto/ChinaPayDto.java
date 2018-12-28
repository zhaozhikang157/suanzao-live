package com.longlian.dto;

/**
 * Created by Administrator on 2016/8/23.
 */
public class ChinaPayDto {
    private String merId;//商户Id
    private String merDate;//交易日期20160823
    private String merSeqId;//平台交易流水号
    private String cardNo;//银行卡号
    private String usrName;//开户人姓名
    private String  openBank;//开户行
    private String subBank="十里河分行";//分行
    private String prov="北京";//省份
    private String city="北京";//城市
    private String tranAmt;//交易金额 分
    private String purpose;//用途
    private String chkValue;//签名字符串
    private String termType;//public static final String DF_PAY_TERMTYPE="07";
    private String signFlag;//DF_QUERY_SIGNFLAG="1";
    private String version;//版本 DF_QUERY_VERSION="20141120";
    private String flag;//对公对私 00- 对私  01-对公

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getMerDate() {
        return merDate;
    }

    public void setMerDate(String merDate) {
        this.merDate = merDate;
    }

    public String getMerSeqId() {
        return merSeqId;
    }

    public void setMerSeqId(String merSeqId) {
        this.merSeqId = merSeqId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTranAmt() {
        return tranAmt;
    }

    public void setTranAmt(String tranAmt) {
        this.tranAmt = tranAmt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSubBank() {
        return subBank;
    }

    public void setSubBank(String subBank) {
        this.subBank = subBank;
    }

    public String getChkValue() {
        return chkValue;
    }

    public void setChkValue(String chkValue) {
        this.chkValue = chkValue;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(String signFlag) {
        this.signFlag = signFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("merId:"+merId+",");
		sb.append("merDate:"+merDate+",");
		sb.append("merSeqId:"+merSeqId+",");
		sb.append("cardNo:"+cardNo+",");
		sb.append("usrName:"+usrName+",");
		sb.append("openBank:"+openBank+",");
		sb.append("signFlag:"+signFlag+",");
		sb.append("version:"+version);
		return sb.toString();
	}
    
    
}
