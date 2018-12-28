package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.DateUtil;
import com.huaxin.util.ExcelTop;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.constant.Const;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.AccountTrackDto;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.dao.AccountTrackMapper;
import com.longlian.live.dao.UserRewardRecordMapper;
import com.longlian.live.service.AccountTrackService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.model.Account;
import com.longlian.model.AccountTrack;
import com.longlian.type.AccountFromType;
import com.longlian.type.PayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2016/8/18.WHA
 */
@Service("accountTrackService")
public class AccountTrackServiceImpl implements AccountTrackService {
    @Autowired
    private AccountTrackMapper accountTrackMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    UserRewardRecordMapper userRewardRecordMapper;

    @Autowired
    LiveRoomService liveRoomService;
    /**
     * 收益记录
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> getProfit(long id) {
        List<Map> profit = accountTrackMapper.getProfit(id);
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
    public List<Map> getWalletsPage(long id ,Integer pageSize , Integer offset) {
        if(pageSize == null || pageSize == 0) pageSize = 10;
        if(offset == null ) offset = 0;
        DataGridPage dg = new DataGridPage();
        dg.setPageSize(pageSize);
        dg.setOffset(offset);
        List<Map> wallets = accountTrackMapper.getWalletsPage(id, dg);
        if (wallets != null && wallets.size() > 0) {
            for(Map map : wallets){
                map.put("createTime",DateUtil.format((Date)map.get("createTime"),"yyyy-MM-dd HH:mm:ss"));
                //订单表
                if("0".equals(map.get("yy").toString())){
                    map.put("statusName","提现审核中");
                    map.put("typeName","提现");
                    map.put("amount","-"+map.get("amount"));
                }else if("1".equals(map.get("yy").toString())){
                //账户记录表
                    List<Map> mapList = AccountFromType.getList();
                    map.put("statusName","");
                    for(Map fromType:mapList){
                        if(fromType.get("value").toString().equals(map.get("status").toString())){
                            if("0".equals(map.get("status").toString())){
                                if("1".equals(map.get("type").toString())){
                                    if("1".equals(map.get("orderType").toString())){
                                        map.put("typeName","提现");
                                    }else{
                                        map.put("typeName","购买课时费");
                                    }
                                    map.put("amount","-"+map.get("amount"));
                                }else{
                                    map.put("typeName","课时费");
                                    map.put("amount","+"+map.get("amount"));
                                }
                            }else if("12".equals(map.get("status").toString())){
                                map.put("amount","-"+map.get("amount"));
                                map.put("typeName",fromType.get("text"));
                            }else{
                                map.put("amount","+"+map.get("amount"));
                                map.put("typeName",fromType.get("text"));
                            }
                            continue;
                        }
                    }
                }
            }
        }
        return wallets;
    }

    @Override

    public List<Map> getWalletsPageNew(long id,Integer returnMoneyLevel ,Integer pageSize , Integer offset) {

        if(pageSize == null || pageSize == 0) pageSize = 10;
        if(offset == null ) offset = 0;
        DataGridPage dg = new DataGridPage();
        dg.setPageSize(pageSize);
        dg.setOffset(offset);

        List<Map> wallets = accountTrackMapper.getWalletsPageNew(id,returnMoneyLevel, dg);

        if (wallets != null && wallets.size() > 0) {
            for(Map map : wallets){
                map.put("createTime",DateUtil.format((Date)map.get("createTime"),"yyyy-MM-dd HH:mm:ss"));
                //账户记录表
                List<Map> mapList = AccountFromType.getList();
                map.put("statusName","");
                for(Map fromType:mapList){
                    if(fromType.get("value").toString().equals(map.get("status").toString())){
                        if("0".equals(map.get("status").toString())){
                            map.put("typeName","课时费");
                            map.put("amount","+"+map.get("amount"));
                        }else{
                            map.put("amount","+"+map.get("amount"));
                            map.put("typeName",fromType.get("text"));
                        }
                        continue;
                    }
                }
            }
        }
        return wallets;
    }

    /**
     * 分销达人  排行 前5
     *
     * @param courseId
     * @return
     */
    @Override
    public List<Map> getDistributionSortByCourseId(long courseId) {
        return accountTrackMapper.getDistributionSortByCourseId(courseId);
    }

    @Override
    public List<Map> getDistributionMasterList(Long courseId, Integer pageNum, Integer pageSize) {
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setOffset(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        return accountTrackMapper.getDistributionMasterListPage(dg, courseId);
    }

    @Override
    public Map findAllWallet(long appId) {
        Map map = new HashMap();
        List<Map> map2 = accountTrackMapper.findAllWallet(appId);
        BigDecimal livAmount = new BigDecimal(0);
        BigDecimal disAmount = new BigDecimal(0);
        BigDecimal plaAmount = new BigDecimal(0);
        BigDecimal proAmount = new BigDecimal(0);
        for (Map map1 : map2) {
            if ("0".equals(String.valueOf(map1.get("level")))) {
                livAmount = livAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if ("1".equals(String.valueOf(map1.get("level")))) {
                disAmount = disAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if ("2".equals(String.valueOf(map1.get("level"))) || "3".equals(String.valueOf(map1.get("level")))
                    || "4".equals(String.valueOf(map1.get("level")))) {
                plaAmount = plaAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if("6".equals(String.valueOf(map1.get("level")))){
                proAmount = proAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
        }
        Account account  = accountMapper.getAccountByAppId(appId);
        BigDecimal rewAmount = userRewardRecordMapper.findSumAccount(appId);
        map.put("balance", account.getBalance());
        map.put("isHaveTradePwd", account.getTradePwd() == null ? "0": "1"); // 0: 没有交易密码 1:有
        map.put("livAmount", livAmount);
        map.put("disAmount", disAmount);
        map.put("plaAmount", plaAmount);
        map.put("proAmount", proAmount);
        map.put("rewAmount", rewAmount);
        map.put("totalAmount",livAmount.add(disAmount).add(plaAmount).add(rewAmount).add(proAmount));
        return map;
    }

    /**
     * 用户今日收益
     * @param appId
     * @return
     */
    public Map findTodayWallet(long appId){
        Map map = new HashMap<>();
        List<Map> list = accountTrackMapper.findAllWallet(appId) ;
        BigDecimal livAmount = new BigDecimal(0);
        BigDecimal disAmount = new BigDecimal(0);
        BigDecimal plaAmount = new BigDecimal(0);
        BigDecimal proAmount = new BigDecimal(0);
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal hisAccountMoney = accountTrackMapper.findAccountMoney(appId);
        for(Map map1 : list){
            if ("0".equals(String.valueOf(map1.get("level")))) { //默认
                livAmount = livAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if ("1".equals(String.valueOf(map1.get("level")))) { //分销奖励
                disAmount = disAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if ("2".equals(String.valueOf(map1.get("level"))) || "3".equals(String.valueOf(map1.get("level")))
                    || "4".equals(String.valueOf(map1.get("level")))) { //2-推荐老师平台奖励 3-老师课程授课奖励 4-老师粉丝关注奖励
                plaAmount = plaAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if("6".equals(String.valueOf(map1.get("level")))){  //老师提现给代理返钱
                proAmount = proAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
        }
        Account account  = accountMapper.getAccountByAppId(appId);
        BigDecimal rewAmount = userRewardRecordMapper.findSumAccount(appId);
        BigDecimal todayAmount = accountTrackMapper.findTodayAccount(appId);
        map.put("balance", account.getBalance());
        map.put("isHaveTradePwd", account.getTradePwd() == null ? "0": "1"); // 0: 没有交易密码 1:有
        map.put("livAmount", livAmount);
        map.put("disAmount", disAmount);
        map.put("plaAmount", plaAmount);
        map.put("proAmount", proAmount);
        map.put("rewAmount", rewAmount);
        map.put("todayAmount", todayAmount);
        totalAmount = livAmount.add(disAmount).add(plaAmount).add(rewAmount).add(proAmount);
        //处理账户中不能提现得钱
        if(totalAmount.subtract(hisAccountMoney).compareTo(new BigDecimal(0))<0){
            map.put("totalAmount",0);
        }else{
            map.put("totalAmount",totalAmount.subtract(hisAccountMoney));
        }
        return map;
    }


    public Map findTodayWalletNew(long appId){
        Map map = new HashMap<>();
        List<Map> list = accountTrackMapper.findAllWallet(appId) ;
        BigDecimal livAmount = new BigDecimal(0.00);
        BigDecimal disAmount = new BigDecimal(0.00);
        BigDecimal plaAmount = new BigDecimal(0.00);
        BigDecimal proAmount = new BigDecimal(0.00);
        BigDecimal totalAmount = new BigDecimal(0.00);
        BigDecimal hisAccountMoney = accountTrackMapper.findAccountMoney(appId);
        for(Map map1 : list){
            if ("0".equals(String.valueOf(map1.get("level")))) { //默认
                livAmount = livAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if ("1".equals(String.valueOf(map1.get("level")))) { //分销奖励
                disAmount = disAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if ("2".equals(String.valueOf(map1.get("level"))) || "3".equals(String.valueOf(map1.get("level")))
                    || "4".equals(String.valueOf(map1.get("level")))) { //2-推荐老师平台奖励 3-老师课程授课奖励 4-老师粉丝关注奖励
                plaAmount = plaAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
            if("6".equals(String.valueOf(map1.get("level")))){  //老师提现给代理返钱
                proAmount = proAmount.add(new BigDecimal(String.valueOf(map1.get("amount"))));
            }
        }
        BigDecimal relayAmount = liveRoomService.getRelayCourseIncomeTotal(appId);
        relayAmount = relayAmount != null ? relayAmount : new BigDecimal(0.00);
        Account account  = accountMapper.getAccountByAppId(appId);
        BigDecimal rewAmount = userRewardRecordMapper.findSumAccount(appId);
        BigDecimal todayAmount = accountTrackMapper.findTodayAccount(appId);
        map.put("balance", account.getBalance());
        map.put("isHaveTradePwd", account.getTradePwd() == null ? "0": "1"); // 0: 没有交易密码 1:有
        map.put("livAmount", livAmount);
        map.put("disAmount", disAmount);
        map.put("plaAmount", plaAmount);
        map.put("proAmount", proAmount);
        map.put("rewAmount", rewAmount);
        map.put("relayAmount", relayAmount);

//        BigDecimal relayTodayAmountL = liveRoomService.getRelayCourseIncomeTotal(appId, true);
//        BigDecimal relayTodayAmount = relayTodayAmountL != null ? relayTodayAmountL : new BigDecimal(0);
//        map.put("todayAmount", todayAmount.add(relayTodayAmount));
        map.put("todayAmount", todayAmount);
        totalAmount = livAmount.add(disAmount).add(plaAmount).add(rewAmount).add(proAmount).add(relayAmount);
        //处理账户中不能提现得钱
        if(totalAmount.subtract(hisAccountMoney).compareTo(new BigDecimal(0))<0){
            map.put("totalAmount","0.00");
        }else{
            map.put("totalAmount",totalAmount.subtract(hisAccountMoney));
        }
        return map;
    }

    @Override
    public List findTodayWalletByType(Integer type,Integer pageNum,Integer pageSize) {
        DataGridPage page = new DataGridPage();
        page.setCurrentPage(pageNum);
        page.setPageSize(pageSize);
        List list = accountTrackMapper.findTodayWalletByTypePage(type,page);
        return list;
    }

    /**
     * 用户收支汇总
     *
     * @param accountTrackDto
     * @return
     */
    @Override
    public List<AccountTrackDto> getIncomeSummaryPage(DataGridPage page, AccountTrackDto accountTrackDto) {
        String end = DateUtil.format(accountTrackDto.getEndTime(), "yyyy-MM-dd");
        String start= DateUtil.format(accountTrackDto.getStartTime(), "yyyy-MM-dd");
        Date endTime = DateUtil.format(end + " 23:59:59");
        Date startTime=DateUtil.format(start+" 00:00:00");
        accountTrackDto.setEndTimeStr(end + " 23:59:59");
        accountTrackDto.setStartTimeStr(start + " 00:00:00");
        List<AccountTrackDto> trackList = new ArrayList<AccountTrackDto>();
        if(page == null){
            trackList = accountTrackMapper.getIncomeSummary(accountTrackDto);
        }else{

            trackList = accountTrackMapper.getIncomeSummaryPage(page, accountTrackDto);
        }
        page.setTotal(Integer.parseInt(accountTrackMapper.getIncomeSummaryTotalCount(accountTrackDto).get("totalCount")+""));
        List<Map> mapList = accountTrackMapper.findAllBanlanceByTime(endTime);
        for (Map map : mapList) {    //map 中 当前时间所有的 app 用户基本信息；
            for (AccountTrackDto dto : trackList) {
                if ("1".equals(accountTrackDto.getIsShow())) {
                    dto.setExpenditure(new BigDecimal(0));
                }
                if (String.valueOf(map.get("appId")).equals(String.valueOf(dto.getToAccountId()))) {
                    if (!"1".equals(dto.getIsExt())) {   //没有设置过在走下面的方法
                        if (StringUtils.isEmpty(String.valueOf(map.get("currBalance"))))
                            dto.setAmount(new BigDecimal(0));
                        dto.setAmount(new BigDecimal(String.valueOf(map.get("currBalance"))));
                        dto.setIsExt("1");  //已设置
                        dto.setTime(DateUtil.format(accountTrackDto.getStartTime(), "yyyy-MM-dd") + " -- " + end);
                        if (dto.getExpenditure() == null) dto.setExpenditure(new BigDecimal(0));
                    }
                    break;     //满足条件 中断内层循环  继续外层循环
                }
            }

        }
        return trackList;
    }

    /**
     * 资金池 收支记录
     *
     * @return
     */
    @Override
    public List<Map> getTrackAndOrders(DataGridPage page, AccountTrackDto trackDto) {
        String end = "";
        if(null == trackDto.getEndTime()){
            trackDto.setEndTime(new Date());
        }
        if(null == trackDto.getStartTime()){
            trackDto.setStartTime(DateUtil.format(DateUtil.format(new Date(), "yyyy-MM-dd") + " 00:00:00"));
        }
        end = DateUtil.format(trackDto.getEndTime(), "yyyy-MM-dd");
        Date endTime = DateUtil.format(end + " 23:59:59");
        trackDto.setEndTime(endTime);
        List<Map> ops = accountTrackMapper.getTrackAndOrdersPage(page,
                Const.longlian_live_zi_jin_chi_account_id, trackDto);

        return newListTrackAndOrders(ops);
    }

    private List<Map> newListTrackAndOrders(List<Map> ops) {
        if (ops != null && ops.size() > 0) {
            for (Map m : ops) {
                if (m.get("orderType") != null) {
                    if ("0".equals(m.get("orderType").toString())) {
                        m.put("orderType", "课时");
                        if (m.get("oamount") != null) {
                            m.put("oamount", "+" + m.get("oamount"));
                        }
                        if (m.get("tamount") != null) {
                            boolean tamount = m.get("tamount").toString().contains("-");
                            if (tamount) {
                                m.put("tamount", m.get("tamount"));
                            } else {
                                m.put("tamount", "+" + m.get("tamount"));
                            }

                        }
                    } else if ("1".equals(m.get("orderType").toString()) && m.get("returnMoneyLevel") != null && !"10".equals(m.get("returnMoneyLevel").toString())) {
                        if (m.get("optStatus") != null && !"3".equals(m.get("optStatus").toString())) {
                            m.put("orderType", "提现");
                        } else if (m.get("optStatus") != null && "3".equals(m.get("optStatus").toString())) {
                            m.put("orderType", "提现(已退款)");

                        }
                        if (m.get("oamount") != null) {
                            m.put("oamount", "-" + m.get("oamount"));
                        }
                        if (m.get("tamount") != null) {
                            m.put("tamount", "-" + m.get("tamount"));
                        }
                    } else if ("1".equals(m.get("orderType").toString()) && m.get("returnMoneyLevel") != null && "10".equals(m.get("returnMoneyLevel").toString())) {
                        m.put("orderType", "提现失败退款");
                        if (m.get("oamount") != null) {
                            m.put("oamount", "+" + m.get("oamount"));
                        }
                        if (m.get("tamount") != null) {
                            boolean tamount = m.get("tamount").toString().contains("-");
                            if (tamount) {
                                m.put("tamount", m.get("tamount"));
                            } else {
                                m.put("tamount", "+" + m.get("tamount"));
                            }
                        }
                    } else if ("2".equals(m.get("orderType").toString())) {
                        m.put("orderType", "钱包充值");
                    }
                }
                if (m.get("bankType") != null) {
                    m.put("bankType", PayType.getNameByValue(m.get("bankType").toString()));
                }
            }
        }
        return ops;
    }



    /**
     * 计算 资金池 收支记录 手续费
     *
     * @return
     */
    @Override
    public Map getCountAccount(String orderNo, String orderType, String bankType, Date startDate,
                               Date endDate, String mobile) {
        String end = DateUtil.format(endDate, "yyyy-MM-dd");
        Date endTime = DateUtil.format(end + " 23:59:59");
        Map map = new HashMap();
        long id = Const.ll360_zi_jin_chi_account_id;
        BigDecimal countOamount = new BigDecimal(0);
        //查询本年收入  本月收入   本周收入   本日收入
        String year=DateUtil.format(new Date(),"yyyy"); //获取当前年份
        String month=DateUtil.format(new Date(),"yyyy-MM");
        String day=DateUtil.format(new Date(),"yyyy-MM-dd");
        Map requestDateMap = DateUtil.getCalendarWeek();
        requestDateMap.put("year",year); requestDateMap.put("month",month);
        requestDateMap.put("day",day);requestDateMap.put("id",id);
        Map dateMap = accountTrackMapper.getYearMonthWeekDateOamount(requestDateMap);
        //处理数据
        map.put("yearOamount",(dateMap.get("yearOamount")!=null?dateMap.get("yearOamount"):new BigDecimal(0)) ); //年收入
        map.put("monthOamount",(dateMap.get("monthOamount")!=null?dateMap.get("monthOamount"):new BigDecimal(0)) ); //年收入
        map.put("weekOamount",(dateMap.get("weekOamount")!=null?dateMap.get("weekOamount"):new BigDecimal(0)) ); //年收入
        map.put("dayOamount",(dateMap.get("dayOamount")!=null?dateMap.get("dayOamount"):new BigDecimal(0)) ); //年收入
        //总收入
        if(!"1".equals(orderType) || "".equals(orderType)){
            countOamount = accountTrackMapper.getCountOamount(id, orderNo, orderType, bankType, startDate, endTime, mobile);
        }
        //银行手续费
        BigDecimal countCharge = accountTrackMapper.getCountCharge(id, orderNo, orderType, bankType, startDate, endTime, mobile);
        //用户手续费
        BigDecimal countllCharge = accountTrackMapper.getCountllCharge(id, orderNo, orderType, bankType, startDate, endTime, mobile);
        map.put("countOamount", (countOamount != null ? countOamount : new BigDecimal(0)).add(countllCharge != null ? countllCharge : new BigDecimal(0)));
        //总支出
        BigDecimal countTamount = new BigDecimal(0);
        if("".equals(orderType)||"1".equals(orderType)){
            countTamount = accountTrackMapper.getCountTamount(id, orderNo, orderType, bankType, startDate, endTime, mobile);
        }
        map.put("countTamount", (countTamount != null ? countTamount : new BigDecimal(0)).add(
                countCharge != null ? countCharge : new BigDecimal(0)).subtract(
                countllCharge != null ? countllCharge : new BigDecimal(0)
        ));
        map.put("countllCharge", countllCharge != null ? countllCharge : 0.00);
        map.put("countCharge", countCharge != null ? countCharge : 0.00);
        return map;
    }

    @Override
    public ExportExcelWhaUtil importExcel(HttpServletRequest req, HttpServletResponse response, String orderNo, String orderType, String bankType, Date startDate, Date endDate, String mobile) {
        String end = DateUtil.format(endDate, "yyyy-MM-dd");
        endDate = DateUtil.format(end + " 23:59:59");
        List<Map> ops = accountTrackMapper.getTrackAndOrders(
                Const.longlian_live_zi_jin_chi_account_id, orderNo, orderType, bankType,
                startDate, endDate, mobile);
        List<Map> maps = newListTrackAndOrders(ops);
        Map countAccount = this.getCountAccount(orderNo, orderType, bankType,
                startDate, endDate, mobile);
        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "资金统计";
        listStr.add(top1);
        String top2 = "总收入:," + countAccount.get("countOamount") + "元,总支出:,"
                + countAccount.get("countTamount") + "元,用户手续费:,"
                + "0.00 元,银行手续费:,"
                + "0.00 元";
        listStr.add(top2);
        String top3 = "序号,订单编号,手机号,用户名称,业务类型,支付方式,收入/支出,实际收入/支出,用户手续费,银行手续费,账户余额,交易日期,备注";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "orderNo,mobile,uname,orderType,bankType,oamount,tamount,llCharge,charge,currBalance,createTime,remark";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "资金统计", req, exceltitel);
        return excel;
    }

    public List<ExcelTop> getExceltitel(List<String> strs) {
        //++++++++++++++++++++++++++++++++++++++++表头合并拼接++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        List<ExcelTop> ets = new ArrayList<ExcelTop>();//行集合
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                List<String> titleList = ExportExcelWhaUtil.getTitleList(strs.get(i));
                ExcelTop et = new ExcelTop();//每行第一个位置
                et.setRowIndex(i);
                et.setRowText(0);
                et.setSl(false);
                et.setData(titleList.get(0));
                List<ExcelTop> etscell = new ArrayList<ExcelTop>();//每行内的 每列集合
                if (titleList != null && titleList.size() > 1) {
                    for (int j = 1; j < titleList.size(); j++) {
                        ExcelTop etj = new ExcelTop();
                        etj.setRowIndex(i);
                        etj.setRowText(j);
                        etj.setSl(false);
                        etj.setData(titleList.get(j));
                        etscell.add(etj);
                    }
                }
                et.setEts(etscell);
                ets.add(et);
            }
        }
        return ets;
    }

    /**
     * 收支汇总详情
     *
     * @param startTime
     * @param endTime
     * @param appId
     * @return
     */
    @Override
    public List<AccountTrackDto> getDetailedPage(DataGridPage page, String startTime, String endTime, Long appId) {
        Date start = DateUtil.format(startTime + " 00:00:00");
        Date end = DateUtil.format(endTime + " 23:59:59");
        List<AccountTrackDto> trackDtoList = accountTrackMapper.getDetailedPage(page, start, end, appId);
        for (AccountTrackDto trackDto : trackDtoList) {
            List<Map> list = AccountFromType.getList();
            for (Map map : list) {
                if (String.valueOf(map.get("value")).equals(String.valueOf(trackDto.getReturnMoneyLevel()))) {
                    trackDto.setRemark(map.get("text") + "");
                    break;
                }
            }
            if ("0".equals(trackDto.getType())) {
                if ("0".equals(String.valueOf(trackDto.getReturnMoneyLevel()))) {
                    trackDto.setRemark("直播间收益");
                }
                trackDto.setIncome(trackDto.getAmount());
                trackDto.setExpenditure(new BigDecimal(0));
            }
            if ("1".equals(trackDto.getType())) {
                if ("0".equals(String.valueOf(trackDto.getReturnMoneyLevel()))) {
                    trackDto.setRemark("提现");
                }
                trackDto.setIncome(new BigDecimal(0));
                trackDto.setExpenditure(trackDto.getAmount());
            }
        }
        return trackDtoList;
    }

    public DatagridResponseModel getAppUserAccountsPage(DatagridRequestModel requestModel,
                                                        String name, String mobile, Long appId) {
        DatagridResponseModel drm = new DatagridResponseModel();
        List<Map> appUserAccountsPage = accountMapper.getAppUserAccountsPage(requestModel, name, mobile, appId);
        if (appUserAccountsPage != null && appUserAccountsPage.size() > 0) {
            for (Map map : appUserAccountsPage) {
                if (map.get("balance") != null && map.get("id") != null) {
                    BigDecimal bg = new BigDecimal(map.get("balance").toString());
                    BigDecimal bg2 = accountMapper.getSumAmount(Long.parseLong(map.get("id").toString()));
                    if (bg2 == null) {
                        bg2 = new BigDecimal(0);
                    }
                    BigDecimal subtract = bg.subtract(bg2);
                    map.put("balance", subtract);
                }
            }
        }
        drm.setRows(appUserAccountsPage);
        drm.setTotal(requestModel.getTotal());
        return drm;
    }

    @Override
    public ExportExcelWhaUtil importExcelUserAccounts(HttpServletRequest req, String name,
                                                      String mobile, Long appId) {
        List<Map> ops = accountMapper.getAppUserAccounts(name, mobile, appId);
        //++++++++++++++++++++++++拼接表头数据++++++++++++++++++++++++++++++++++++
        List<String> listStr = new ArrayList<String>();
        String top1 = "会员钱包";
        listStr.add(top1);
        String top3 = "序号,用户id,姓名,手机号,钱包余额";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "id,name,mobile,balance";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(ops, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "会员钱包", req, exceltitel);
        return excel;
    }


    public List<Map> getBankType() {
        List<Map> list = new ArrayList<Map>();
        PayType[] payTypes = PayType.values();
        for (PayType payType : payTypes) {
            Map map = new HashMap();
            map.put("id", payType.getValue());
            map.put("name", payType.getText());
            list.add(map);
        }
        return list;
    }

    @Override
    public List<AccountTrack> getListByOrderId(long orderId) {
        return accountTrackMapper.getListByOrderId(orderId);
    }

    /**
     * 导出用户收支汇总明细
     * @param request
     * @param toAccountId
     * @param mobile
     * @param startTime
     * @param endTime
     * @param returnMoneyLevel
     * @param isShow
     * @return
     */
    @Override
    public ExportExcelWhaUtil importExcelBalanceOfPayments(HttpServletRequest request, String toAccountId, String mobile,
                                                           String startTime, String endTime,
                                                           String returnMoneyLevel, String isShow) {
        Date start = DateUtil.format(startTime + " 00:00:00");
        Date end = DateUtil.format(endTime + " 23:59:59");
        AccountTrackDto accountTrackDto = new AccountTrackDto();
        accountTrackDto.setEndTime(end);
        accountTrackDto.setStartTime(start);
        accountTrackDto.setReturnMoneyLevel(Integer.valueOf(returnMoneyLevel));
        accountTrackDto.setIsShow(isShow);
        accountTrackDto.setMobile(mobile);
        if(StringUtils.isEmpty(toAccountId)) toAccountId = "0";
        accountTrackDto.setToAccountId(Long.valueOf(toAccountId));
        List<Map> trackList = accountTrackMapper.importExcelBalanceOfPayments(accountTrackDto);
        List<Map> mapList = accountTrackMapper.findAllBanlanceByTime(end);
        for (Map map : mapList) {
            for (Map dto : trackList) {
                if ("1".equals(String.valueOf(dto.get("isShow")))) {
                    dto.put("expenditure",0);
                }
                if (String.valueOf(map.get("appId")).equals(String.valueOf(dto.get("toAccountId")))) {
                    if (!"1".equals(String.valueOf(dto.get("isExt")))) {   //没有设置过在走下面的方法
                        if (StringUtils.isEmpty(String.valueOf(map.get("currBalance")))) dto.put("amount", 0);
                        dto.put("amount", map.get("currBalance"));
                        dto.put("isExt",1);//已设置
                        dto.put("time", DateUtil.format(accountTrackDto.getStartTime(), "yyyy-MM-dd") + " -- " + end);
                        if (dto.get("expenditure") == null) dto.put("expenditure",0);
                    }
                }
            }
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "用户收支汇总明细";
        listStr.add(top1);
        String top3 = "序号,交易日期,户名,用户ID,手机号,收入金额,提现金额,余额";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "time,userName,toAccountId,mobile,income,expenditure,amount";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(trackList, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "用户收支汇总明细", request, exceltitel);
        return excel;
    }

    /**
     * 导出收支明细
     * @param request
     * @param appId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public ExportExcelWhaUtil importDetails(HttpServletRequest request, String appId, String startTime, String endTime) {
        Date start = DateUtil.format(startTime + " 00:00:00");
        Date end = DateUtil.format(endTime + " 23:59:59");
        List<Map> trackDtoList = accountTrackMapper.importDetails(start, end, Long.valueOf(appId));
        for (Map trackDto : trackDtoList) {
            List<Map> list = AccountFromType.getList();
            for (Map map : list) {
                if (String.valueOf(map.get("value")).equals(String.valueOf(trackDto.get("returnMoneyLevel")))) {
                    trackDto.put("remark",map.get("text"));
                    break;
                }
            }
            if ("0".equals(String.valueOf(trackDto.get("type")))) {
                if ("0".equals(String.valueOf(trackDto.get("returnMoneyLevel")))) {
                    trackDto.put("remark","直播间收益");
                }
                trackDto.put("income",trackDto.get("amount"));
                trackDto.put("expenditure", 0);
            }
            if ("1".equals(String.valueOf(trackDto.get("type")))) {
                if ("0".equals(String.valueOf(trackDto.get("returnMoneyLevel")))) {
                    trackDto.put("remark","提现");
                }
                trackDto.put("income", 0);
                trackDto.put("expenditure", trackDto.get("amount"));
            }
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "用户收支汇总详情";
        listStr.add(top1);
        String top3 = "序号,交易日期,交易订单号,用户名,手机号,收入类型,收入金额,提现金额,余额";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "createTime,orderNo,userName,mobile,remark,income,expenditure,currBalance";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(trackDtoList, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "用户收支汇总详情", request, exceltitel);
        return excel;
    }

    /**
     * 收支明细
     * @param requestModel
     * @param accountTrackDto
     * @return
     */
    @Override
    public List<AccountTrackDto> getReDetailPage(DataGridPage requestModel, AccountTrackDto accountTrackDto , String type) {
        String end = DateUtil.format(accountTrackDto.getEndTime(), "yyyy-MM-dd");
        Date endTime = DateUtil.format(end + " 23:59:59");
        accountTrackDto.setEndTime(endTime);
        List<AccountTrackDto> trackDtoList = new ArrayList<AccountTrackDto>();
        List<AccountTrackDto> accountTrackDtoList = getIncomeSummaryPage(null,accountTrackDto);
        StringBuffer stringBuffer = new StringBuffer();
        for(AccountTrack track : accountTrackDtoList){
            stringBuffer.append(track.getToAccountId());
            stringBuffer.append(",");
        }
        stringBuffer.append(0);
        if("page".equals(type)){
            //分页
            trackDtoList = accountTrackMapper.getReDetailPage(requestModel,stringBuffer.toString(),accountTrackDto);
        }else{
            trackDtoList = accountTrackMapper.getImportReDetail(stringBuffer.toString(),accountTrackDto);
        }
        for (AccountTrackDto trackDto : trackDtoList) {
            List<Map> list = AccountFromType.getList();
            for (Map map : list) {
                if (String.valueOf(map.get("value")).equals(String.valueOf(trackDto.getReturnMoneyLevel()))) {
                    trackDto.setRemark(map.get("text") + "");
                    break;
                }
            }
            if ("0".equals(trackDto.getType())) {
                if ("0".equals(String.valueOf(trackDto.getReturnMoneyLevel()))) {
                    trackDto.setRemark("直播间收益");
                }
                trackDto.setIncome(trackDto.getAmount());
                trackDto.setExpenditure(new BigDecimal(0));
            }
            if ("1".equals(trackDto.getType())) {
                if ("0".equals(String.valueOf(trackDto.getReturnMoneyLevel()))) {
                    trackDto.setRemark("提现");
                }
                trackDto.setIncome(new BigDecimal(0));
                trackDto.setExpenditure(trackDto.getAmount());
            }
        }
        return trackDtoList;
    }

    @Override
    public ExportExcelWhaUtil importExcelReDetail(HttpServletRequest request, String toAccountId, String mobile, String startTime, String endTime, String returnMoneyLevel, String isShow) {
        Date start = DateUtil.format(startTime + " 00:00:00");
        Date end = DateUtil.format(endTime + " 23:59:59");
        AccountTrackDto accountTrackDto = new AccountTrackDto();
        accountTrackDto.setEndTime(end);
        accountTrackDto.setStartTime(start);
        accountTrackDto.setReturnMoneyLevel(Integer.valueOf(returnMoneyLevel));
        accountTrackDto.setIsShow(isShow);
        accountTrackDto.setMobile(mobile);
        if(StringUtils.isEmpty(toAccountId)) toAccountId = "0";
        accountTrackDto.setToAccountId(Long.valueOf(toAccountId));
        List<AccountTrackDto> trackDtoList = getReDetailPage(null,accountTrackDto,null);
        List<Map> trackMap = new ArrayList<Map>();
        for(AccountTrackDto trackDto : trackDtoList){
            Map map  = new HashMap();
            map.put("createTime",DateUtil.format(trackDto.getCreateTime()));
            map.put("orderNo",trackDto.getOrderNo());
            map.put("userName",trackDto.getUserName());
            map.put("mobile",trackDto.getMobile());
            map.put("remark",trackDto.getRemark());
            map.put("income",trackDto.getIncome());
            map.put("expenditure",trackDto.getExpenditure());
            map.put("currBalance",trackDto.getCurrBalance());
            trackMap.add(map);
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "收支详情";
        listStr.add(top1);
        String top3 = "序号,交易日期,交易订单号,用户名,手机号,收入类型,收入金额,提现金额,余额";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "createTime,orderNo,userName,mobile,remark,income,expenditure,currBalance";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(trackMap, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "收支明细", request, exceltitel);
        return excel;
    }

    @Override
    public List<Map> getproxyIncomePage(Integer offset, Integer pageSize, Long appId) {
        DataGridPage page = new DataGridPage();
        page.setOffset(offset);
        page.setPageSize(pageSize);
        List<Map> mapList = accountTrackMapper.getproxyIncomePage(page, appId);
        for(Map map : mapList){
            if(map.get("createTime")!=null){
                map.put("createTime",map.get("createTime").toString().substring(0,map.get("createTime").toString().length()-2));
            }
        }
        return mapList;
    }

}
