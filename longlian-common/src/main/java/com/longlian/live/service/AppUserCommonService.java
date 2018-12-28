package com.longlian.live.service;

import com.longlian.model.AppUser;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by syl on 2017/7/8.
 */
public interface AppUserCommonService {


    public String createYunxinUser(Long accid, String name, String photo);

    /**
     * 获取用户 根据 openid 和 unionid
     * @param openid
     * @param unionid
     * @return
     */
    AppUser getByOpenidOrUnionid(String openid, String unionid, String systemType) throws Exception;


    void getAndHandlerSaveRedis();

    /**
     * 插入redis openid 和 unionid
     * @param openid
     * @param unionid
     * @param id
     * @return
     */
    void addAppUser2Redis(String openid, String unionid , long id);

    void handleAccount(AppUser appUser);

    AppUser getAppUser(Long id);

    /**
     * redis加载userInfo
     * @param id
     * @return
     */
    Map getUserInfoFromRedis(Long id);

    void loadVirtualUser2Redis();

    /**
     * 取随机数量的人员userId
     * @param base
     * @param random
     * @return
     */
    String[] getRandomCountUsers(int base, int random);

    AppUser queryByMobile( String mobile);

    int updateAppUserById( long id,  Map map);

    Map getUserInfo(long id);

    int updateUserType(long id);

    Integer getProportion(Long id);
}
