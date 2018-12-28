package com.longlian.live.service.impl;

import com.huaxin.util.HttpUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.Utility;
import com.huaxin.util.XmlUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.pay.WeiXinOrder;
import com.longlian.dto.ActResultDto;
import com.longlian.live.constant.Const;
import com.longlian.live.service.OrdersService;
import com.longlian.live.service.WeiXinService;
import com.longlian.model.Orders;
import com.longlian.token.AppUserIdentity;
import com.longlian.weixin.ParamesAPI.WebNavigationType;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WeiXinServiceImpl implements WeiXinService {
	
	private static Logger log = LoggerFactory.getLogger(WeiXinServiceImpl.class);

	@Autowired
	OrdersService ordersService ;

	@Autowired
	RedisUtil redisUtil;

	/**
	 * 微信支付回调
	 * @param orderNo
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ActResultDto callBankUrl(String orderNo ,String  payType) throws  Exception{
		ActResultDto resultDto = new ActResultDto();
		Orders orders = ordersService.thirdPay(orderNo, payType, "");
		resultDto.setExt(orders);
		return resultDto;
	}
	/**
	 * 微信支付回调（转播购买）
	 * @param orderNo
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ActResultDto callBankRelayUrl(String orderNo, String payType) throws Exception {
		ActResultDto resultDto = new ActResultDto();
		Orders orders = ordersService.thirdPayRelay(orderNo, payType, "");
		resultDto.setExt(orders);
		return resultDto;
	}

	/**
	 * 微信H5购买流量支付回调
	 * @param orderNo
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ActResultDto callBankUrlByH5BuyFlow(String orderNo, String payType) throws Exception {
		ActResultDto resultDto = new ActResultDto();
		Orders orders = ordersService.updateBuyFlow(orderNo, payType);
		resultDto.setExt(orders);
		return resultDto;
	}

	@Override
	public ActResultDto callBankUrlByH5BuyFlowError(String orderNo, String payType) throws Exception {
		ActResultDto resultDto = new ActResultDto();
		Orders orders = ordersService.updateBuyFlowError(orderNo, payType);
		resultDto.setExt(orders);
		return resultDto;
	}

	/**
     * 
    * @Title: unifiedorder
    * @Description: TODO(向微信服务器发送请求，获取预支付订单号)
    * @param wx_order
    * @param ip
    * @return
    * @return ActResultDto    返回类型
     */
    public ActResultDto unifiedorder(WeiXinOrder wx_order,String ip){
		 ActResultDto resultDto = new ActResultDto();
		//组装包体
		String xml = getPakage(wx_order, ip);
		String result = null;
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder",xml);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String prepay_id = xmlutil.getChildText("prepay_id");
				String appid = xmlutil.getChildText("appid");
				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				TreeMap<String, String> map = new TreeMap<String,String>();
				map.put("appid", appid);
				map.put("noncestr", nonce_str);
				map.put("prepayid", prepay_id);
				map.put("partnerid", mch_id);
				map.put("package", "Sign=WXPay");
				String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
				long timestamp = new Date().getTime()/1000;
				map.put("timestamp", timestamp+"");
				String sign =  sign(map ,partnerKey);
				map.put("sign", sign);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
				log.error("微信支付异常>>>>:", return_code + " | " + return_msg);
			}
		} catch (Exception e) {
			log.error("向微信服务器发送请求异常>>>>:", e.getMessage());
		}
		return resultDto;
	}
	/**
	 *
	 * @Title: unifiedRelayorder
	 * @Description: TODO(向微信服务器发送请求，获取预支付订单号)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return ActResultDto    返回类型
	 */
	@Override
	public ActResultDto unifiedRelayorder(WeiXinOrder wx_order, String ip) {
		ActResultDto resultDto = new ActResultDto();
		//组装包体
		String xml = getRelayPakage(wx_order, ip);
		String result = null;
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder",xml);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String prepay_id = xmlutil.getChildText("prepay_id");
				String appid = xmlutil.getChildText("appid");
				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				TreeMap<String, String> map = new TreeMap<String,String>();
				map.put("appid", appid);
				map.put("noncestr", nonce_str);
				map.put("prepayid", prepay_id);
				map.put("partnerid", mch_id);
				map.put("package", "Sign=WXPay");
				String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
				long timestamp = new Date().getTime()/1000;
				map.put("timestamp", timestamp+"");
				String sign =  sign(map ,partnerKey);
				map.put("sign", sign);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
			}
		} catch (Exception e) {
			log.error("向微信服务器发送请求异常>>>>:", e.getMessage());
		}
		return resultDto;
	}

	/**
	 * 微信网页端 公众账号服务号
	 * @Title: unifiedorder
	 * @Description: TODO(向微信服务器发送请求，获取预支付订单号)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return ActResultDto    返回类型
	 */
	public ActResultDto unifiedorderH5(WeiXinOrder wx_order,String ip){
		ActResultDto resultDto = new ActResultDto();
		//组装包体
		String xml = getPakageH5(wx_order, ip);
		String result = null;
		String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder",xml);
			System.out.println("微信支付返回数据 ---》" + result);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String prepay_id = xmlutil.getChildText("prepay_id");
				String appid = xmlutil.getChildText("appid");
				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				TreeMap<String, String> map = new TreeMap<String,String>();
				long timestamp = new Date().getTime()/1000;
				map.put("appId", appid);
				map.put("timeStamp", timestamp+"");
				map.put("nonceStr", nonce_str);
				map.put("signType", "MD5");
				map.put("package", "prepay_id=" + prepay_id);
				String sign =  getSignStr(map, partnerKey);
				String signValue = DigestUtils.md5Hex(sign).toUpperCase();
				map.put("paySign", signValue);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
				System.out.println("微信支付返回数据 ---》" + result + ",发送的数据" + xml);
				log.error("微信支付发出去{}，微信支付返回数据 ---》{}", xml, result);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return resultDto;
	}

	@Override
	public ActResultDto unifiedRelayorderH5(WeiXinOrder wx_order, String ip) {
		ActResultDto resultDto = new ActResultDto();
		//组装包体
		String xml = getPakageRelayH5(wx_order, ip);
		String result = null;
		String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder",xml);
			System.out.println("微信支付返回数据 ---》" + result);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String prepay_id = xmlutil.getChildText("prepay_id");
				String appid = xmlutil.getChildText("appid");
				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				TreeMap<String, String> map = new TreeMap<String,String>();
				long timestamp = new Date().getTime()/1000;
				map.put("appId", appid);
				map.put("timeStamp", timestamp+"");
				map.put("nonceStr", nonce_str);
				map.put("signType", "MD5");
				map.put("package", "prepay_id=" + prepay_id);
				String sign =  getSignStr(map, partnerKey);
				String signValue = DigestUtils.md5Hex(sign).toUpperCase();
				map.put("paySign", signValue);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
				System.out.println("微信支付返回数据 ---》" + result + ",发送的数据" + xml);
				log.error("微信支付发出去{}，微信支付返回数据 ---》{}", xml, result);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return resultDto;
	}

	@Override
	public ActResultDto buyFlowOrderH5(WeiXinOrder wx_order, String ip) {
		ActResultDto resultDto = new ActResultDto();
		//组装包体
		String xml = getBuyFlowH5(wx_order, ip);
		String result = null;
		String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder",xml);
			System.out.println("微信支付返回数据 ---》" + result);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String prepay_id = xmlutil.getChildText("prepay_id");
				String appid = xmlutil.getChildText("appid");
				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				TreeMap<String, String> map = new TreeMap<String,String>();
				long timestamp = new Date().getTime()/1000;
				map.put("appId", appid);
				map.put("timeStamp", timestamp+"");
				map.put("nonceStr", nonce_str);
				map.put("signType", "MD5");
				map.put("package", "prepay_id=" + prepay_id);
				String sign =  getSignStr(map, partnerKey);
				String signValue = DigestUtils.md5Hex(sign).toUpperCase();
				map.put("paySign", signValue);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
				System.out.println("微信支付返回数据 ---》" + result + ",发送的数据" + xml);
				log.error("微信支付发出去{}，微信支付返回数据 ---》{}", xml, result);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return resultDto;
	}

	/**
	 *
	 * @Title: unifiedorder
	 * @Description: TODO(向微信服务器发送请求，获取预支付订单号)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return ActResultDto    返回类型
	 */
	public ActResultDto buyFlowOrderApp(WeiXinOrder wx_order,String ip){
		ActResultDto resultDto = new ActResultDto();
		//组装包体
		String xml = getPakageBuyFlowApp(wx_order, ip);
		String result = null;
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder",xml);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String prepay_id = xmlutil.getChildText("prepay_id");
				String appid = xmlutil.getChildText("appid");
				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				TreeMap<String, String> map = new TreeMap<String,String>();
				map.put("appid", appid);
				map.put("noncestr", nonce_str);
				map.put("prepayid", prepay_id);
				map.put("partnerid", mch_id);
				map.put("package", "Sign=WXPay");
				String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
				long timestamp = new Date().getTime()/1000;
				map.put("timestamp", timestamp+"");
				String sign =  sign(map, partnerKey);
				map.put("sign", sign);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return resultDto;
	}


	/**
	 * 订单查询
	 * @param wx_order
	 * @return
	 * @throws Exception
	 */
	public  ActResultDto orderquery(WeiXinOrder wx_order){
		ActResultDto resultDto = new ActResultDto();
		String xml = getPakageForQuery(wx_order);
		String result = null;
		String partnerKey= CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		try {
			result = HttpUtil.doPost("https://api.mch.weixin.qq.com/pay/orderquery",xml);
			XmlUtil xmlutil = new XmlUtil(result);
			String return_code = xmlutil.getChildText("return_code");
			String result_code = xmlutil.getChildText("result_code");
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				String appid = xmlutil.getChildText("appid");
//				String mch_id = xmlutil.getChildText("mch_id");
				String nonce_str = xmlutil.getChildText("nonce_str");
				String trade_state =  xmlutil.getChildText("trade_state");
				String trade_state_desc =  xmlutil.getChildText("trade_state_desc");
				TreeMap<String, String> map = new TreeMap<String,String>();
				long timestamp = new Date().getTime()/1000;
				map.put("appId", appid);
				map.put("timeStamp", timestamp+"");
				map.put("nonceStr", nonce_str);
				map.put("signType", "MD5");
				map.put("trade_state", trade_state+"");
				map.put("trade_state_desc", trade_state_desc);
				String sign =  sign(map, partnerKey);
				String signValue = DigestUtils.md5Hex(sign).toUpperCase();
				map.put("paySign", signValue);
				resultDto.setData(map);
			}else{
				String return_msg = xmlutil.getChildText("return_msg");
				resultDto.setCode(return_code);
				resultDto.setMessage(return_msg);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return resultDto;
	}
	/**
	 *
	 * @Title: getPakage
	 * @Description: TODO(组装请求微信接口的body)
	 * @param wx_order
	 * @param
	 * @return
	 * @return String    返回类型
	 */
	public  String getPakageForQuery(WeiXinOrder wx_order){
	    String weixin_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String weixin_mch_id= CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");
		String partnerKey= CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", weixin_appid);
		treeMap.put("mch_id", weixin_mch_id);
		treeMap.put("nonce_str", UUIDGenerator.generate());
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		String sign = sign(treeMap, partnerKey);
		treeMap.put("sign", sign);
		StringBuilder xml = new StringBuilder();
		xml.append("<xml>\n");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
		}
		xml.append("</xml>");
		return xml.toString();
	}
	/**
	 * 
	* @Title: getPakage
	* @Description: TODO(组装请求微信接口的body)
	* @param wx_order
	* @param ip
	* @return
	* @return String    返回类型
	 */
	public String getPakage(WeiXinOrder wx_order,String ip){
		
		//改定订单单位,元 改成 分
		BigDecimal de = new BigDecimal(wx_order.getTotal_fee());
		BigDecimal b = new BigDecimal(100);
		BigDecimal mony = de.multiply(b);
		String website=CustomizedPropertyConfigurer.getContextProperty("website");
		String notify_url= website + CustomizedPropertyConfigurer.getContextProperty("weixin_callBack_url");
		String weixin_appid=CustomizedPropertyConfigurer.getContextProperty("app.wechat.appid");
		String weixin_mch_id=CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", weixin_appid);  
		treeMap.put("body", wx_order.getDes());  
		treeMap.put("mch_id", weixin_mch_id);  
		treeMap.put("nonce_str", UUIDGenerator.generate());  
		treeMap.put("notify_url", notify_url);
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		treeMap.put("spbill_create_ip", ip);  
		treeMap.put("total_fee", mony.intValue()+"");  //单位是分
		treeMap.put("trade_type","APP");
		String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
		String sign =  sign(treeMap , partnerKey);
		treeMap.put("sign", sign);
		StringBuilder xml = new StringBuilder();  
		xml.append("<xml>\n");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {  
			xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");  
		}  
		xml.append("</xml>");  
		return xml.toString();
	}
	/**
	 *
	 * @Title: getPakage
	 * @Description: TODO(组装请求微信接口的body)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return String    返回类型
	 */
	public String getRelayPakage(WeiXinOrder wx_order,String ip){

		//改定订单单位,元 改成 分
		BigDecimal de = new BigDecimal(wx_order.getTotal_fee());
		BigDecimal b = new BigDecimal(100);
		BigDecimal mony = de.multiply(b);
		String website=CustomizedPropertyConfigurer.getContextProperty("website");
		String notify_url= website + CustomizedPropertyConfigurer.getContextProperty("weixin_callBack_relay_url");
		String weixin_appid=CustomizedPropertyConfigurer.getContextProperty("app.wechat0.appid");
		String weixin_mch_id=CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", weixin_appid);
		treeMap.put("body", wx_order.getDes());
		treeMap.put("mch_id", weixin_mch_id);
		treeMap.put("nonce_str", UUIDGenerator.generate());
		treeMap.put("notify_url", notify_url);
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		treeMap.put("spbill_create_ip", ip);
		treeMap.put("total_fee", mony.intValue()+"");  //单位是分
		treeMap.put("trade_type","APP");
		String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
		String sign =  sign(treeMap , partnerKey);
		treeMap.put("sign", sign);
		StringBuilder xml = new StringBuilder();
		xml.append("<xml>\n");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
		}
		xml.append("</xml>");
		return xml.toString();
	}

	/**
	 *
	 * @Title: getPakage
	 * @Description: TODO(组装请求微信接口的body)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return String    返回类型
	 */
	public String getPakageBuyFlowApp(WeiXinOrder wx_order,String ip){

		//改定订单单位,元 改成 分
		BigDecimal de = new BigDecimal(wx_order.getTotal_fee());
		BigDecimal b = new BigDecimal(100);
		BigDecimal mony = de.multiply(b);
		String website=CustomizedPropertyConfigurer.getContextProperty("website");
		String notify_url= website + CustomizedPropertyConfigurer.getContextProperty("weixin_H5_buy_flow_callBack_url");
		String weixin_appid=CustomizedPropertyConfigurer.getContextProperty("app.wechat.appid");
		String weixin_mch_id=CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", weixin_appid);
		treeMap.put("body", wx_order.getDes());
		treeMap.put("mch_id", weixin_mch_id);
		treeMap.put("nonce_str", UUIDGenerator.generate());
		treeMap.put("notify_url", notify_url);
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		treeMap.put("spbill_create_ip", ip);
		treeMap.put("total_fee", mony.intValue()+"");  //单位是分
		treeMap.put("trade_type","APP");
		String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
		String sign =  sign(treeMap , partnerKey);
		treeMap.put("sign", sign);
		StringBuilder xml = new StringBuilder();
		xml.append("<xml>\n");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
		}
		xml.append("</xml>");
		return xml.toString();
	}

	public static void main(String[] args) {
		WeiXinServiceImpl weiXinService = new WeiXinServiceImpl();
		WeiXinOrder wx_order = new WeiXinOrder();
		wx_order.setOut_trade_no("sz_2017032032717492064");
		ActResultDto actResultDto = weiXinService.orderquery(wx_order);
	}

	/**
	 *
	 * @Title: getPakage
	 * @Description: TODO(组装请求微信接口的body)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return String    返回类型
	 */
	public String getPakageH5(WeiXinOrder wx_order,String ip){
		//改定订单单位,元 改成 分
		BigDecimal de = new BigDecimal(wx_order.getTotal_fee());
		BigDecimal b = new BigDecimal(100);
		BigDecimal mony = de.multiply(b);
		String website=CustomizedPropertyConfigurer.getContextProperty("website");
		String notify_url= website + CustomizedPropertyConfigurer.getContextProperty("weixin_callBack_url");
		String appId=  CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String weixin_mch_id=  CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");
		String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", appId);
		//treeMap.put("attach", "测试");
		treeMap.put("body", wx_order.getDes());
		treeMap.put("mch_id", weixin_mch_id);
		treeMap.put("nonce_str", UUIDGenerator.generate());
		treeMap.put("notify_url", notify_url);
		treeMap.put("device_info", "WEB");
		treeMap.put("openid", wx_order.getOpenid());
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		treeMap.put("spbill_create_ip", ip);
		treeMap.put("total_fee", mony.longValue() + "");  //单位是分
		treeMap.put("trade_type","JSAPI");
		String sign =  getSignStr(treeMap, partnerKey);
		String signValue = DigestUtils.md5Hex(sign).toUpperCase();
		treeMap.put("sign", signValue);
		StringBuilder xml = new StringBuilder();
		xml.append("<xml>");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			xml.append("<" + entry.getKey() + ">")
					.append("<![CDATA[" + entry.getValue() + "]]>").append("</" + entry.getKey() + ">");
			//xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
		}
		xml.append("</xml>");
		return xml.toString();
	}

	/**
	 *
	 * @Title: getPakage
	 * @Description: TODO(组装请求微信接口的body)
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return String    返回类型
	 */
	public String getPakageRelayH5(WeiXinOrder wx_order,String ip){
		//改定订单单位,元 改成 分
		BigDecimal de = new BigDecimal(wx_order.getTotal_fee());
		BigDecimal b = new BigDecimal(100);
		BigDecimal mony = de.multiply(b);
		String website=CustomizedPropertyConfigurer.getContextProperty("website");
		String notify_url= website + CustomizedPropertyConfigurer.getContextProperty("weixin_callBack_relay_url");
		String appId=  CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String weixin_mch_id=  CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");
		String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", appId);
		//treeMap.put("attach", "测试");
		treeMap.put("body", wx_order.getDes());
		treeMap.put("mch_id", weixin_mch_id);
		treeMap.put("nonce_str", UUIDGenerator.generate());
		treeMap.put("notify_url", notify_url);
		treeMap.put("device_info", "WEB");
		treeMap.put("openid", wx_order.getOpenid());
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		treeMap.put("spbill_create_ip", ip);
		treeMap.put("total_fee", mony.longValue() + "");  //单位是分
		treeMap.put("trade_type","JSAPI");
		String sign =  getSignStr(treeMap, partnerKey);
		String signValue = DigestUtils.md5Hex(sign).toUpperCase();
		treeMap.put("sign", signValue);
		StringBuilder xml = new StringBuilder();
		xml.append("<xml>");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			xml.append("<" + entry.getKey() + ">")
					.append("<![CDATA[" + entry.getValue() + "]]>").append("</" + entry.getKey() + ">");
			//xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
		}
		xml.append("</xml>");
		return xml.toString();
	}

	/**
	 *  微信H5_流量购买
	 * @Title: getBuyFlowH5
	 * @Description:
	 * @param wx_order
	 * @param ip
	 * @return
	 * @return String    返回类型
	 */
	public String getBuyFlowH5(WeiXinOrder wx_order,String ip){
		//改定订单单位,元 改成 分
		BigDecimal de = new BigDecimal(wx_order.getTotal_fee());
		BigDecimal b = new BigDecimal(100);
		BigDecimal mony = de.multiply(b);
		String website=CustomizedPropertyConfigurer.getContextProperty("website");
		String notify_url= website + CustomizedPropertyConfigurer.getContextProperty("weixin_H5_buy_flow_callBack_url");
		String appId=  CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
		String weixin_mch_id=  CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");
		String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", appId);
		//treeMap.put("attach", "测试");
		treeMap.put("body", wx_order.getDes());
		treeMap.put("mch_id", weixin_mch_id);
		treeMap.put("nonce_str", UUIDGenerator.generate());
		treeMap.put("notify_url", notify_url);
		treeMap.put("device_info", "WEB");
		treeMap.put("openid", wx_order.getOpenid());
		treeMap.put("out_trade_no", wx_order.getOut_trade_no());  //商户订单号
		treeMap.put("spbill_create_ip", ip);
		treeMap.put("total_fee", mony.longValue() + "");  //单位是分
		treeMap.put("trade_type","JSAPI");
		String sign =  getSignStr(treeMap, partnerKey);
		String signValue = DigestUtils.md5Hex(sign).toUpperCase();
		treeMap.put("sign", signValue);
		StringBuilder xml = new StringBuilder();
		xml.append("<xml>");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			xml.append("<" + entry.getKey() + ">")
					.append("<![CDATA[" + entry.getValue() + "]]>").append("</" + entry.getKey() + ">");
			//xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
		}
		xml.append("</xml>");
		return xml.toString();
	}

	/**
	 *
	 * @Title: sign
	 * @Description: TODO(微信签名)
	 * @param treeMap
	 * @return
	 * @return String    返回类型
	 */
	public static String getSignStr(TreeMap<String, String> treeMap , String partnerKey){
		StringBuilder sb = new StringBuilder();
		for (String key : treeMap.keySet()) {
			sb.append(key).append("=").append(treeMap.get(key)).append("&");
		}
		sb.append("key=" + partnerKey);
		return sb.toString();
	}
	/**
	 * 
	* @Title: sign
	* @Description: TODO(微信签名)
	* @param treeMap
	* @return
	* @return String    返回类型
	 */
/*	public static String sign(TreeMap<String, String> treeMap){
		String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
		String sign =  sign(treeMap, partnerKey);
		String sig2 =  MD5Encode(sign).toUpperCase();
		return sig2;
	}*/

	/**
	 *
	 * @Title: sign
	 * @Description: TODO(微信签名)
	 * @param treeMap
	 * @return
	 * @return String    返回类型
	 */
	public static String sign(TreeMap<String, String> treeMap ,String partnerKey){
		String sign =  getSignStr(treeMap, partnerKey);
		String sig2 =  MD5Encode(sign).toUpperCase();
		return sig2;
	}

	private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",  
			"8", "9", "a", "b", "c", "d", "e", "f"};  

	/** 
	 * 转换字节数组为16进制字串 
	 * @param b 字节数组 
	 * @return 16进制字串 
	 */  
	public static String byteArrayToHexString(byte[] b) {  
		StringBuilder resultSb = new StringBuilder();  
		for (byte aB : b) {  
			resultSb.append(byteToHexString(aB));  
		}  
		return resultSb.toString();  
	}  

	/** 
	 * 转换byte到16进制 
	 * @param b 要转换的byte 
	 * @return 16进制格式 
	 */  
	private static String byteToHexString(byte b) {  
		int n = b;  
		if (n < 0) {  
			n = 256 + n;  
		}  
		int d1 = n / 16;  
		int d2 = n % 16;  
		return hexDigits[d1] + hexDigits[d2];  
	}  

	/** 
	 * MD5编码 
	 * @param origin 原始字符串 
	 * @return 经过MD5加密之后的结果 
	 */  
	public static String MD5Encode(String origin) {  
		String resultString = null;  
		try {  
			resultString = origin;  
			MessageDigest md = MessageDigest.getInstance("MD5");  
			md.update(resultString.getBytes("UTF-8"));  
			resultString = byteArrayToHexString(md.digest());  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return resultString;  
	}


	/***
	 * 核心方法，里面有递归调用
	 *
	 * @param ele
	 */
	public static TreeMap<String, String> ele2map(Element ele) {
		List<Element> elements = ele.elements();
		TreeMap<String, String> tempMap = new TreeMap<String, String>();
		for (Element element : elements) {
			String name = element.getName();
			String text = element.getText();
			if(!"sign".equals(name) && text != null && !"".equals(text)){
				tempMap.put(name, text);
			}
		}
		return tempMap;
	}

	/**
	 * 处理用户导航记录，存入redis
	 * @param request
	 * @param appUserIdentity
	 * @param uri
	 */
	public void handlerUserNavivationRecord(HttpServletRequest request ,  AppUserIdentity appUserIdentity , String uri){
		if(appUserIdentity != null ){
			String val = WebNavigationType.getValByUrl(uri);
			if(!Utility.isNullorEmpty(val)){ //需要处理
				String userNavRecord = redisUtil.hget(RedisKey.ll_live_web_user_navigation_record , appUserIdentity.getId() + "" );
				if(!Utility.isNullorEmpty(userNavRecord)){
					if(userNavRecord.contains("," + val + ",")){
						request.setAttribute(Const.longlian_live_user_web_navigation_sign , "1");
					}else {
						redisUtil.hset(RedisKey.ll_live_web_user_navigation_record, appUserIdentity.getId() + "",userNavRecord + val + "," );
						request.setAttribute(Const.longlian_live_user_web_navigation_sign, "0");
					}
				}else{
					redisUtil.hset(RedisKey.ll_live_web_user_navigation_record, appUserIdentity.getId() + "", "," + val + "," );
					request.setAttribute(Const.longlian_live_user_web_navigation_sign, "0");
				}
			}else {
				request.setAttribute(Const.longlian_live_user_web_navigation_sign , "0");
			}
		}else{
			request.setAttribute(Const.longlian_live_user_web_navigation_sign , "");
		}
	}

}
