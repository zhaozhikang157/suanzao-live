package com.longlian.console.service.impl;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.dao.AppUserMapper;
import com.longlian.console.dao.SystemAdminMapper;
import com.longlian.console.service.AppUserService;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.RewardRecordService;
import com.longlian.model.AppUser;
import com.longlian.model.system.SystemAdmin;
import com.longlian.type.AccountFromType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/14.
 */
@Service("appUserService")
public class AppUserServiceImpl implements AppUserService {
    private static Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    AccountMapper accountMapper;

    @Autowired
    RewardRecordService rewardRecordService;
    @Autowired
    private SystemAdminMapper systemAdminMapper;
    @Override
    public DatagridResponseModel getAllAppUserinfoPage(String name , String mobile , String userType,
                                           DatagridRequestModel datagridRequestModel ) {
        DatagridResponseModel responseModel = new DatagridResponseModel();
        List<Map> appUserList = appUserMapper.getAllAppUserinfoPage(datagridRequestModel,name,mobile,userType);
        List<Map> courseList = liveRoomService.getCourseCount();
        int i = 1;
        for(Map appUser: appUserList){
            appUser.put("index",i);
            for(Map map: courseList){
                if(String.valueOf(appUser.get("id")).equals(map.get("appId").toString())){
                    appUser.put("followCount",map.get("followCount"));
                    appUser.put("payNumber",map.get("joinCount"));
                    appUser.put("courseCount",map.get("courseCount"));
                    continue;
                }
            }
            i++;
        }
        responseModel.setRows(appUserList);
        responseModel.setTotal(datagridRequestModel.getTotal());
        return responseModel;
    }

    /**
     * 后台轮询 推介老师奖励
     * @return
     */
    @Override
    public List<Map> getInvitationTeachRewardList() {
        //先获取已经推介的老师已经奖励过的老师记录
        String temps = rewardRecordService.getRewardSuccessInvitationTeach();
        List<Map> list = appUserMapper.getInvitationTeachRewardList(temps);
        return list;
    }

    @Override
    public Map getAppUsers(long id) {
        Map appUser = appUserMapper.getAppUser(id);
        List<Map> bankCard = appUserMapper.findBankCard(id);
        appUser.put("bankCards", bankCard);
        return appUser;
    }

    @Override
    public Map withdrawDeposit(long id) {
        Map map = new HashMap();
        Map sum = accountMapper.sumWithdrawDeposit(id);
        List<Map> maps = accountMapper.withdrawDeposit(id);
        if(sum!=null){
            map.put("sumAmount",sum.get("sumAmount")!=null?sum.get("sumAmount"):0);
            map.put("sumCharge",sum.get("sumCharge")!=null?sum.get("sumCharge"):0);
        }else{
            map.put("sumAmount",0);
            map.put("sumCharge",0);
        }
        map.put("maps", maps);
        return map;
    }

    @Override
    public Map membershiprebate(long id,Map mapRequest) {
        Map map = new HashMap();
        List<Map> maps = accountMapper.membershiprebate(id,mapRequest);
        BigDecimal rebateAmount = new BigDecimal(0);
        if(maps!=null&&maps.size()>0){
            for(Map m : maps){
                if(m!=null&&m.get("amount")!=null&&!"".equals(m.get("amount").toString())){
                    rebateAmount = rebateAmount.add(new BigDecimal(m.get("amount").toString()));
                }
                if(m!=null && m.get("returnMoneyLevel")!=null){
                    String typeStr = AccountFromType.getNameByValue(Integer.parseInt(m.get("returnMoneyLevel") + ""));
                    m.put("typeStr",typeStr);
                }
            }
        }
        map.put("rebateAmount",rebateAmount);
        map.put("rebates",maps);
        return map;
    }

    @Override
    public List<Map> getMemberDetailsList(DatagridRequestModel page, Map map) {
        if(!StringUtils.isEmpty(map.get("startTime")) && !StringUtils.isEmpty(map.get("endTime"))){
            String startTime = map.get("startTime").toString()+" 00:00:00";
            String endTime = map.get("endTime").toString()+" 23:59:59";
            map.put("startTime",startTime);
            map.put("endTime",endTime);
        }
        if(!StringUtils.isEmpty(map.get("createStartTime")) && !StringUtils.isEmpty(map.get("createEndTime"))){
            String startTime = map.get("createStartTime").toString()+" 00:00:00";
            String endTime = map.get("createEndTime").toString()+" 23:59:59";
            map.put("createStartTime",startTime);
            map.put("createEndTime",endTime);
        }
        return appUserMapper.getMemberDetailsListPage(page,map);
    }

    @Override
    public List<Map> getTeacherListPage(DatagridRequestModel page,Map map){
        if(!StringUtils.isEmpty(map.get("startTime")) && !StringUtils.isEmpty(map.get("endTime"))){
            String startTime = map.get("startTime").toString()+" 00:00:00";
            String endTime = map.get("endTime").toString()+" 23:59:59";
            map.put("startTime",startTime);
            map.put("endTime",endTime);
        }
        if(!StringUtils.isEmpty(map.get("createStartTime")) && !StringUtils.isEmpty(map.get("createEndTime"))){
            String startTime = map.get("createStartTime").toString()+" 00:00:00";
            String endTime = map.get("createEndTime").toString()+" 23:59:59";
            map.put("createStartTime",startTime);
            map.put("createEndTime",endTime);
        }
        return appUserMapper.getTeacherListPage(page,map);
    }
    @Override
    public AppUser getAppUserById(long id){
        return appUserMapper.findById(id);
    }

    @Override
    public List<Long> findAllAppUser() {
        return appUserMapper.findAllAppUser();
    }

    @Override
    public int getUserCount() {
        return appUserMapper.getUserCount();
    }

    @Override
    public List<Map> findNoYunxinTokenUser() {
        List<Map> list = appUserMapper.findNoYunxinTokenUser();
        return list;
    }

    @Override
    public void resetPwd(Long id,String password){
        appUserMapper.resetPwd(id,password);
    }

    @Override
    public ActResult updateProportion(Long id, Integer addCount) {
        ActResult act = new ActResult();
        try{
            appUserMapper.updateProportion(id,addCount);
            act.setData(addCount);
        }catch (Exception e){
            log.info("修改比率出错",e);
            act.setSuccess(false);
            act.setMsg("修改比率出错!请联系管理员！");
        }
        return act;
    }

    /**
     * 查询超级管理员
     *
     * @return
     */
    @Override
    public List<SystemAdmin> getSystemAdminList() {
        return systemAdminMapper.getSystemAdminList();
    }

    @Override
    public int insertSystemAdmin(SystemAdmin admin) throws Exception{
        int result = systemAdminMapper.insertSystemAdmin(admin);
        /*if (result > 0) {
            List<Long> teacherIdList = appUserMapper.getAllTeacherId();
            List<SystemAdminRel> adminList = new ArrayList<SystemAdminRel>();
            for (Long tId : teacherIdList) {
                SystemAdminRel rel = new SystemAdminRel();
                rel.setTeacherId(tId);
                rel.setUserId(admin.getAdminId());
                adminList.add(rel);
            }
            List<SystemAdminRel> adList= null;
            if (adminList.size() > 100) {
                int pageSize = adminList.size() % 100 == 0 ? adminList.size() / 100 : adminList.size() / 100 + 1;
                for (int i = 0; i < pageSize; i++) {
                    int pageNo = i * 100;
                    if(pageNo + 100 > adminList.size()){
                        adList = adminList.subList(pageNo, adminList.size());
                    } else {
                        adList = adminList.subList(pageNo, pageNo + 100);
                    }
                    systemAdminMapper.insertSystemAdminRel(adList);
                }
            } else {
                adList = adminList;
                systemAdminMapper.insertSystemAdminRel(adList);
            }
        }*/
        return result;
    }

    @Override
    public int deleteSystemAdmin(Long id) {
        return systemAdminMapper.deleteSystemAdmin(id);
    }

}
