package com.longlian.live.service.impl;

import com.huaxin.util.alipay.app.AlipayNotify;
import com.huaxin.util.alipay.web.SignUtils;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AlipayService;
import com.longlian.live.service.OrdersService;
import com.longlian.model.Orders;
import com.longlian.type.PayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by admin on 2016/6/6.
 */
@Service("alipayService")
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    OrdersService ordersService;

    /**
     * android and IOS
     * @param orderNumber
     * @return
     */
    @Override
    public ActResultDto getVIPAliPay(String orderNumber , BigDecimal amount) throws Exception {
        ActResultDto resultDto =  new ActResultDto();//verification(bankIn);  //验证
        Map map = new HashMap();
        map.put("androidData" , getAlipayInfoAndroid(orderNumber ,amount));
        map.put("iosData" , getAlipayInfoIos(orderNumber , amount));
        resultDto.setData(map);
        resultDto.setExt(CustomizedPropertyConfigurer.getContextProperty("aliapy_rsa_private"));
        return resultDto;
    }
    /**
     * android 购卡续费
     * @param orderNumber 商户订单编号
     * @param      amount 金额
     * @return
     */
    public String getAlipayInfoAndroid(String orderNumber , BigDecimal amount) throws Exception{
        //1.在BankIn创建数据
        //2.创建支付宝所需参数
        String tradeNo = orderNumber;//订单号
        String subject = "11369VIP续费支付";//商品名称
        String body = "11369VIP续费支付";   //商品描述
        String price = amount +"";//价格
        String orderInfo = getOrderInfo(tradeNo, subject, body, price);
        String sign = SignUtils.sign(orderInfo, CustomizedPropertyConfigurer.getContextProperty("aliapy_rsa_private"));
        sign = URLEncoder.encode(sign, "UTF-8");
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
        String temp = payInfo;
        return temp;
    }

    /**
     * IOS 购卡续费
     * @param orderNumber
     * @return
     */
    public Map getAlipayInfoIos(String orderNumber , BigDecimal amount) throws Exception{
        Map orderInfoIOS = null;
        orderInfoIOS  = new HashMap();
        //1.在BankIn创建数据
        //2.创建支付宝所需参数
        String tradeNo =  orderNumber;//订单号
        String subject = "商品名称";//商品名称
        String body = "商品描述";   //商品描述
        String price = amount + "";//价格
        String orderInfo = getOrderInfo(tradeNo, subject, body, price);
        String sign = SignUtils.sign(orderInfo, CustomizedPropertyConfigurer.getContextProperty("aliapy_rsa_private"));
        sign = URLEncoder.encode(sign, "UTF-8");
        orderInfoIOS = getInfoIOS(tradeNo, subject, body, price);
        orderInfoIOS.put("sign", sign);
        orderInfoIOS.put("sign_type", "RSA");
        final Map payInfo =orderInfoIOS;
        return orderInfoIOS;
    }


    /**
     * android封装参数
     * @param tradeNo 订单号
     * @param subject 商品名称
     * @param body    描述
     * @param price   价格
     * @return
     */
    public String getOrderInfo(String tradeNo,String subject,String body,String price) {
        String orderInfo = "partner=" + "\"" + CustomizedPropertyConfigurer.getContextProperty("alipay_partner") + "\"";
        orderInfo += "&seller_id=" + "\"" + CustomizedPropertyConfigurer.getContextProperty("alipay_seller") + "\"";
        orderInfo += "&out_trade_no=" + "\"" + tradeNo + "\"";  // 商户网站唯一订单号
        orderInfo += "&subject=" + "\"" + subject + "\"";       // 商品名称
        orderInfo += "&body=" + "\"" + body + "\"";             // 商品详情
        orderInfo += "&total_fee=" + "\"" + price + "\"";       // 商品金额
        orderInfo += "&notify_url=" + "\"" + CustomizedPropertyConfigurer.getContextProperty("alipay_notify_url_app") + "\"";
        //orderInfo += "&notify_url=" + "\"http://shuyoulin.imwork.net:13732/alipay/alipayCallBack\"";
        orderInfo += CustomizedPropertyConfigurer.getContextProperty("alipay_service");
        orderInfo += CustomizedPropertyConfigurer.getContextProperty("alipay_payment_type");
        orderInfo += CustomizedPropertyConfigurer.getContextProperty("alipay_charset");
        orderInfo += CustomizedPropertyConfigurer.getContextProperty("alipay_it_b_pay");
        orderInfo += CustomizedPropertyConfigurer.getContextProperty("alipay_return_rul");
        return orderInfo;
    }

    /**
     * ios封装参数
     * @param tradeNo 订单号
     * @param subject 商品名称
     * @param body    描述
     * @param price   价格
     * @return
     */
    public Map getInfoIOS(String tradeNo,String subject,String body,String price) {
        Map map = new HashMap();
        map.put("partner", CustomizedPropertyConfigurer.getContextProperty("alipay_partner"));
        map.put("seller", CustomizedPropertyConfigurer.getContextProperty("alipay_seller"));
        map.put("out_trade_no",tradeNo);
        map.put("subject",subject);
        map.put("body", body);
        map.put("total_fee",price);
       // map.put("notify_url","http://shuyoulin.imwork.net:13732/alipay/alipayCallBack");
        map.put("notify_url",CustomizedPropertyConfigurer.getContextProperty("alipay_notify_url_app"));
        map.put("service", CustomizedPropertyConfigurer.getContextProperty("alipay_service_ios"));
        map.put("payment_type",CustomizedPropertyConfigurer.getContextProperty("alipay_payment_type_ios"));
        map.put("_input_charset",CustomizedPropertyConfigurer.getContextProperty("alipay_charset_ios"));
        map.put("it_b_pay",CustomizedPropertyConfigurer.getContextProperty("alipay_it_b_pay_ios"));
        map.put("return_url",CustomizedPropertyConfigurer.getContextProperty("alipay_return_rul_ios"));
        return map;
    }

    /**
     * 支付宝回调(充值续费)
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto alipayAppCalBack(HttpServletRequest request) throws Exception{
        ActResultDto resultDto = new ActResultDto();
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        String out_trade_no = request.getParameter("out_trade_no"); //商户订单号
        String trade_no = request.getParameter("trade_no");         //支付宝交易号
        String trade_status = request.getParameter("trade_status");//交易状态
        if(AlipayNotify.verify(params)){//验证成功
            if(trade_status.equals("TRADE_FINISHED")){
            } else if (trade_status.equals("TRADE_SUCCESS")){
                Orders orders =  ordersService.thirdPay(out_trade_no, PayType.alipay.getValue(), trade_no);
                resultDto.setExt(orders);
            }
            resultDto.setData("success") ;
        }else{//验证失败
            resultDto.setData("fail") ;
        }

        return resultDto;
    }

}
