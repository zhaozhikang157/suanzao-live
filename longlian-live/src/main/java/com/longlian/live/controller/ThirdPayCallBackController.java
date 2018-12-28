package com.longlian.live.controller;

import com.huaxin.util.XmlUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AlipayService;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.OrdersService;
import com.longlian.live.service.WeiXinService;
import com.longlian.live.service.impl.WeiXinServiceImpl;
import com.longlian.live.util.log.Log;
import com.longlian.live.util.log.RequestInfoContext;
import com.longlian.model.AppUser;
import com.longlian.model.DataChargeRecord;
import com.longlian.model.Orders;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import com.longlian.type.PayType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Administrator on 2016/8/15.
 */

@Controller
@RequestMapping("/thirdPayCallBack")
public class ThirdPayCallBackController {
    private static Logger log = LoggerFactory.getLogger(ThirdPayCallBackController.class);
    @Autowired
    WeiXinService weiXinService;

    @Autowired
    AlipayService alipayService;

    @Autowired
    OrdersService ordersService;

    @Autowired
    AppUserService appUserService;

    /**
     * 微信异步回调
     * @param req
     * @param body
     * @return
     */
    @RequestMapping(value = "/weixin" )
    @Log(content="{logModel.payTypeDesc},金额：{logModel.money}，订单号:{logModel.orderNo},支付描述:{logModel.message}", type= LogType.third_pay_thirdPayCallBack, systemType = "0")
    @ApiOperation(value = "微信异步回调", httpMethod = "GET", notes = "微信异步回调")
    public  String weixinPayCallBack(HttpServletRequest req,
                                     @ApiParam(required =true, name = "报文", value = "报文") @RequestBody String body){
        log.info("====================>>>>>>>>>>>>微信异步通知消息：" + body);
        Map mapP = new HashMap();
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        try {
            XmlUtil xml = new XmlUtil(body);
            String return_code = xml.getChildText("return_code");
            if (return_code.equals("SUCCESS")) {
                String out_trade_no = xml.getChildText("out_trade_no");
                String transaction_id  = xml.getChildText("transaction_id");//微信支付订单号,暂被弃用
                String appid = xml.getChildText("appid");//获取appid
                String sign = xml.getChildText("sign");
                Document doc = DocumentHelper.parseText(body);
                Element rootElement = doc.getRootElement();
                TreeMap<String, String> map = WeiXinServiceImpl.ele2map(rootElement);
                String createSign = "";
                String payType  = PayType.weixin.getValue();
                String appId=  CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
                if(appId.equals(appid)){
                    String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
                    createSign = WeiXinServiceImpl.sign(map , partnerKey);
                    //createSign = DigestUtils.md5Hex(createSign).toUpperCase();
                    //H5，网页
                    payType  = PayType.weixin_h5.getValue();
                }else{
                    String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
                    createSign = WeiXinServiceImpl.sign(map ,partnerKey);
                }
                if(createSign.equals(sign)){
                    ActResultDto actResultDto =  weiXinService.callBankUrl(out_trade_no , payType);//系统业务
                    if(actResultDto.getExt() != null){
                        Orders o =  (Orders)actResultDto.getExt();
                        mapP.put("money",  o.getRealAmount() );
                        mapP.put("orderNo",o.getOrderNo());
                        sendSystemLog(o);
                    }
                }else{
                    log.info("====>>异步通知,签名失败!");
                    mapP.put("payTypeDesc", "异步通知,签名失败!appid:" + appid + ";out_trade_no:" + out_trade_no + ";createSign:" + createSign + ";sign:" + sign);
                }
                mapP.put("payTypeDesc", "订单号【" + out_trade_no + "】支付成功appid:");
                //支付成功后业务
            } else {
                log.info("微信异步通知,返回结果错误,return_code=" + return_code);
                mapP.put("payTypeDesc" ,   "微信异步通知,返回结果错误," + body);
            }
        } catch (Exception e) {
            log.error("", e);
            e.printStackTrace();
            mapP.put("payTypeDesc" ,   "后台出错,返回结果错误," + e.getMessage());
        }
        //给微信段做出响应
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
    /**
     * 微信异步回调（转播购买回调）
     * @param req
     * @param body
     * @return
     * @author qym
     */
    @RequestMapping(value = "/weixinRelay" )
    @Log(content="{logModel.payTypeDesc},金额：{logModel.money}，订单号:{logModel.orderNo},支付描述:{logModel.message}", type= LogType.third_pay_thirdPayCallBack, systemType = "0")
    @ApiOperation(value = "微信异步回调", httpMethod = "GET", notes = "微信异步回调")
    public  String weixinRelayPayCallBack(HttpServletRequest req,
                                     @ApiParam(required =true, name = "报文", value = "报文") @RequestBody String body){
        log.info("====================>>>>>>>>>>>>微信异步通知消息：" + body);

        Map mapP = new HashMap();
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        try {
            XmlUtil xml = new XmlUtil(body);
            String return_code = xml.getChildText("return_code");
            if (return_code.equals("SUCCESS")) {
                String out_trade_no = xml.getChildText("out_trade_no");
                String transaction_id  = xml.getChildText("transaction_id");//微信支付订单号,暂被弃用
                String appid = xml.getChildText("appid");//获取appid
                String sign = xml.getChildText("sign");
                Document doc = DocumentHelper.parseText(body);
                Element rootElement = doc.getRootElement();
                TreeMap<String, String> map = WeiXinServiceImpl.ele2map(rootElement);
                String createSign = "";
                String payType  = PayType.weixin.getValue();
                String appId=  CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
                if(appId.equals(appid)){
                    String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
                    createSign = WeiXinServiceImpl.sign(map , partnerKey);
                    //createSign = DigestUtils.md5Hex(createSign).toUpperCase();
                    //H5，网页
                    payType  = PayType.weixin_h5.getValue();
                }else{
                    String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
                    createSign = WeiXinServiceImpl.sign(map ,partnerKey);
                }
                if(createSign.equals(sign)){
                    ActResultDto actResultDto =  weiXinService.callBankRelayUrl(out_trade_no , payType);//系统业务
                    if(actResultDto.getExt() != null){
                        Orders o =  (Orders)actResultDto.getExt();
                        mapP.put("money",  o.getRealAmount() );
                        mapP.put("orderNo",o.getOrderNo());
                        sendSystemLog(o);
                    }
                }else{
                    log.info("====>>异步通知,签名失败!");
                    mapP.put("payTypeDesc", "异步通知,签名失败!appid:" + appid + ";out_trade_no:" + out_trade_no + ";createSign:" + createSign + ";sign:" + sign);
                }
                mapP.put("payTypeDesc", "订单号【" + out_trade_no + "】支付成功appid:");
                //支付成功后业务
            } else {
                log.info("微信异步通知,返回结果错误,return_code=" + return_code);
                mapP.put("payTypeDesc" ,   "微信异步通知,返回结果错误," + body);
            }
        } catch (Exception e) {
            log.error("", e);
            e.printStackTrace();
            mapP.put("payTypeDesc" ,   "后台出错,返回结果错误," + e.getMessage());
        }
        //给微信段做出响应
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }


    /**
     * 微信异步回调
     * @param req
     * @param orderNumber
     * @return
     */
    @RequestMapping(value = "/test" )
    @ResponseBody
    @ApiOperation(value = "微信异步回调", httpMethod = "GET", notes = "微信异步回调")
    public ActResultDto test(HttpServletRequest req,
                             @ApiParam(required =true, name = "订单号", value = "订单号")  String orderNumber)throws  Exception{
        ActResultDto resultDto =  weiXinService.callBankUrl(orderNumber ,PayType.weixin.getValue());
        return resultDto;
    }



    /**
     * 支付宝回调
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alipay",method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc},金额：{logModel.money}，订单号:{logModel.orderNo},支付描述:{logModel.message}", type= LogType.third_pay, systemType = "0")
    @ApiOperation(value = "支付宝回调", httpMethod = "GET", notes = "支付宝回调")
    public String alipayCallBack(HttpServletRequest request) throws Exception {
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        ActResultDto actResultDto =  alipayService.alipayAppCalBack(request);
        if(actResultDto.getExt() != null){
            Orders o =  (Orders)actResultDto.getExt();
            sendSystemLog(o);
        }
        return  actResultDto.getData().toString();
    }

/*

    */
/**
     * 银联回调
     * @return
     * @throws Exception
     *//*

    @RequestMapping(value = "/unionPay",method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc},金额：{logModel.money}，订单号:{logModel.orderNo},支付描述:{logModel.message}", type= LogType.third_pay, systemType = "0")
    public String unionPay(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            RequestInfoContext.getRequestInfo().setIsCreateLog(false);
            PrintWriter out = null;
            try {
                LogUtil.wrriteLog("BackRcvResponse接收后台通知开始");
                String encoding = req.getParameter(SDKConstants.param_encoding);
                // 获取银联通知服务器发送的后台通知参数
                Map<String, String> reqParam = getAllRequestParam(req);
                LogUtil.printRequestLog(reqParam);
                Map<String, String> valideData = null;
                if (null != reqParam && !reqParam.isEmpty()) {
                    Iterator<Map.Entry<String, String>> it = reqParam.entrySet().iterator();
                    valideData = new HashMap<String, String>(reqParam.size());
                    while (it.hasNext()) {
                        Map.Entry<String, String> e = it.next();
                        String key = (String) e.getKey();
                        String value = (String) e.getValue();
                        value = new String(value.getBytes(encoding), encoding);
                        valideData.put(key, value);
                    }
                }
                //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
                if (!AcpService.validate(valideData, encoding)) {
                    LogUtil.writeLog("验证签名结果[失败].");
                } else {
                    LogUtil.writeLog("验证签名结果[成功].");
                    String orderNo = valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
                    String orderId = valideData.get("reqReserved");//扩展字段 订单表ID
                    String queryId = valideData.get("queryId");//银联订单号
                    Orders o = ordersService.thirdPay(orderNo, PayType.unionpay.getValue(), queryId);
                    if(o != null){
                        sendSystemLog(o);
                    }
                }
                LogUtil.writeLog("BackRcvResponse接收后台通知结束");
                //返回给银联服务器http 200  状态码
                out = resp.getWriter();
                out.print("ok");
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(out != null){
                    out.close();
                }
            }
        return null;
    }
*/


    /**
     * 获取请求参数中所有的信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                //System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }


    /**
     *  发送日志
     * @param o
     */
    public  void sendSystemLog(Orders o){
        RequestInfoContext.getRequestInfo().setIsCreateLog(true);
        RequestInfoContext.getRequestInfo().setUserId(o.getAppId());
        AppUser appUser369 = appUserService.getById(o.getAppId());
        if(appUser369 != null){
            RequestInfoContext.getRequestInfo().setUserName(appUser369.getMobile());
        }
        Map mapP = new HashMap();
        String payTypeDesc = PayType.getNameByValue(o.getBankType());
        mapP.put("payTypeDesc", "第三方支付回调:支付方式:" + payTypeDesc);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        mapP.put("money", o.getRealAmount());
        RequestInfoContext.getRequestInfo().setTableId(o.getId());
        RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
        mapP.put("orderNo", "\"" + o.getOrderNo() + "\"" );
        mapP.put("message", "支付成功");
    }

    /**
     * 微信充值流量回调
     * @param req
     * @param body
     * @return
     */
    @RequestMapping(value = "/weixinH5BuyFlow" )
    @Log(content="{logModel.payTypeDesc},金额：{logModel.money}，订单号:{logModel.orderNo},支付描述:{logModel.message}", type= LogType.third_pay_thirdPayCallBack, systemType = "0")
    @ApiOperation(value = "微信充值流量回调", httpMethod = "GET", notes = "微信充值流量回调")
    public  String weixinH5BuyFlow(HttpServletRequest req,@ApiParam(required =true, name = "报文", value = "报文")  @RequestBody String body){
        log.info("====================>>>>>>>>>>>>微信异步通知消息：" + body);
        Map mapP = new HashMap();
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        try {
            XmlUtil xml = new XmlUtil(body);
            String return_code = xml.getChildText("return_code");
            if (return_code.equals("SUCCESS")) {
                String out_trade_no = xml.getChildText("out_trade_no");
                String transaction_id  = xml.getChildText("transaction_id");//微信支付订单号,暂被弃用
                String appid = xml.getChildText("appid");//获取appid
                String sign = xml.getChildText("sign");
                Document doc = DocumentHelper.parseText(body);
                Element rootElement = doc.getRootElement();
                TreeMap<String, String> map = WeiXinServiceImpl.ele2map(rootElement);
                String createSign = "";
                String payType  = PayType.weixin_H5_buy_flow.getValue();
                String appId=  CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
                if(appId.equals(appid)){
                    String partnerKey=  CustomizedPropertyConfigurer.getContextProperty("wechat.partnerKey");
                    createSign = WeiXinServiceImpl.sign(map , partnerKey);
                    //createSign = DigestUtils.md5Hex(createSign).toUpperCase();
                    //H5，网页
                    payType  = PayType.weixin_H5_buy_flow.getValue();
                }else{
                    String partnerKey= CustomizedPropertyConfigurer.getContextProperty("app.wechat.partnerKey");
                    createSign = WeiXinServiceImpl.sign(map ,partnerKey);
                }
                if(createSign.equals(sign)){
                    ActResultDto actResultDto =  weiXinService.callBankUrlByH5BuyFlow(out_trade_no , payType);//系统业务
                    if(actResultDto.getExt() != null){
                        Orders o =  (Orders)actResultDto.getExt();
                        mapP.put("money",  o.getRealAmount() );
                        mapP.put("orderNo",o.getOrderNo());
                        sendSystemLog(o);
                    }
                }else{
                    log.info("====>>异步通知,签名失败!");
                    mapP.put("payTypeDesc", "异步通知,签名失败!appid:" + appid + ";out_trade_no:" + out_trade_no + ";createSign:" + createSign + ";sign:" + sign);
                    weiXinService.callBankUrlByH5BuyFlowError(out_trade_no , payType);
                }
                mapP.put("payTypeDesc", "订单号【" + out_trade_no + "】支付成功appid:");
                //支付成功后业务
            } else {
                log.info("微信异步通知,返回结果错误,return_code=" + return_code);
                mapP.put("payTypeDesc", "微信异步通知,返回结果错误," + body);
            }
        } catch (Exception e) {
            log.error("", e);
            e.printStackTrace();
            mapP.put("payTypeDesc" ,   "后台出错,返回结果错误," + e.getMessage());
        }
        //给微信段做出响应
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}
