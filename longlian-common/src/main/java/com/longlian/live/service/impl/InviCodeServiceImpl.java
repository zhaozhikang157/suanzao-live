package com.longlian.live.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.Const;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.InviCodeDto;
import com.longlian.live.dao.InviCodeMapper;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.InviCodeItemService;
import com.longlian.live.service.InviCodeService;
import com.longlian.live.service.JoinCourseRecordService;
import com.longlian.model.Course;
import com.longlian.model.InviCode;
import com.longlian.model.JoinCourseRecord;
import com.longlian.token.AppUserIdentity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by admin on 2017/8/29.
 */
@Service("inviCodeService")
public class InviCodeServiceImpl implements InviCodeService {
    private static Logger log = LoggerFactory.getLogger(InviCodeServiceImpl.class);

    @Autowired
    InviCodeMapper inviCodeMapper;
    @Autowired
    InviCodeItemService itemService;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    RedisUtil redisUtil;
    @Value("${website}")
    private String website;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;

    /**
     * 获取所有的邀请码
     * @param dataGridPage
     * @param appId
     * @return
     */
    @Override
    public List<InviCodeDto> getAllInviCodePage(DataGridPage dataGridPage, long appId,InviCodeDto inviCode) {
        List<InviCodeDto> list = inviCodeMapper.getAllInviCodePage(dataGridPage, appId, inviCode);
        for(InviCodeDto dto: list){
            if(dto.getStartTime()!=null&&dto.getEndTime()!=null){
                dto.setTermOfValidity(DateUtil.format(dto.getStartTime(),"yyyy-MM-dd HH:mm:ss")+"  至  "
                        + DateUtil.format(dto.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
            }else if(dto.getStartTime()!=null&&dto.getEndTime()==null){
                dto.setTermOfValidity(DateUtil.format(dto.getStartTime(),"yyyy-MM-dd HH:mm:ss")+"之后使用");
            }else if(dto.getStartTime()==null&&dto.getEndTime()!=null){
                dto.setTermOfValidity(DateUtil.format(dto.getEndTime(),"yyyy-MM-dd HH:mm:ss")+"之前使用");
            }else {
                dto.setTermOfValidity("永久有效");
            }
            dto.setTheUseOfNum(dto.getBalanceCount()+"/"+dto.getCodeCount());
            dto.setCopyHref(website + "/weixin/inviCode?inviCodeId="+dto.getId());
        }
        return list;
    }

    /**
     * 插入邀请码
     * @param inviCode
     */
    @Override
    public void insertInviCode(InviCode inviCode) {
        if(StringUtils.isEmpty(inviCode.getRemark())){
            inviCode.setRemark("1.一个邀请码只能使用一次。<br>2.邀请码不可折为现金。<br>3.请在有效期内使用。");
        }
        inviCodeMapper.insertInviCode(inviCode);
        int num = inviCode.getCodeCount();
        itemService.insertItem(inviCode.getId(), num, inviCode.getCourseId());
        courseBaseService.updateIsInviteCode(inviCode.getCourseId());
        redisUtil.del(RedisKey.ll_course + inviCode.getCourseId());
    }

    /**
     * 导出
     * @param request
     *
     * @param response
     * @param courseName
     * @param id
     * @return
     */
    @Override
    public ExportExcelWhaUtil importExcelInviCode(HttpServletRequest request, HttpServletResponse response, String courseName, Long id) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        List<Map> maps = inviCodeMapper.getAllInviCode(token.getId(),courseName,id);
        String name = "";
        for(Map map : maps){
            map.put("courseStartTime",DateUtil.format(map.get("courseStartTime").toString(),"yyyy-MM-dd HH:mm:ss"));
            map.put("theUseOfNum",map.get("balanceCount").toString() + " " +"/"+" "+ map.get("codeCount").toString() );
        }
        List<String> listStr = new ArrayList<String>();
        String top1 = "邀请码列表";
        listStr.add(top1);
        String top3 = "序号,批次,课程名称,开课时间,邀请码使用情况,有效期";
        listStr.add(top3);
        List<ExcelTop> exceltitel = this.getExceltitel(listStr);
        String keys = "id,liveTopic,courseStartTime,theUseOfNum,termOfValidity";
        List<List<Object>> content = ExportExcelWhaUtil.getContent(maps, keys);
        ExportExcelWhaUtil excel = new ExportExcelWhaUtil(null, content, "邀请码列表", request, exceltitel);
        return excel;
    }

    @Override
    public InviCodeDto findInviCodeInfo(long id) {
        InviCodeDto dto = inviCodeMapper.findCodeInfo(id);
        if(dto.getStartTime()!=null&&dto.getEndTime()!=null){
            dto.setTermOfValidity(DateUtil.format(dto.getStartTime(),"yyyy-MM-dd HH:mm:ss")+"  至  "
                    + DateUtil.format(dto.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        }else if(dto.getStartTime()!=null&&dto.getEndTime()==null){
            dto.setTermOfValidity(DateUtil.format(dto.getStartTime(),"yyyy-MM-dd HH:mm:ss")+"之后使用");
        }else if(dto.getStartTime()==null&&dto.getEndTime()!=null){
            dto.setTermOfValidity(DateUtil.format(dto.getEndTime(),"yyyy-MM-dd HH:mm:ss")+"之前使用");
        }else {
            dto.setTermOfValidity("永久有效");
        }
        dto.setTheUseOfNum(dto.getBalanceCount() + "/" + dto.getCodeCount());
        dto.setRemark(DateUtil.format(dto.getCourseStartTime()));
        dto.setCopyHref(website + "/weixin/inviCode?inviCodeId="+dto.getId());
        return dto;
    }

    @Override
    public InviCode findInviCode(long id) {
        return inviCodeMapper.getCodeInfo(id);
    }

    public List<ExcelTop> getExceltitel(List<String> strs) {
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
     * 微信邀请码页面
     * @param id
     * @return
     */
    @Override
    public Map getInfo(long id) {
        return inviCodeMapper.getInfo(id);
    }

    @Override
    public String isUseTime(String inviCodeId , long appId) {
        String redisTime = redisUtil.get(RedisKey.ll_invi_code_use_time + inviCodeId + appId);
        String isUse = "1"; // 0:能使用  1:不在有效期内  2:过期  3:第二次点击
        if(StringUtils.isNotEmpty(redisTime)){
            String[] times = redisTime.split(",");
            String start = times[0].trim();
            String end = times[1].trim();
            if(StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)){
                Date startTime = DateUtil.format(start);
                Date endTime = DateUtil.format(end);
                if(startTime.compareTo(new Date()) < 1 && endTime.compareTo(new Date()) > -1){
                    isUse = "0";
                }else if(startTime.compareTo(new Date())  > 0){   //开始时间大于当前时间 :还不能使用
                    isUse = "1";
                }else if(endTime.compareTo(new Date()) < 0){      //结束时间小于当前时间 :过期
                    isUse = "2";
                }
            }else if(StringUtils.isNotEmpty(start) && !StringUtils.isNotEmpty(end)){
                Date startTime = DateUtil.format(start);
                if(startTime.compareTo(new Date()) < 1){
                    isUse = "0";
                }else{
                    isUse = "1";
                }
            }else if(!StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)){
                Date endTime = DateUtil.format(end);
                if(endTime.compareTo(new Date()) > -1){
                    isUse = "0";
                }else{
                    isUse = "2";
                }
            }else{
                isUse = "0";
            }
        }else{
            isUse = "3";
        }
        return isUse;
    }

    @Override
    public Map getInviCode(long courseId , AppUserIdentity identity , Long inviCodeId ,Course course) throws Exception {
        Map map = new HashMap<>();
        map.put("itemId",0);
        if(course!=null){
            String isSeriesCourse = course.getIsSeriesCourse();
            map.put("isSeriesCourse",isSeriesCourse);
            if(identity!=null && !String.valueOf(course.getAppId()).equals(String.valueOf(identity.getId()))){
                long teacherId = course.getAppId();
                if(!String.valueOf(teacherId).equals(String.valueOf(identity.getId()))){
                    //处理抢码，
                    JoinCourseRecord joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseIdByRedis(identity.getId() , courseId , false );
                    if(joinCourseRecord == null || "-1".equals(joinCourseRecord.getSignUpStatus()) ||  "0".equals(joinCourseRecord.getSignUpStatus())){
                        //先从redis看抢到码了没
                        if(!redisUtil.hexists(RedisKey.ll_get_invi_code_course + courseId, identity.getId() + "")){
                            //如果没有从redis队列拉取
                            String itemId = redisUtil.rpop(RedisKey.ll_get_no_invi_code_course + inviCodeId);
                            if(!Utility.isNullorEmpty(itemId)){
//                                Map m = new HashMap<>();
//                                m.put("appId",identity.getId());
//                                m.put("itemId",itemId);
//                                m.put("courseId",courseId);
                                redisUtil.hset(RedisKey.ll_get_invi_code_course + courseId, identity.getId() + "", itemId);//存入抢到的邀请码
//                                redisUtil.lpush(RedisKey.ll_get_use_invi_code_course_wait2db, JsonUtil.toJson(m)); //入消息队列
                            }else{
                                map.put("isGrab","1");
                            }
                        }
                    }
                }
            }
            if(identity !=null && String.valueOf(course.getAppId()).equals(String.valueOf(identity.getId()))){
                map.put("isTeacherSelf",1); //是老师自己
                return map;
            }
            if(identity !=null && redisUtil.hexists(RedisKey.ll_get_invi_code_course + courseId, identity.getId() + "")){
                String redisItemId = redisUtil.hget(RedisKey.ll_get_invi_code_course + courseId, identity.getId() + "");
                if(!org.springframework.util.StringUtils.isEmpty(redisItemId)){
                    map.put("itemId", redisUtil.hget(RedisKey.ll_get_invi_code_course + courseId,identity.getId() + ""));
                }
            }
        }
        return map;
    }
}
