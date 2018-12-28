package com.longlian.live.service.impl;

import com.huaxin.util.DataGridPage;
import com.huaxin.util.ExcelTop;
import com.huaxin.util.ExportExcelWhaUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.RewardRecordMapper;
import com.longlian.live.service.RewardRecordService;
import com.longlian.model.Course;
import com.longlian.model.RewardRecord;
import com.longlian.type.LongLianRewardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/2/23.
 */
@Service("rewardRecordService")
public class RewardRecordServiceImpl implements RewardRecordService {
    @Autowired
    RewardRecordMapper rewardRecordMapper;

    @Autowired
    RedisUtil redisUtil;
    @Override
    public void insert(RewardRecord rewardRecord) {
        rewardRecord.setCreateTime(new Date());
        if(rewardRecord.getLlAddMenCount() == null){
            rewardRecord.setLlAddMenCount(0);
        }
        rewardRecordMapper.insert(rewardRecord);
    }

    /**
     *   <!--推荐老师 成功过的老师记录-->
     * @return 以逗号分隔的字符串
     */
    @Override
    public String getRewardSuccessInvitationTeach() {
        String temp = "";
        if(!redisUtil.exists(RedisKey.ll_live_reward_success_invitation_teach_record)){
            temp = reSetRedisAndReturnDate();
            return  temp;
        }
       //从redis取
        Set<String> set = redisUtil.smembers(RedisKey.ll_live_reward_success_invitation_teach_record);
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : set) {
            stringBuffer.append(str + ",");
        }
        if(stringBuffer.length() > 0)  temp = stringBuffer.substring( 0 ,stringBuffer.length() -1 );
        return temp;
    }

    public  String  reSetRedisAndReturnDate(){
        String temp = "";
        StringBuffer stringBuffer = new StringBuffer();
        List<Map> list = getRewardSuccessInvitationTeachList() ;
        for(Map map :list){
            String appId = map.get("relationAppId").toString();
            stringBuffer.append(appId + ",");
            redisUtil.sadd(RedisKey.ll_live_reward_success_invitation_teach_record, appId);
        }
        if(stringBuffer.length() > 0)  temp = stringBuffer.substring( 0 ,stringBuffer.length() -1 );
        return  temp;
    }
    /**
     *  <!--推荐老师 成功过的老师记录-->
     * @return
     */
    public List<Map> getRewardSuccessInvitationTeachList() {
        return rewardRecordMapper.getRewardSuccessInvitationTeach();
    }




    /**
     * 初始化 老师粉丝关注奖励 记录存入redis中
     */
    @Override
    public  void  getFollowRewardReSetRedisInit(){
        List<Map> list = getFollowRewardTeach() ;
        for(Map map :list){
            String followRewardId = map.get("followRewardId").toString();
            String appId = map.get("appId").toString();
            redisUtil.sadd(RedisKey.ll_live_follow_reward_record_ + appId, followRewardId);
        }
    }
    /**
     *    <!--老师粉丝关注奖励 成功过的老师记录-->
     * @return
     */
    public List<Map> getFollowRewardTeach() {
        return rewardRecordMapper.getFollowRewardTeach();
    }
    /**
     * 根据老师ID 获取粉丝关注奖励过的记录
     * @param appId
     * @return
     */
    @Override
    public Set getFollowRewardByTeachAppId(long appId) {
        Set<String> set = redisUtil.smembers(RedisKey.ll_live_follow_reward_record_ + appId);
        return set;
    }

    @Override
    public List<Map> getplatIncomePage(Integer offset, Integer pageSize, long appId) {
        DataGridPage dg = new DataGridPage();
        dg.setOffset(offset);
        dg.setPageSize(pageSize);
        List<Map> list = rewardRecordMapper.getplatIncomePage(dg, appId);
        List<Map> mapList = LongLianRewardType.getList();
        for(Map map:list){
            for(Map map1 : mapList){
                if(String.valueOf(map.get("type")).equals(String.valueOf(map1.get("value")))){
                    map.put("type",map1.get("text"));
                }
            }
        }
        return list;
    }

    public List<Map> getRecommendTeacherList(DatagridRequestModel page, Map map) {
        List<Map> list = rewardRecordMapper.getRecommendTeacherListPage(page, map);
        return list;
    }

    @Override
    public BigDecimal getRecommendTeacherAccount(Map map) {
        return rewardRecordMapper.getRecommendTeacherAccount(map);
    }

    @Override
    public String exportExcelRecommendTeacher(Map map, HttpServletRequest request) throws Exception {
        List list = countHead();
        List list1 = columnHeader(map);
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

        List<List<Object>> lists = numerical(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "邀请奖明细 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }

    /**
     * 表头
     *
     * @return
     */
    private List countHead() {
        List list = new ArrayList();
        list.add("交易日期");
        list.add("推荐人户名");
        list.add("推荐人ID");
        list.add("推荐人手机号");
        list.add("被邀人户名");
        list.add("被邀人ID");
        list.add("推荐奖");
        return list;
    }

    private List columnHeader(Map map) {
        List list = new ArrayList();
        list.add("邀请奖明细 ");
        list.add("申请时间:" + map.get("startTime").toString() + " 至 " + map.get("endTime").toString());
        list.add("金额:" + map.get("startAmount").toString() + "  至  " + map.get("endAmount").toString());
        list.add("用户ID:" + map.get("recommendId").toString()+"  用户名："+ map.get("recommendName").toString()+"  奖励总金额:"+getRecommendTeacherAccount(map)+"元");
        return list;
    }

    private List<List<Object>> numerical(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        List<Map> list = rewardRecordMapper.getRecommendTeacherList(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            data.add(m.get("createTime") == null ? "" : m.get("createTime"));
            data.add(m.get("recommendName") == null ? "" : m.get("recommendName"));
            data.add(m.get("recommendId") == null ? "" : m.get("recommendId"));
            data.add(m.get("recommendMobile") == null ? "" : m.get("recommendMobile"));
            data.add(m.get("recommendedName") == null ? "" : m.get("recommendedName"));
            data.add(m.get("recommendedId") == null ? "" : m.get("recommendedId"));
            data.add(m.get("amount") == null ? "" : m.get("amount"));
            count.add(data);
        }
        return count;
    }

    @Override
    public List<Map> getFollowRewardRecordList(DatagridRequestModel page, Map map) {
        return rewardRecordMapper.getFollowRewardRecordListPage(page, map);
    }


    @Override
    public BigDecimal getFollowRewardRecordAccount(Map map) {
        return rewardRecordMapper.getFollowRewardRecordAccount(map);
    }

    @Override
    public String exportExcelFollowRewardRecord(Map map, HttpServletRequest request) throws Exception {
        List list = countHeadFollow();
        List list1 = columnHeaderFollow(map);
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

        List<List<Object>> lists = numericalFollow(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "关注奖明细 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }

    /**
     * 表头
     *
     * @return
     */
    private List countHeadFollow() {
        List list = new ArrayList();
        list.add("交易日期");
        list.add("讲师户名");
        list.add("讲师ID");
        list.add("讲师手机号");
        list.add("关注量");
        list.add("关注奖");
        return list;
    }

    private List columnHeaderFollow(Map map) {
        List list = new ArrayList();
        list.add("关注奖明细 ");
        list.add("申请时间:" + map.get("startTime").toString() + " 至 " + map.get("endTime").toString());
        list.add("金额:" + map.get("startAmount").toString() + "  至  " + map.get("endAmount").toString());
        list.add("用户ID:" + map.get("appId").toString()+"  用户名："+ map.get("name").toString()+"  奖励总金额:"+getFollowRewardRecordAccount(map)+"元");
        return list;
    }

    private List<List<Object>> numericalFollow(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        List<Map> list = rewardRecordMapper.getFollowRewardRecordList(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            data.add(m.get("createTime") == null ? "" : m.get("createTime"));
            data.add(m.get("name") == null ? "" : m.get("name"));
            data.add(m.get("appId") == null ? "" : m.get("appId"));
            data.add(m.get("mobile") == null ? "" : m.get("mobile"));
            data.add(m.get("remark") == null ? "" : m.get("remark"));
            data.add(m.get("amount") == null ? "" : m.get("amount"));
            count.add(data);
        }
        return count;
    }

    @Override
    public List<Map> getCourseRecommendList(DatagridRequestModel page, Map map) {
        return rewardRecordMapper.getCourseRecommendListPage(page, map);
    }

    @Override
    public BigDecimal getCourseRecommendAccount(Map map) {
        return rewardRecordMapper.getCourseRecommendAccount(map);
    }

    @Override
    public String exportExcelCourseRewardRecord(Map map, HttpServletRequest request) throws Exception {
        List list = countHeadCourse();
        List list1 = columnHeaderCourse(map);
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

        List<List<Object>> lists = numericalCourse(map);
        ExportExcelWhaUtil exportExcelWhaUtil = new ExportExcelWhaUtil(list, lists, "开课奖明细 ", request, result);
        String url = exportExcelWhaUtil.getExcel();
        return url;
    }

    @Override
    public List<Long> findAlreadyCourseId() {
        return rewardRecordMapper.findAlreadyCourseId();
    }

    /**
     * 表头
     *
     * @return
     */
    private List countHeadCourse() {
        List list = new ArrayList();
        list.add("交易日期");
        list.add("讲师户名");
        list.add("讲师ID");
        list.add("手机号");
        list.add("课时费");
        list.add("奖励听课人数量");
        list.add("龙链添加人数");
        list.add("开课奖励");
        list.add("课程名称");
        return list;
    }

    private List columnHeaderCourse(Map map) {
        List list = new ArrayList();
        list.add("开课奖明细 ");
        list.add("申请时间:" + map.get("startTime").toString() + " 至 " + map.get("endTime").toString());
        list.add("金额:" + map.get("startAmount").toString() + "  至  " + map.get("endAmount").toString());
        list.add("用户ID:" + map.get("appId").toString()+"  用户名："+ map.get("name").toString()+"  奖励总金额:"+getCourseRecommendAccount(map)+"元");
        return list;
    }

    private List<List<Object>> numericalCourse(Map map) {
        List<List<Object>> count = new ArrayList<List<Object>>();
        List<Map> list = rewardRecordMapper.getCourseRecommendList(map);
        for (Map<String, Object> m : list) {
            List<Object> data = new ArrayList();
            data.add(m.get("createTime") == null ? "" : m.get("createTime"));
            data.add(m.get("name") == null ? "" : m.get("name"));
            data.add(m.get("appId") == null ? "" : m.get("appId"));
            data.add(m.get("mobile") == null ? "" : m.get("mobile"));
            data.add(m.get("singleAmount") == null ? "" : m.get("singleAmount"));
            data.add(m.get("menCount") == null ? "" : m.get("menCount"));
            data.add(m.get("llAddMenCount") == null ? "0" : m.get("llAddMenCount"));
            data.add(m.get("amount") == null ? "" : m.get("amount"));
            data.add(m.get("liveTopic") == null ? "" : m.get("liveTopic"));
            count.add(data);
        }
        return count;
    }


}
