package com.longlian.live.service.impl;

import com.huaxin.util.*;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.LlAccountTrackDto;
import com.longlian.live.dao.LlAccountTrackMapper;
import com.longlian.live.service.LlAccountTrackService;
import com.longlian.type.LlAccountFromType;
import com.longlian.type.PayType;
import com.longlian.type.ReturnMessageType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by admin on 2017/4/28
 */
@Service("llAccountTrackService")
public class LlAccountTrackServiceImpl implements LlAccountTrackService {

    @Autowired
    LlAccountTrackMapper llAccountTrackMapper;
    @Value("${wechat.mch_id:}")
    private String wechatMch_id;    //微信H5
    @Value("${app.wechat.mch_id:}")
    private String appWechatMch_id; //微信
    @Value("${ios_pay_merId:}")
    private String iosPayMerId; //IOS内购

    /**
     * 收益记录
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> getProfit(long id) {
        List<Map> profit = llAccountTrackMapper.getProfit(id);
        if (profit != null && profit.size() > 0) {
            for (Map map : profit) {
                if (map.get("createTime") != null) {//格式化 日期
                    map.put("createTime", DateUtil.format((Date) map.get("createTime"), "yyyy-MM-dd HH:mm:ss"));
                }
                //不是一级，则把中间的字符替换了
                if (map.get("level") != null && !"1".equals(map.get("level").toString())) {
                    String mobile = map.get("mobile").toString();
                    if (mobile != null && !"".equals(mobile) && mobile.length() == 11) {
                        String substring1 = mobile.substring(0, 3);
                        String substring2 = mobile.substring(7, 11);
                        StringBuffer sb = new StringBuffer();
                        String sbStr = sb.append(substring1).append("****").append(substring2).toString();
                        map.put("mobile", sbStr);
                    } else {
                        map.put("mobile", "***********");
                    }
                }

            }

        }
        return profit;
    }

    @Override
    public List<Map> getWalletsPage(long id,Integer returnMoneyLevel, Integer pageSize, Integer offset) {

        if (pageSize == null || pageSize == 0) pageSize = 10;
        if (offset == null) offset = 0;
        DataGridPage dg = new DataGridPage();
        dg.setPageSize(pageSize);
        dg.setOffset(offset);

        List<Map> wallets = llAccountTrackMapper.getWalletsNewPage(id,returnMoneyLevel, dg);

        List<Map> llType = LlAccountFromType.getList();
        if (wallets != null && wallets.size() > 0) {
            for (Map map : wallets) {
                map.put("createTime", DateUtil.format((Date) map.get("createTime"), "yyyy-MM-dd HH:mm:ss"));
                for(Map type : llType){
                    if(type.get("value").toString().equals(map.get("status").toString())){
                        if(map.get("status").toString().equals("0")){
                            map.put("amount", "+" + map.get("amount"));
                            map.put("typeName", type.get("text"));
                        }else{
                            map.put("amount", "-" + map.get("amount"));
                            map.put("typeName", type.get("text"));
                        }
                    }
                }
            }
        }
        return wallets;
    }

    @Override
    public ActResultDto findMyCourseProfit(Long courseId, Integer pageSize, Integer offset, Long appId) {
        if(offset == null) offset = 0 ;
        if(pageSize == null) pageSize = 10 ;
        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(appId == null || appId <1){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        DataGridPage dg = new DataGridPage();
        dg.setPageSize(pageSize);
        dg.setOffset(offset);
        List<Map> list = llAccountTrackMapper.findMyCourseProfitPage(appId , courseId , dg);
        if(list.size()<1){
            resultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            resultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
            return resultDto;
        }
        for(Map map : list){
            map.put("createTime",map.get("createTime").toString().substring(0,map.get("createTime").toString().length()-2));
            if(map.get("amount").toString().equals(map.get("oAmount").toString())){
                map.put("type","正常购买课程");
            }else{
                map.put("type","分销购买课程");
            }
        }
        resultDto.setData(list);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    @Override
    public List<Map> relayCourseIncomeDetail(Long courseId, Integer pageSize, Integer offset, Long appId) {
        return this.relayCourseIncomeDetail(courseId, pageSize, offset, appId, false);
    }

    @Override
    public List<LlAccountTrackDto> getRechargePage(DataGridPage requestModel, LlAccountTrackDto llAccountTrackDto) {
        if(llAccountTrackDto.getEndTime()!=null){
            String end = DateUtil.format(llAccountTrackDto.getEndTime(), "yyyy-MM-dd");
            Date endTime = DateUtil.format(end + " 23:59:59");
            llAccountTrackDto.setEndTime(endTime);
        }
        if(llAccountTrackDto.getStartTime()!=null){
            String start = DateUtil.format(llAccountTrackDto.getStartTime(), "yyyy-MM-dd");
            Date startTime = DateUtil.format(start + " 00:00:00");
            llAccountTrackDto.setStartTime(startTime);
        }
        List<LlAccountTrackDto> list = llAccountTrackMapper.getRechargePage(requestModel, llAccountTrackDto);
        List<Map> mapList = PayType.getList();
        for(LlAccountTrackDto dto : list){
            for(Map map : mapList){
                if(map.get("value").toString().equals(dto.getBankType())){
                    dto.setBankType(map.get("text").toString());
                }
            }
        }
        return list;
    }

    @Override
    public String exportExcel(Map map, HttpServletRequest request) throws Exception {
        List list = countHeadReceipt();
        List list1 = columnHeaderReceipt(map);
        List<ExcelTop> result = new ArrayList<ExcelTop>();
        //封装列头
        for (int i = 0; i < list1.size(); i++) {
            ExcelTop excelTopss1 = new ExcelTop();
            excelTopss1.setRowIndex(i);
            excelTopss1.setRowNum(i);
            excelTopss1.setRowText(0);
            excelTopss1.setStart(0);
            excelTopss1.setEnd(9);
            excelTopss1.setSl(true);
            excelTopss1.setData(list1.get(i).toString());
            result.add(excelTopss1);
        }
        List<List<Object>> lists = numericalReceipt(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "电子回单 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }

    /**
     * 表头
     *
     * @return
     */
    private List countHeadReceipt() {
        List list = new ArrayList();
        list.add("用户手机");
        list.add("订单编号");
        list.add("第三方交易编号");
        list.add("支付类型");
        list.add("金额");
        list.add("日期");
        list.add("备注");
        return list;
    }

    /**
     * 条件
     * @param map
     * @return
     */
    private List columnHeaderReceipt(Map map) {
        List list = new ArrayList();
        if(map.get("bankType") == null){
            map.put("bankType", "");
        }
        list.add("电子回单 ");
        list.add("申请时间:" + map.get("startTime").toString() + " 至 " + map.get("endTime").toString());
        list.add("订单编号:" + map.get("orderNo").toString() + "    用户手机:" + map.get("appMobile").toString()
                + "    支付类型:" + getPayType(map.get("bankType").toString()));
        return list;
    }

    /**
     * 电子回单 充值 导出 内容
     * @param map
     * @return
     */
    private List<List<Object>> numericalReceipt(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        if(map.get("startTime")!=null){
            map.put("startTime", map.get("startTime")+ " 00:00:00");
        }
        if(map.get("endTime")!=null){
            map.put("endTime",map.get("endTime")+ " 23:59:59");
        }
        List<Map> list = llAccountTrackMapper.getRecharge(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            m.put("BANK_TYPE", getPayType(m.get("BANK_TYPE").toString()));
            data.add(m.get("MOBILE"));
            data.add(m.get("ORDER_NO"));
            data.add(m.get("TRAN_NO"));
            data.add(m.get("BANK_TYPE"));
            data.add(m.get("O_AMOUNT"));
            data.add(m.get("CREATE_TIME").toString().substring(0,m.get("CREATE_TIME").toString().length()-2));
            data.add(m.get("REMARK"));
            count.add(data);
        }
        return count;
    }

    /**
     * 获取支付类型
     * @param value
     * @return
     */
    public String getPayType(String value){
        if(StringUtils.isEmpty(value)){
            return "";
        }
        List<Map> payList = PayType.getList();
        for(Map pay : payList){
            if(pay.get("value").toString().equals(value)){
                return pay.get("text").toString();
            }
        }
        return "";
    }

    /**
     * 电子回单 - 充值 详情
     * @param id
     * @return
     */
    @Override
    public Map findOrderInfo(long id) {
        Map map = llAccountTrackMapper.findOrderInfo(id);
        map.put("capitalAmount",TransformationUtil.getInstance().format(map.get("oAmount").toString()));
        map.put("mchId","");
        List<Map> payList = PayType.getList();
        for(Map m : payList){
            if(map.get("bankType").toString().equals(PayType.weixin_h5.getValue())){
                map.put("mchId",wechatMch_id);
            }
            if(map.get("bankType").toString().equals(PayType.weixin.getValue())){
                map.put("mchId",appWechatMch_id);
            }
            if(map.get("bankType").toString().equals(PayType.ios.getValue())){
                map.put("mchId",iosPayMerId);
            }
            if(map.get("bankType").toString().equals(m.get("value").toString())){
                map.put("bankType",m.get("text").toString());
            }
        }
        return map;
    }

    @Override
    public List<Map> relayCourseIncomeStat(Long courseId, Long appId) {
        return this.relayCourseIncomeStat(courseId, appId, false);
    }

    @Override
    public List<Map> relayCourseIncomeStat(Long courseId, Long appId, boolean today) {
        return this.llAccountTrackMapper.relayCourseIncomeStat(courseId, appId, today);
    }

    @Override
    public List<Map> relayCourseIncomeDetail(Long courseId, Integer pageSize, Integer offset, Long appId, boolean today) {
        if(offset == null) offset = 0 ;
        if(pageSize == null) pageSize = 10 ;

        DataGridPage dg = new DataGridPage();
        dg.setPageSize(pageSize);
        dg.setOffset(offset);
        List<Map> list = llAccountTrackMapper.relayCourseIncomePage(appId, courseId, dg, today);
        if(list.size()<1){
            return null;
        }
        for(Map map : list){
            map.put("createTime",DateUtil.transFormationStringDate2(DateUtil.format((Date) map.get("CREATE_TIME"))));
//            map.put("createTime",map.get("CREATE_TIME").toString().substring(0,map.get("CREATE_TIME").toString().length()-2));
        }
//        resultDto.setData(list);
        return list;
    }

}
