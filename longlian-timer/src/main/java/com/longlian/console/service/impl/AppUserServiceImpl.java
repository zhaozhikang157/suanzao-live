package com.longlian.console.service.impl;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.console.dao.AppUserMapper;
import com.longlian.console.service.AppUserService;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.RewardRecordService;
import com.longlian.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 查询绑定直播间的服务号对应用户手机号及过期时间
     *
     * @return
     */
    @Override
    public List<Map> findUserPhoneList() {
        return appUserMapper.findUserPhoneList();
    }
}
