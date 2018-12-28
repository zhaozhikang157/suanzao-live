package com.longlian.live.util;

import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.SystemParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by syl on 2016/6/15.
 */
@Component("systemParaRedisUtil")
public class SystemParaRedisUtil {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SystemParaService systemParaService;

    @Autowired
    AppUserCommonService appUserCommonService;


    /**
     * 重新将参数保存到redis中
     */
    public void resetSystemParam(){
        List<Map<String , String>> list = systemParaService.getAllList();
        for (Map<String , String> temp : list ){
            String value = temp.get("value");
            if("bank_out_remark".equals( temp.get("code").toString())){
                value = temp.get("describe");
            }
            redisUtil.hset(RedisKey.ll_live_system_param, temp.get("code"), value);
        }
    }

    /**
     * 获取系统参数 by code
     * @param code
     * @return
     */
    public String getSystemParamValueByCode(String code){
        if(!redisUtil.exists(RedisKey.ll_live_system_param)) resetSystemParam();
        String value =  redisUtil.hget(RedisKey.ll_live_system_param, code);
        return value;
    }
    /**
     * 保证金充值 --App端 -- 银联 -- 我们收取客户费率
     * @return
     */
    public BigDecimal getAppUnionPayFlatformPercent(){
        BigDecimal bigDecimal = new BigDecimal(0);
        String value =  getSystemParamValueByCode("app_unionpay_flatform_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 保证金充值  --App端 -- 银联 -- 银联收取我们的费率
     * @return
     */
    public BigDecimal getAppUnionPayPercent(){
        BigDecimal bigDecimal = BigDecimal.valueOf(0.007);
        String value =  getSystemParamValueByCode("app_unionpay_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     * 保证金充值 --APP端 -- 银联 收取我们最低手续费
     * @return
     */
    public BigDecimal getAPPUnionPayMinCharge(){
        BigDecimal bigDecimal = BigDecimal.valueOf(0.08);
        String value =  getSystemParamValueByCode("app_unionpay_min_charge");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     * 保证金充值 --App端 -- 支付宝 -- 我们收取客户费率
     * @return
     */
    public BigDecimal getAppAliPayFlatformPercent(){
        BigDecimal bigDecimal = new BigDecimal(0);
        String value =  getSystemParamValueByCode("app_alipay_flatform_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 保证金充值  --App端 -- 支付宝 -- 阿里收取我们的费率
     * @return
     */
    public BigDecimal getAppAliPayPercent(){
        BigDecimal bigDecimal = BigDecimal.valueOf(0.0055);
        String value =  getSystemParamValueByCode("app_alipay_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 保证金充值 --App端 -- 微信 -- 我们收取客户费率
     * @return
     */
    public BigDecimal getAppWeixinPayFlatformPercent(){
        BigDecimal bigDecimal = new BigDecimal(0);
        String value =  getSystemParamValueByCode("app_weixinpay_flatform_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 保证金充值  --App端 -- 微信 -- 收取我们的费率
     * @return
     */
    public BigDecimal getAppWeixinPayPercent(){
        BigDecimal bigDecimal = BigDecimal.valueOf(0.006);
        String value =  getSystemParamValueByCode("app_weixin_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     * 保证金充值  --H5微信端 -- 微信 -- 收取我们的费率
     * @return
     */
    public BigDecimal getAppWeixinH5PayPercent(){
        BigDecimal bigDecimal = BigDecimal.valueOf(0);
        String value =  getSystemParamValueByCode("app_weixin_h5_percent");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     *  会员年费
     * @return
     */
    public BigDecimal getVipAnnualFees(){
        BigDecimal bigDecimal = new BigDecimal(369);
        String value =  getSystemParamValueByCode("vip_annual_fees");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 银联提现费率
     * @return
     */
    public BigDecimal getAppBankOutChinapayCharge(){
        BigDecimal bigDecimal = BigDecimal.valueOf(1.5);
        String value =  getSystemParamValueByCode("bank_out_chinapay_charge");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     *  一天最高提现最高金额
     * @return
     */
    public BigDecimal getBankOutHignMoney(){
        BigDecimal bigDecimal = new BigDecimal(5000);
        String value =  getSystemParamValueByCode("bank_out_hign_money");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     * 提现最低多少走费率
     * @return
     */
    public BigDecimal getAppBankOutChargeMin(){
        BigDecimal bigDecimal = BigDecimal.valueOf(20.01);
        String value =  getSystemParamValueByCode("app_bank_out_charge_min");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }



    /**
     * 提现手续费率
     * @return
     */
    public BigDecimal getAppBankOutChargePercent(Long id){
        Integer proportion = appUserCommonService.getProportion(id);
        BigDecimal bigDecimal = null;
        if(proportion!=null){
            bigDecimal = BigDecimal.valueOf((double)proportion / 100);
        }else{
            bigDecimal = BigDecimal.valueOf(0.1);
            String value =  getSystemParamValueByCode("app_bank_out_charge_percent");
            if(!Utility.isNullorEmpty(value)){
                bigDecimal = new BigDecimal(value);
            }
        }
        return bigDecimal;
    }

    /**
     * 提现手续费
     * @return
     */
    public BigDecimal getAppBankOutChargePercentFixed(){
        BigDecimal bigDecimal = new BigDecimal(3);
        String value =  getSystemParamValueByCode("bank_out_charge");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }

    /**
     * 提现多少金额需要审核
     * @return
     */
    public BigDecimal getBankOutChargeAudit(){
        BigDecimal bigDecimal = new BigDecimal(5000);
        String value =  getSystemParamValueByCode("bank_out_money_audit");
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 银联提现描述
     * @return
     */
    public String getBankOutRemark(Long id){
        BigDecimal counterFee = getAppBankOutChargePercentFixed();  //手续费
        BigDecimal hightAmount = getBankOutHignMoney(); //最高提现金额
        Integer proportion = appUserCommonService.getProportion(id);
        if(proportion==null){
            proportion = 10;
        }
        String remark = "提现规则说明：单笔提现金额20元（含）以下，每笔收取{}元手续费；单笔提现金额20元（不含）以上，每笔收取提现金额{}%的手续费。单笔最高提现金额为：5000元；每日累计最高提现金额为{}元；提现预计会在3个工作日内到账，请留您的银行卡资金变化。";
        String value =  getSystemParamValueByCode("bank_out_remark");
        if(!Utility.isNullorEmpty(value)){
            remark = value;
        }
        return Utility.replace(remark,counterFee.toString(),proportion.toString(),hightAmount.toString());    //替换字符串中的{}
    }

    /**
     * 合伙人提现描述
     * @return
     */
    public String getBankOutRemarkProxy(){
        
        BigDecimal counterFee = getAppBankOutChargePercentFixed();  //手续费
        BigDecimal hightAmount = getBankOutHignMoney(); //最高提现金额
        BigDecimal proxyCounterFee = getProxyReturnMoneyChargePercent().multiply(new BigDecimal(100));
        String remark = "提现说明：提现每笔收取{}元手续费，每月可提现两次，每次最高可提现{}元。作为机构旗下老师，会自动扣除提现金额的{}% 作为您合作机构的收益。提现预计会在3天内到账，请留意您的银行卡资金变化。";
        return Utility.replace(remark,counterFee.toString(),proxyCounterFee.toString(),hightAmount.toString());//替换字符串中的{}
    }

    /**
     * IOS 支付实收英文
     * @return
     */
    public BigDecimal getIOSPayReal(){
        BigDecimal bigDecimal = BigDecimal.valueOf(403.41);
        String value =  getSystemParamValueByCode("app_ios_pay_really");//app_ios_pay_really
        if(!Utility.isNullorEmpty(value)){
            bigDecimal = new BigDecimal(value);
        }
        return bigDecimal;
    }


    /**
     * 获取所有比例 有效的
     * @return
     */
    public List  getCourseDivideScale(){
        List list = new ArrayList();
        List<Map> allList = getAllCourseDivideScale();
        for (Map map : allList){
            String isUse = map.get("isUse").toString();
            if("0".equals(isUse)){
                list.add(map);
            }
        }
        return list;
    }

    public List  getAllCourseDivideScale(){
        List list = new ArrayList();
        String value =  getSystemParamValueByCode("course_divideScale");//app_ios_pay_really
        if (!StringUtils.isEmpty(value)) {
            list = JsonUtil.getList(value, Map.class);
        }
        return list;
    }

    /**
     * 获取单个分销比例
     * @param value  传 0、1参数值   0-5:5  1-6:4等
     * @return
     */
    public String  getCourseDivideScaleByValue(String value){
        if(Utility.isNullorEmpty(value)){
            return "";
        }
        List<Map> list =   getAllCourseDivideScale();
        for (Map map : list){
            String key = map.get("key").toString();
            if(value.equals(key)) return map.get("value").toString();
        }
        return "";
    }

    /**
     * 获取录音
     * @return
     */
    public String getRecordingLength() {
        String value = getSystemParamValueByCode("recording_length");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "10,180";
    }


    /**
     * 老师讲课奖励（不免费、所有的） 0-所有的 1-收费的且成功的
     * @return
     */
    public String getTeachCourseRewardMenCountIsFree() {
        String value = getSystemParamValueByCode("teach_course_reward_men_count_isfree");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "1";
    }
    /**
     * 老师讲课奖励规则
     * @return
     */
    public String getTeachCourseRewardRule() {
        String value = getSystemParamValueByCode("teach_course_reward_rule");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "";
    }

    /**
     * 新的老师讲课奖励规则
     * @return
     */
    public String getNewTeachCourseRewardRule() {
        String value = getSystemParamValueByCode("new_teach_course_reward");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "";
    }

    /**
     * 新的老师讲课奖励规则开始时间
     * @return
     */
    public String getNewTeachCourseRewardRuleStartTime() {
        String value = getSystemParamValueByCode("new_teach_course_reward_start_ime");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "";
    }
    /**
     * 新的老师讲课奖励结束时间
     * @return
     */
    public String getNewTeachCourseRewardRuleEndime() {
        String value = getSystemParamValueByCode("new_teach_course_reward_end_time");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "";
    }

    /**
     * 老师讲课奖励单个人金额
     * @return
     */
    public BigDecimal getTeachCourseRewardSingleAmount() {
        BigDecimal temp = BigDecimal.valueOf(0);
        String value = getSystemParamValueByCode("teach_course_reward_single_amount");
        if (!Utility.isNullorEmpty(value)) {
            temp = new BigDecimal(value);
        }
        return temp;
    }


    /**
     * 推荐老师奖励规则
     * @return
     */
    public String getInvitatitonTeachRewardRule() {
        String value = getSystemParamValueByCode("introduction_teach_reward_rule");
        if (!Utility.isNullorEmpty(value)) {
            return value;
        }
        return "";
    }

    /**
     * 推荐老师奖励规则
     * @return
     */
    public BigDecimal getInvitatitonTeachRewardAmount() {
        BigDecimal temp = BigDecimal.valueOf(0);
        String value = getSystemParamValueByCode("introduction_teach_reward_amount");
        if (!Utility.isNullorEmpty(value)) {
            temp = new BigDecimal(value);
        }
        return temp;
    }

    /**
     * 老师提现代理手续费率
     * @return
     */
    public BigDecimal getProxyReturnMoneyChargePercent() {
        BigDecimal temp = BigDecimal.valueOf(0.05);
        String value = getSystemParamValueByCode("proxy_return_money_charge_percent");
        if (!Utility.isNullorEmpty(value)) {
            temp = new BigDecimal(value);
        }
        return temp;
    }

    /**
     * 首次关注公众号提示内容
     * @return
     */
    public String getWechatFirstFollowSendConent() {
        String str = "1)欢迎酸枣的新同学！\n" +
                "2)酸枣在线，是一个直播学习平台。聚集各行业优质老师，分享行业知识和个人经验。老师 1 分钟开通自己的直播间，实现知识变现；学生即刻在线学习，丰富碎片时间。\n" +
                "3)我要开课：<a href='suanzao.llkeji.com/weixin/createSingleCourse'>1分钟开课点这里</a>\n" +
                "4)我要听课：<a href='suanzao.llkeji.com/weixin'>精彩课程点这里</a>";
        String value = getSystemParamValueByCode("wechat_first_follow_send_conent");
        if (!Utility.isNullorEmpty(value)) {
            str = value;
        }
        return str;
    }


}
