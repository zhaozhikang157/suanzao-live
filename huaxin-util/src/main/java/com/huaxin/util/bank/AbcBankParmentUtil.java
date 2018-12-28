package com.huaxin.util.bank;

/**
 * Created by admin on 2015/12/17.
 */
public enum AbcBankParmentUtil {
    /*
    设定交易类型 必须设定，
    ImmediatePay：直接支付
    PreAuthPay：预授权支付
    DividedPay：分期支付
    */
    PAY_TYPE_ID("ImmediatePay","直接支付"),//----------------------------设定为直接支付
    CYRRENCY_CODE("156","人民币"),//----------------------------------设定交易币种 156： 人民币
    INSTALL_MENT_MARK("0","非分期交易"),//---------------------------------非分期交易，必须设定为0
    ORDER_DESC("保证金充值","订单说明"),
    /*
       充值类 0101:支付账户充值
       消费类 0201:虚拟类,0202:传统类,0203:实名类
       转账类 0301:本行转账,0302:他行转账
       缴费类 0401:水费,0402:电费,0403:煤气费,0404:有线电视费,0405:通讯费,
       0406:物业费,0407:保险费,0408:行政费用,0409:税费,0410:学费,0499:其他
       理财类 0501:基金,0502:理财产品,0599:其他
    */
    COMMODITY_TYPE("0201","虚拟类"),//---------------------------------暂时设定为 虚拟类型消费
    PAY_MENT_LINK_TYPE("1","internet 网络接入"),//必须设定 1.internet 网络接入 2.手机网络接入3:数字电视网络接入 4:智能客户端
    NOTIFY_TYPE("1","服务器通知"),//-------------------------------必须设定， 0： URL 页面通知 1：服务器通知
  //  RESULT_NOTIFY_URL("http://localhost:8080/ComprehensiveTopUpController/toPaymentRequestResultPage","回调URL地址(测试环境)"),//必须设定，合法的 URL地址
//    RESULT_NOTIFY_URL("http://www.longlianwang/abcBank/toPaymentRequestResultPage","回调URL地址(生产环境)"),//必须设定，合法的 URL地址
    IS_BREAK_ACCOUNT("0","交易不分帐"),//-----------------------------------------交易是否分账必须设定， 0:否； 1:是
    /*
       必须设定， 1：农行卡支
       付 2：国际卡支付 3：农
       行贷记卡支付 5:基于第三
       方的跨行支付 A:支付方
       式合并 6：银联跨行支付
    */
    PAY_MENT_TYPE("A","支付方式合并");//支付方式合并
    private AbcBankParmentUtil(String code, String message){
        this.code = code;
        this.message = message;
    }
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
