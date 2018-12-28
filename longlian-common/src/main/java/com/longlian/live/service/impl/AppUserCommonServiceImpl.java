package com.longlian.live.service.impl;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisLock;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.live.dao.AppUserCommonMapper;
import com.longlian.live.service.AccountService;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.LlAccountService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.log.Log;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.Account;
import com.longlian.model.AppUser;
import com.longlian.model.LlAccount;
import com.longlian.model.SystemLog;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by pangchao on 2017/7/8.
 */
@Service("appUserCommonService")
public class AppUserCommonServiceImpl implements AppUserCommonService  {
    private static Logger log = LoggerFactory.getLogger(AppUserCommonServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    private RedisLock redisLock;

    @Autowired
    AppUserCommonMapper appUserCommonMapper;

    @Autowired
    AccountService accountService;
    @Autowired
    LlAccountService llAccountService;



    /**
     * 获取用户 根据 openid 和 unionid  如果不存在则先存进去
     * @param openid
     * @param unionid
     * @return
     */
    @Override
    public AppUser getByOpenidOrUnionid(String openid, String unionid , String systemType) throws Exception{
        boolean exists = redisUtil.exists(RedisKey.ll_live_app_user_by_unionid);
        AppUser appUser = null;
        if(!exists){
            getAndHandlerSaveRedis();
            exists = redisUtil.exists(RedisKey.ll_live_app_user_by_unionid);
        }
        if(exists){
            appUser = new AppUser();
            String ids = "";
            boolean existsUser = redisUtil.hexists(RedisKey.ll_live_app_user_by_unionid ,unionid);//是否存在 unionid
            if(!existsUser){
                existsUser = redisUtil.hexists(RedisKey.ll_live_app_user_by_openid, openid);//是否存在openid
                if(existsUser){
                    ids = redisUtil.hget(RedisKey.ll_live_app_user_by_openid, openid);//先取一下再存入
                    redisUtil.hset(RedisKey.ll_live_app_user_by_unionid , unionid , ids);//之前没有unionid加进去
                }
            }else{
                ids = redisUtil.hget(RedisKey.ll_live_app_user_by_unionid ,unionid );//取Idunionid
            }
            //不存在则添加到reids，占着，id设置为0
            if(!existsUser || "0".equals(ids)){
                //long countOpenid = redisUtil.hsetnx(RedisKey.ll_live_app_user_by_openid, openid, "0");//新用户就不用了
                long countUnionid = redisUtil.hsetnx(RedisKey.ll_live_app_user_by_unionid, unionid , "0");
                if(countUnionid == 0){
                    for (int i = 1 ; i<=10 ; i++){
                        Thread.sleep(100);//在沉睡100毫秒，可能在创建数据库
                        ids = redisUtil.hget(RedisKey.ll_live_app_user_by_unionid ,unionid );//再取一次
                        if(!"0".equals(ids)){
                            //log.error("并发处理appUser,循环次数才取到：" + i + ";openid:" + openid + ";unionid:" + unionid);
                            SystemLog sLog = new SystemLog();
                            sLog.setLogType(LogType.log_repeat_count.getType()+ "");
                            sLog.setDeviceNo("");
                            sLog.setSystemType(systemType);
                            sLog.setUserId(0);
                            sLog.setUserName(unionid);
                            sLog.setContent("并发处理appUser,循环次数才取到：" + i + ";openid:" + openid + ";unionid:" + unionid);
                            sLog.setTableId(0);
                            sLog.setTableType(LogTableType.def.getVal());
                            SystemLogUtil.saveLog(sLog);
                            break;
                        }
                    }
                }
            }
            appUser.setOpenid(openid);
            appUser.setUnionid(unionid);
            if(!Utility.isNullorEmpty(ids)){
                appUser.setId(Utility.parseLong(ids));
            }
        }
        return appUser;
    }


    /**
     * 重新设置redis数据
     */
    @Override
    public void getAndHandlerSaveRedis(){
        redisUtil.del(RedisKey.ll_live_app_user_by_openid);
        redisUtil.del(RedisKey.ll_live_app_user_by_unionid);
        List<Map> list =  appUserCommonMapper.getAllUserPartInfo();
        for (Map map : list){
            String id = map.get("id").toString();
            if(!Utility.isNullorEmpty(map.get("openid"))){
                String openid = map.get("openid").toString();
                redisUtil.hset(RedisKey.ll_live_app_user_by_openid, openid, id);
            }
            if(!Utility.isNullorEmpty(map.get("unionid"))){
                String unionid = map.get("unionid").toString();
                redisUtil.hset(RedisKey.ll_live_app_user_by_unionid , unionid , id);
            }
        }
    }


    /**
     * 插入redis openid 和 unionid
     * @param openid
     * @param unionid
     * @param id
     * @return
     */
    @Override
    public  void addAppUser2Redis(String openid, String unionid , long id){
        //redisUtil.hset(RedisKey.ll_live_app_user_by_openid, openid, id + "");不用匹配了，老用户
        redisUtil.hset(RedisKey.ll_live_app_user_by_unionid, unionid, id + "");
    }


    /**
     * 创建用户处理学币账户和钱包账号
     * @param appUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class )
    public void handleAccount(AppUser appUser){

        //创建学币账号
        LlAccount llAccount = new LlAccount();
        llAccount.setAccountId(appUser.getId());
        llAccount.setCreateTime(new Date());
        llAccountService.addAccount(llAccount);
        //创建钱包账号
        Account account = new Account();
        account.setAccountId(appUser.getId());
        account.setCreateTime(new Date());
        accountService.addAccount(account);

    }

    @Override
    public AppUser getAppUser(Long id) {
        return appUserCommonMapper.selectByPrimaryKey( id);
    }

    /**
     * redis加载userInfo
     *
     * @param id
     * @return
     */
    @Override
    public Map getUserInfoFromRedis(Long id) {
        String userInofStr = redisUtil.hget(RedisKey.ll_user_info,String.valueOf(id));
        if (!StringUtils.isEmpty(userInofStr)) {
            return  JsonUtil.getObject(userInofStr , HashMap.class);
        } else {
            AppUser appUser = this.getAppUser(id);
            if (appUser != null) {
                Map temp = new HashMap();
                temp.put("id", appUser.getId());
                temp.put("name", appUser.getName());
                temp.put("photo", appUser.getPhoto());
                redisUtil.hset(RedisKey.ll_user_info, String.valueOf(appUser.getId()), JsonUtil.toJson(temp));
                return temp;
            }
        }
        return null;
    }

    @Override
    public void loadVirtualUser2Redis() {
        String key = RedisKey.ll_all_virtual_userid ;
        if (redisUtil.exists(key)) {
            return ;
        }
        List<String> list = new ArrayList<>();
        List<Map> mapList   =  appUserCommonMapper.getAllVirtualUserIds();

        for (Map m : mapList) {
            list.add(String.valueOf(m.get("ID")));
        }
        redisUtil.lpushlist(key , list);
    }

    @Override
    public String[] getRandomCountUsers(int base, int randomNum) {
        //取范围内的随机数
        Random random = new Random();
        int s = random.nextInt(randomNum);
        int count = base + s;


        List<String> list = redisUtil.lrangeall(RedisKey.ll_all_virtual_userid);
        //不够
        if (count >= list.size()) {
            return list.toArray(new String[0]);
        } else {
            int[] indexs = Utility.getRandoms(list.size() , count);
            List<String> result = new ArrayList<>();
            for (int i = 0 ; i < indexs.length ;i++) {
                if (indexs[i] < list.size()) {
                    result.add(list.get(indexs[i]));
                }
            }
            return result.toArray(new String[0]);
        }
    }



    @Override
    public String createYunxinUser(Long accid, String name, String photo) {
        //200毫秒轮讯一次 ， 2秒算超时
        boolean flag = redisLock.lock(RedisKey.ll_lock_pre + accid, 200 * 1000, 6);
        //获取锁失败，
        if (!flag) {
            log.info("获取锁{}失败，请稍等!", RedisKey.ll_lock_pre + accid);
            GlobalExceptionHandler.sendEmail("获取锁" + RedisKey.ll_lock_pre + accid + "失败，请等侍!", "注意");
            return "-1";
        }
        try {
            String temp = redisUtil.get(RedisKey.ll_yunxin_token_temp + accid);
            //已经创建好
            if (!StringUtils.isEmpty(temp)) {
                return temp;
            }
            String token = yunxinUserUtil.createUser(String.valueOf(accid), name, photo);
            if(!StringUtils.isEmpty(token)) {
                appUserCommonMapper.updateYunXinToken(accid, token);
                //如果登录用户
                if (redisUtil.exists(RedisKey.ll_live_weixin_login_prefix + accid)) {
                    redisUtil.hset(RedisKey.ll_live_weixin_login_prefix + accid, "yunxinToken" , token );
                }
                //缓存一个小时
                redisUtil.setex(RedisKey.ll_yunxin_token_temp + accid ,  60 * 60  , token);
            }
            return token;
        }finally {
            redisLock.unlock(RedisKey.ll_lock_pre + accid);
        }
    }


   public AppUser queryByMobile( String mobile) {
        return appUserCommonMapper.queryByMobile(mobile);
   }

    public int updateAppUserById(  long id,  Map map) {
        return appUserCommonMapper.updateAppUserById(id, map);
    }

    public  Map getUserInfo(long id){
        return appUserCommonMapper.getUserInfo(id);
    }

    public int updateUserType(long id){
        return appUserCommonMapper.updateUserType(id);
    }

    @Override
    public Integer getProportion(Long id) {
        return appUserCommonMapper.getProportion(id);
    }

}