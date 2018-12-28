package com.longlian.live.service.impl;

import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.*;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.ShortMessage;
import com.longlian.live.util.StoreFileUtil;
import com.longlian.live.util.SystemUtil;
import com.longlian.live.util.pic.BlurPicUtil;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.*;
import com.longlian.model.course.CourseCard;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.SMSTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/2/8.
 */
@Service("appUserService")
public class AppUserServiceImpl implements AppUserService {

    private static Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    LiveRoomMapper liveRoomMapper;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    RedisUtil redisUtil;
    @Value("${website}")
    private String website;
    @Autowired
    MessageClient messageClient;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    InviCardMapper inviCardMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    JoinCourseRecordMapper joinCourseRecordMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    LlAccountService llAccountService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    CountService countService;
    @Autowired
    AppMsgService appMsgService;
    @Autowired
    InviCardService inviCardService;
    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    AppUserAccountDataRunnerImpl appUserAccountDataRunner;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    UserFollowWechatOfficialService userFollowWechatOfficialService;

    @Autowired
    ShortMessage shortMessage;
    @Autowired
    private StoreFileUtil storeFileUtilLonglian;
    @Autowired
    CourseService courseService;
    @Autowired
    SystemAdminMapper systemAdminMapper;
    @Autowired
    CourseRelayService courseRelayService;

    @Override
    public ActResultDto loginIn(AppUser user, String machineType) throws Exception {
        ActResultDto result = new ActResultDto();
        if(!isMobileNO(user.getMobile())){
            result.setCode(ReturnMessageType.CODE_MOBILE_ERROR.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_ERROR.getMessage());
            return result;
        }
            AppUser selectUser = appUserCommonService.queryByMobile(user.getMobile());
        if(selectUser == null){
            result.setCode(ReturnMessageType.NOT_EXIT_APP_USER.getCode());
            result.setMessage(ReturnMessageType.NOT_EXIT_APP_USER.getMessage());
            return result;
        }
        /*if("teacherLogin".equals(machineType)){
            if("0".equals(selectUser.getUserType())){
                result.setCode(ReturnMessageType.STUDENT_NOT_LOGIN.getCode());
                result.setMessage(ReturnMessageType.STUDENT_NOT_LOGIN.getMessage());
                return result;
            }
        }*/
        if (user.getPassword().equals(selectUser.getPassword())) {
            //用户正常
                if ("0".equals(selectUser.getStatus())) {
                    AppUserIdentity identity = new AppUserIdentity();
                    BeanUtils.copyProperties(selectUser, identity);
                    result.setData(loginSuccess(identity, machineType));
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            } else {
                result.setCode(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getCode());
                result.setMessage(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getMessage());
            }
        } else {
            result.setCode(ReturnMessageType.CODE_LOGIN_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_LOGIN_FALSE.getMessage());
        }
        return result;
    }

    @Override
    public ActResultDto loginInByMobile(AppUser user, String machineType) throws Exception {
        ActResultDto result = new ActResultDto();
        if(!verificationCode(user.getMobile(), user.getPassword(), RedisKey.ll_login_mobile_code)){
            result.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return result;
        }
        AppUser selectUser = appUserCommonService.queryByMobile(user.getMobile());
        if("teacherLogin".equals(machineType)){

            if(selectUser == null){
                result.setCode(ReturnMessageType.NOT_EXIT_APP_USER.getCode());
                result.setMessage(ReturnMessageType.NOT_EXIT_APP_USER.getMessage());
            }
        }
        if (selectUser != null) {
            //用户正常
            if ("0".equals(selectUser.getStatus())) {
                AppUserIdentity identity = new AppUserIdentity();
                BeanUtils.copyProperties(selectUser, identity);
                result.setData(loginSuccess(identity, machineType));
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            } else {
                result.setCode(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getCode());
                result.setMessage(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getMessage());
            }
        } else {
            result.setCode(ReturnMessageType.NOT_EXIT_APP_USER.getCode());
            result.setMessage(ReturnMessageType.NOT_EXIT_APP_USER.getMessage());
        }
        return result;
    }

    /**
     * 微信登录
     *
     * @param weixinAppUser
     * @param invitationAppId 邀请人ID
     * @param  sytemType  系统类型 H5-微信 APP-安卓和IOS
     */
    @Override
    public ActResultDto weixinLogin(WeixinAppUser weixinAppUser, long invitationAppId ,String sytemType) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity identity = new AppUserIdentity();
        //先考虑unionid当唯一用户
        AppUser appUser = appUserCommonService.getByOpenidOrUnionid(weixinAppUser.getOpenid(), weixinAppUser.getUnionid(),sytemType);
        //AppUser appUser = appUserMapper.queryByUnionid(weixinAppUser.getUnionid());
        /*在考虑openid 是否存在，兼容下老数据，
         但是有一种情况下回存在问题 那就是之前没有unionid ,且现在用app端微信注册情况，存在两个账号了，后面再去用微信登录，自动和新用户关联，导致老用户彻底不关联*/
        //if(appUser == null) appUser =  appUserMapper.queryByOpenid(weixinAppUser.getOpenid());
        if (appUser == null ||  appUser.getId() == 0) {
            //不存在创建用户
            appUser = new AppUser();
            appUser.setOpenid(weixinAppUser.getOpenid());
            appUser.setPassword("");
            appUser.setLoginCount(1);
            appUser.setGender(weixinAppUser.getSex());
            appUser.setCity(weixinAppUser.getCity());
            appUser.setFromType("0");
            appUser.setName(weixinAppUser.getNickname());
            appUser.setPhoto(weixinAppUser.getHeadimgurl());
            appUser.setUnionid(weixinAppUser.getUnionid());
            appUser.setInvitationAppId(invitationAppId);
            // System.out.println("新增---" + invitationAppId);
            appUser.setIsFollowLlWechat(weixinAppUser.getIsFollowLlWechat());
            createUser(appUser);
            //马上设置redis
            appUserCommonService.addAppUser2Redis(weixinAppUser.getOpenid(), weixinAppUser.getUnionid(), appUser.getId());
            //学币账号和钱包账号迁移新开的线程中
            appUserAccountDataRunner.headler(appUser);
           /* //创建学币账号
            LlAccount llAccount = new LlAccount();
            llAccount.setAccountId(appUser.getId());
            llAccount.setCreateTime(new Date());
            llAccountService.addAccount(llAccount);
            //创建钱包账号
            Account account = new Account();
            account.setAccountId(appUser.getId());
            account.setCreateTime(new Date());
            accountService.addAccount(account);*/
            //创建聊天用户
                if (!"weixin".equals(sytemType)) {
                    String token = yunxinUserUtil.createUser(String.valueOf(appUser.getId()), appUser.getName(), appUser.getPhoto());
                    appUserMapper.updateYunXinToken(appUser.getId(), token);
                } else {
                    Map send = new HashMap();
                send.put("id" , String.valueOf(appUser.getId()));
                send.put("name" ,appUser.getName());
                send.put("photo" ,appUser.getPhoto());
                redisUtil.lpush(RedisKey.ll_create_yunxin_user , JsonUtil.toJson(send));
            }
            countService.newUserCount(appUser.getId());
            identity.setNewUser(true);

        } else {
            appUser = getById(appUser.getId());//从数据取一次
            if (!"0".equals(appUser.getStatus())) {
                actResultDto.setCode(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getCode());
                actResultDto.setMessage(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getMessage());
                return actResultDto;
            }

            //处理更新信息  ...
            String name = appUser.getName() == null ? "" : appUser.getName();
            String gender = appUser.getGender() == null ? "" : appUser.getGender();
            String photo = appUser.getPhoto() == null ? "" : appUser.getPhoto();
            String unionid = appUser.getUnionid() == null ? "" : appUser.getUnionid();
            String openid = appUser.getOpenid() == null ? "" : appUser.getOpenid();
            String isFollowLlWechat =  appUser.getIsFollowLlWechat() == null ? "0" : appUser.getIsFollowLlWechat();
            String headimgurl = weixinAppUser.getHeadimgurl();
            String weixinPhotoPre = "http://wx.qlogo.cn/mmopen";

            appUser.setUnionid(weixinAppUser.getUnionid());//避免以前没有unionid用户
            if(photo.startsWith(weixinPhotoPre) || photo.equals("")
                    || !unionid.equals(weixinAppUser.getUnionid())  || !openid.equals(weixinAppUser.getOpenid())
                    || !isFollowLlWechat.equals(weixinAppUser.getIsFollowLlWechat())) {//处理如果设置好自己的图像后，就不做修改
                if (!photo.startsWith(weixinPhotoPre)) headimgurl = photo;
                if ("weixin".equals(sytemType)) {//微信端进来的，需要更新
                    openid = weixinAppUser.getOpenid();
                    appUser.setOpenid(openid);
                }
                try {
                    //第二次以及以后微信授权登陆之后 名称和性别不再改变
                    appUserMapper.updateWechatInfo(appUser.getId() ,headimgurl, name, gender, weixinAppUser.getUnionid() ,openid,weixinAppUser.getIsFollowLlWechat());
                    if (!photo.equals(headimgurl) ) {
                        yunxinUserUtil.updateUserInfo(String.valueOf(appUser.getId()), name, headimgurl);
                    }
                } catch (Exception ex){
                    log.error("用户更新问题:{}" , ex);
                    GlobalExceptionHandler.sendEmail(ex,"用户更新问题");
                }
            }
        }
        String yunxinToken = appUser.getYunxinToken();
        if (!"weixin".equals(sytemType)) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(yunxinToken)) {
                yunxinToken = yunxinUserUtil.refreshToken(String.valueOf(appUser.getId()));
                this.updateYunxinToken(appUser.getId(), yunxinToken);
                appUser.setYunxinToken(yunxinToken);
            }
        }
        BeanUtils.copyProperties(appUser, identity);
        //用户登录
        loginSuccess(identity, sytemType);
        if(!identity.isNewUser()){
            LlAccount accountByUserId = llAccountService.getAccountByUserId(appUser.getId());
            if(accountByUserId!=null){
                identity.setBalance(accountByUserId.getBalance());
            }
        }
        actResultDto.setData(identity);
        return actResultDto;
    }


    /**
     * 微信登录
     *
     * @param appUser
     */
    @Override
    public ActResultDto getWeixinLoginFormWebsite(AppUser  appUser) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity identity = new AppUserIdentity();
        BeanUtils.copyProperties(appUser, identity);
        //用户登录
        loginSuccess(identity, "teacherLogin_web");
        actResultDto.setData(identity);
        return actResultDto;
    }


    public void updateLogUserName(long appUserId , String field, String value ) {
        if (redisUtil.exists(RedisKey.ll_live_teacher_app_login_prefix + appUserId)) {
            redisUtil.hset(RedisKey.ll_live_teacher_app_login_prefix + appUserId , field , value );
            redisUtil.expire(RedisKey.ll_live_teacher_app_login_prefix + appUserId ,RedisKey.ll_live_app_user_login_valid_time);
        }

        if (redisUtil.exists(RedisKey.ll_live_weixin_login_prefix + appUserId)) {
            redisUtil.hset(RedisKey.ll_live_weixin_login_prefix + appUserId , field, value );
            redisUtil.expire(RedisKey.ll_live_weixin_login_prefix + appUserId ,RedisKey.ll_live_app_user_login_valid_time);
        }

        if (redisUtil.exists(RedisKey.ll_live_teacher_web_login_prefix + appUserId)) {
            redisUtil.hset(RedisKey.ll_live_teacher_web_login_prefix + appUserId, field, value);
            redisUtil.expire(RedisKey.ll_live_teacher_web_login_prefix + appUserId, RedisKey.ll_live_app_user_login_valid_time);
        }
    }

    @Override
    public ActResultDto updateUser(long appUserId, String name) throws Exception {
        ActResultDto result = new ActResultDto();
        long status = appUserMapper.updateUser(appUserId, name);
        if (status > 0) {
            if(org.apache.commons.lang3.StringUtils.isNotBlank(name)){
                liveRoomMapper.updateRoomByAppId(appUserId,name+"的直播间");
            }
            name = Utility.getCheckNum(name);
            redisUtil.hdel(RedisKey.ll_user_info ,String.valueOf(appUserId));
            yunxinUserUtil.updateUserInfo(String.valueOf(appUserId), name, null);
            updateLogUserName(appUserId, "name", name);
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } else {
            result.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return result;
    }

    @Override
    public Map getUserInfo(long id) {
        Map map = appUserCommonService.getUserInfo(id);
        //判断有无未读信息
        map.put("isAppMsg", appMsgService.getIsAppMsg(id));
        //个人钱包
        Account account = accountMapper.getAccountByAppId(id);
        map.put("recommendNum", joinCourseRecordMapper.getCourseRecordCount(id));
        if (account == null) {
            map.put("myCount", 0.00);
        } else {
            map.put("myCount", account.getBalance());
        }
        LiveRoom liveRoom = liveRoomMapper.findByAppId(id);
        map.put("wechat","0");
        map.put("isNew","0");
        if (liveRoom == null) {
            map.put("authStatus", -1);
            map.put("authRemark", "");
            map.put("followCount", 0);
        } else {
            long followCount = userFollowService.getCountByRoomId(liveRoom.getId());
            map.put("authStatus", liveRoom.getStatus());
            map.put("liveRoomId", liveRoom.getId());
            map.put("authRemark", liveRoom.getAuthRemark());
            map.put("followCount", followCount);
            Boolean bo = wechatOfficialService.isWechatOfficial(liveRoom.getId());
            if(bo){
                map.put("wechat","1");
                map.put("isNew","1");
            }
        }
        return map;
    }

    @Override
    public long userLiveRoomFollowCount(long id) {
        LiveRoom liveRoom = liveRoomMapper.findByAppId(id);
        if (liveRoom == null) {
            return 0;
        } else {
            return userFollowService.getCountByRoomId(liveRoom.getId());
        }
    }

    /**
     * 新接口，专为我的个人中心调用
     * @param id
     * @return
     */
    @Override
    public Map updateFetchUserInfo(long id) {
        Map map = appUserCommonService.getUserInfo(id);
        //判断有无未读信息
        map.put("isAppMsg", appMsgService.getIsAppMsg(id));
        //个人钱包
        Account account = accountMapper.getAccountByAppId(id);
        map.put("recommendNum", joinCourseRecordMapper.getCourseRecordCount(id));
        if (account == null) {
            map.put("myCount", 0.00);
        } else {
            map.put("myCount", account.getBalance());
        }
        LiveRoom liveRoom = liveRoomMapper.findByAppId(id);
        map.put("wechat","0");
        map.put("isNew","0");
        if (liveRoom == null) {
            map.put("authStatus", -1);
            map.put("authRemark", "");
            map.put("followCount", 0);
        } else {
            long followCount = userFollowService.getCountByRoomId(liveRoom.getId());
            map.put("authStatus", liveRoom.getStatus());
            map.put("liveRoomId", liveRoom.getId());
            map.put("authRemark", liveRoom.getAuthRemark());
            map.put("followCount", followCount);
            Boolean bo = wechatOfficialService.isWechatOfficial(liveRoom.getId());
            if(bo){
                map.put("wechat","1");
                map.put("isNew","1");
            }
        }
        String blurUrl = "";
        String headPhoto = map.get("headPhoto") != null ? map.get("headPhoto").toString() : "";
        String blurPhoto = map.get("blurPhoto") != null ? map.get("blurPhoto").toString() : "";
        //第一次或redis的key过期进入我的页面
        if(Utility.isNullorEmpty(blurPhoto)) {
            if(!Utility.isNullorEmpty(headPhoto)) {
                blurUrl = updateBlurPhotoUrl(headPhoto, id);
            }
        } else {
            blurUrl = blurPhoto;
        }
        map.put("blurPhoto", blurUrl);
        return map;
    }

    public String updateBlurPhotoUrl(String headPhoto,Long id){
        String blurUrl = "";
        try {
            URL url = new URL(headPhoto);
            URLConnection urlConn = url.openConnection();
            InputStream input = urlConn.getInputStream();
            BufferedImage blurredImage = ImageIO.read(input);
            //保存处理后的图
            ByteArrayOutputStream bos = new ByteArrayOutputStream();// 存储图片文件byte数组
            //模糊处理图片
            blurredImage = BlurPicUtil.blur(blurredImage, 200);
            ImageIO.write(blurredImage, "jpg", bos);
            try {
                String fileName = "";
                StoreFile storeFile = null;
                if (headPhoto.indexOf("longlian-live") != -1) {
                    fileName = headPhoto.substring(headPhoto.lastIndexOf("/") + 1);//截取文件名
                    String prevName = fileName.substring(0, fileName.lastIndexOf("."));
                    String ext = StoreFileUtil.getExtensionName(fileName);
                    fileName = prevName + "_blur." + ext;
                    storeFile = new StoreFile(fileName, DigestUtils.md5DigestAsHex(bos.toByteArray()), 0L, null);
                } else if (headPhoto.indexOf("thirdwx.qlogo.cn") != -1 || headPhoto.indexOf("wx.qlogo.cn") != -1) {//没有阿里云存储头像文件
                    fileName = id + "_blur.jpg";
                    storeFile = new StoreFile(fileName, DigestUtils.md5DigestAsHex(bos.toByteArray()), 0L, null);
                }
                storeFile.setCreateTime(new Date());
                blurUrl = storeFileUtilLonglian.saveFile(bos.toByteArray(), storeFile, "1");
                appUserMapper.updateUserBlurPhoto(id, blurUrl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("高斯模糊图片处理异常:", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blurUrl;
    }
    @Override
    public ActResultDto getApplySms(String mobile ,String redisKey , String type) {
        ActResultDto result = new ActResultDto();
        if (StringUtils.isEmpty(mobile)) {    //手机号码为空
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        mobile = mobile.trim();
        if (!isMobileNO(mobile)) {              //判断是否为手机号码
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        if("2".equals(type)){  //1.不用判断 2.需要判断
            AppUser selectUser =appUserCommonService.queryByMobile(mobile);
            if(selectUser == null){
                result.setCode(ReturnMessageType.NOT_EXIT_APP_USER.getCode());
                result.setMessage(ReturnMessageType.NOT_EXIT_APP_USER.getMessage());
                return result;
            }
        }
        String number = redisUtil.get(redisKey + mobile);//从redis获取
        if (Utility.isNullorEmpty(number)) number = getCheckCode();//如果没有随机生成6位
        String content = "您的验证码为:" + number + "请在有效期2分钟内验证,注意安全！【酸枣在线】";
        //messageClient.sendMessage(mobile, content);
        log.info("================验证码获取成功-手机号：" + mobile+ "-验证码:" + number);
        shortMessage.send_message2( Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getName()),
                Optional.of(SMSTemplate.SMS_TEMPLATE_NAME_SZZX_FORGETPASSWORD.getCode()), Optional.of(mobile),number);

        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        redisUtil.set(redisKey + mobile, number);          //存放到redis
        redisUtil.expire(redisKey + mobile, 2 * 60);     //设置验证码有效期5分钟
        return result;
    }


    /**
     * 注册手机号
     *
     * @param appUserIdentity
     * @param mobile
     * @param checkCode
     * @return
     */
    @Override
    public ActResultDto registerMobile(AppUserIdentity appUserIdentity, String mobile, String checkCode) {
        ActResultDto result = new ActResultDto();
        if (StringUtils.isEmpty(mobile)) {    //手机号码为空
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        mobile = mobile.trim();
        if (!isMobileNO(mobile)) {              //判断是否为手机号码
            result.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return result;
        }
        String number = redisUtil.get(RedisKey.ll_live_mobile_register_sms + mobile);//从redis获取
        if (Utility.isNullorEmpty(number)) {
            result.setCode(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getCode());
            result.setMessage(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getMessage());
            return result;
        }
        if (!number.equals(checkCode)) {
            result.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return result;
        }
        //判断手机号是否已经被使用
        AppUser appUser = appUserCommonService.queryByMobile(mobile);
        if (appUser != null) {
            result.setCode(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getMessage());
            return result;
        }
        updateMobile(appUserIdentity.getId(), mobile);
        //重新设置token 中mobile
        redisUtil.hset(RedisKey.ll_live_weixin_login_prefix + appUserIdentity.getId(), "mobile", mobile);
        return result;
    }

    /**
     * 更新手机号
     *
     * @param id     用户Id
     * @param mobile 手机号
     * @return
     * @throws Exception
     */
    public int updateMobile(long id, String mobile) {
        int i = appUserMapper.updateMobile(id, mobile);
        return i;
    }


    @Override
    public int updatePhone(long id, String photo) throws Exception {
        int i = appUserMapper.updatePhone(id, photo);
        yunxinUserUtil.updateUserInfo(String.valueOf(id), null, photo);
        this.updateLogUserName(id , "photo" , photo);
        return i;
    }

    /**
     * 老师端发送验证码
     *
     * @param mobile
     * @return
     */
    @Override
    public ActResultDto teacherSendCode(String mobile) {
        ActResultDto resultDto = new ActResultDto();
        AppUser appUser = appUserCommonService.queryByMobile(mobile);
        if (appUser == null) {
            resultDto.setMessage(ReturnMessageType.NOT_EXIT_APP_USER.getMessage());
            resultDto.setCode(ReturnMessageType.NOT_EXIT_APP_USER.getCode());
            return resultDto;
        }
        //不是老师不让登录
        if ("0".equals(appUser.getUserType())) {
            resultDto.setMessage(ReturnMessageType.NOT_IS_TEACHER.getMessage());
            resultDto.setCode(ReturnMessageType.NOT_IS_TEACHER.getCode());
            return resultDto;
        }
        return getApplySms(mobile , RedisKey.ll_live_mobile_register_sms,"2");
    }

    /**
     * 验证验证码
     *
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public Boolean verificationCode(String mobile, String code  ,String redisKey) {
        mobile = mobile.trim();
        String redisCode = redisUtil.get(redisKey + mobile);
        if (StringUtils.isEmpty(redisCode)) {
            return false;
        }
        if (redisCode.equals(code)) {
            redisUtil.del(redisKey + mobile);
            return true;
        }
        return false;
    }

    @Override
    public ActResultDto setPassword(String mobile, String password) {
        ActResultDto actResultDto = new ActResultDto();
        try {
            appUserMapper.setPassword(mobile, password);
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            actResultDto.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        }
        return actResultDto;
    }

    /**
     * 成功登录
     *
     * @param user
     * @param machineType
     * @return
     * @throws Exception
     */
    public AppUserIdentity loginSuccess(AppUserIdentity user, String machineType) throws Exception {
        user.setPassword("");        //去掉密码
        user.setToken(SystemUtil.createToken(user.getId(), machineType));
        if ("teacherLogin".equals(machineType)) {
            //龙链直播老师登录端
            user.setIsTeacherClient("1");
            Map<String, String> m = ObjectUtil.objectToStringMap(user);
            redisUtil.hmset(RedisKey.ll_live_teacher_app_login_prefix + user.getId(), m, RedisKey.ll_live_app_user_login_valid_time);
        } else if ("teacherLogin_web".equals(machineType)) {
            //龙链直播老师网站登录
            user.setIsTeacherClient("2");
            Map<String, String> m = ObjectUtil.objectToStringMap(user);
            redisUtil.hmset(RedisKey.ll_live_teacher_web_login_prefix + user.getId(), m, RedisKey.ll_live_app_user_login_valid_time);
        } else {
            //龙链直播学生微信端
            user.setIsTeacherClient("0");
            Map<String, String> m = ObjectUtil.objectToStringMap(user);
            redisUtil.hmset(RedisKey.ll_live_weixin_login_prefix + user.getId(), m, RedisKey.ll_live_app_user_login_valid_time);
        }
        return user;
    }

    /**
     * 创建用户
     *
     * @param appUser
     */
    public void createUser(AppUser appUser) {
        appUser.setStatus("0");
        appUser.setCreateTime(new Date());
        appUser.setLevel(0);
        appUser.setUserPriv("0");
        String isFollowWechat = "0";
        if(!Utility.isNullorEmpty(appUser.getIsFollowLlWechat())){
            isFollowWechat = appUser.getIsFollowLlWechat();
        }
        String userType = "0";
        if(!Utility.isNullorEmpty(appUser.getUserType())){
            userType = appUser.getUserType();
        }
        appUser.setIsFollowLlWechat(isFollowWechat);
        appUser.setUserType(userType);
        appUserMapper.insert(appUser);
    }


    @Override
    public AppUser getById(long id) {
        return appUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public String doInvitationPrivateCard(String modelUrl, HttpServletRequest request,
                                           Long courseId, String type) {
        /*ActResultDto actResult = new ActResultDto();
        if(courseId == null){
            actResult.setCode(ReturnMessageType.INVICARD_ERROR.getCode());
            actResult.setMessage(ReturnMessageType.INVICARD_ERROR.getMessage());
            return actResult;
        }*/
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息;
        Long loginAppId = 0L;
        if(identity != null) {
            loginAppId = identity.getId();
        }
        List<InviCard> inviCardList = inviCardService.getCourseOrRoomCard(type);
        InviCard inviCard = null;
        String url = "";
        if(inviCardList != null && inviCardList.size() > 0){
            ByteArrayOutputStream outputStream = null;
            Course course = null;
            try{
                inviCard = inviCardList.get(0);
                course = courseMapper.getCourse(courseId);
                Long appId = course.getAppId();
                BufferedImage bufferedImage = inviCardService.createPrivateDrawImage(appId, course, modelUrl, inviCard,loginAppId);
                //输出
                outputStream = new ByteArrayOutputStream();
                String path = UUIDGenerator.generate() + "_inviCard.png";
                ImageIO.write(bufferedImage, "jpg", outputStream);
                byte[] bytes = outputStream.toByteArray();
                url = ssoUtil.putObject(path, bytes);
                //更新模板编号
                if(appId==loginAppId){
                    courseMapper.updateTemp("2",courseId);
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("生成私人邀请卡异常:"+e.getMessage());
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //actResult.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
                //actResult.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
            }
            /*Map map = new HashMap();
            map.put("url", url);
            map.put("code", inviCard.getCode());
            map.put("listInviCards", inviCardList);
            map.put("loginAppId",loginAppId);
            String shareAddress = "";
            if(courseId != 0l){
                shareAddress = website + "/weixin/courseInfo?id=" + course.getId() +
                        "&invitationAppId=" + loginAppId + "&fromType=1"+"&seriesid="+course.getSeriesCourseId()+"&isSeries="+course.getIsSeriesCourse();
            }
            map.put("shareAddress",shareAddress);
            actResult.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResult.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResult.setData(map);*/
        }
        return url;
    }

    /**
     * 合成我的邀请卡
     *
     * @param bgColor  邀请卡模板编号
     * @param courseId 课程卡ID
     * @return
     */
    @Override
    public ActResultDto drawInvitationCard(String bgColor, HttpServletRequest request,
                                          Long courseId, Long roomId) {
        ActResultDto actResult = new ActResultDto();
        if (courseId == null) courseId = 0l;
        if (roomId == null) roomId = 0l;
        if(courseId == 0l && roomId == 0l){
            actResult.setCode(ReturnMessageType.INVICARD_ERROR.getCode());
            actResult.setMessage(ReturnMessageType.INVICARD_ERROR.getMessage());
            return actResult;
        }
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息;
        Long appId = 0l;
        Long loginAppId = 0l;
        if(identity != null){
            appId = identity.getId();
            loginAppId = identity.getId();
        }
        String contextUrl = request.getServletContext().getRealPath("") + "web/res/";
        //获取直播间或者课程所有模板编号 0:代表课程模板 1:直播间模板 2:系列课
        Course course = null;
        String type = "1";
        if(courseId != null && courseId > 0){
            type = "0";
            course = courseMapper.getCourse(courseId);
            if(courseId!=null && String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
                course=courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
            }
            if(course != null){
                if("1".equals(course.getIsSeriesCourse())){
                    type = "2";
                }
            }else{
                actResult.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResult.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResult;
            }
        }
        //获取一个邀请卡模板信息
        InviCard inviCard = null;
        List<InviCard> inviCardList = inviCardService.getCourseOrRoomCard(type);
        LiveRoom liveRoom = null;
        if(StringUtils.isEmpty(bgColor)){
            if("0".equals(type) || "2".equals(type)){
                appId = course.getAppId();
                for(InviCard invi : inviCardList){
                    if(invi.getCode().equals("2")){
                        inviCard = invi;
                    }
                }
            }else if("1".equals(type)){
                liveRoom = liveRoomMapper.findById(roomId);
                appId = liveRoom.getAppId();
                for(InviCard invi : inviCardList){
                    if(invi.getCode().equals("2")){
                        inviCard = invi;
                    }
                }
            }
        }else{
            if("0".equals(type) || "2".equals(type)){
                course = courseMapper.getCourse(courseId);
                if(courseId!=null && String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                    CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
                    course=courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
                }
                appId = course.getAppId();
            }else{
                liveRoom = liveRoomMapper.findById(roomId);
                appId = liveRoom.getAppId();
            }
            for(InviCard invi : inviCardList){
                if(invi.getCode().equals(bgColor)){
                    inviCard = invi;
                }
            }
        }
        if(inviCard == null){
            actResult.setCode(ReturnMessageType.INVICARD_ERROR.getCode());
            actResult.setMessage(ReturnMessageType.INVICARD_ERROR.getMessage());
            return actResult;
        }
        String url = "";
        ByteArrayOutputStream outputStream = null;
        try {
            //图片合成
            BufferedImage bufferedImage = null;
            if("0".equals(type) || "2".equals(type)){   //课程模板
                bufferedImage = inviCardService.courseDrawImage(appId, course, contextUrl, inviCard,loginAppId);
            }else{                  //直播间模板
                bufferedImage = inviCardService.roomDrawImage(appId, liveRoom, contextUrl, inviCard,loginAppId);
            }
            if(bufferedImage == null){
                actResult.setCode(ReturnMessageType.INVICARD_ERROR.getCode());
                actResult.setMessage(ReturnMessageType.INVICARD_ERROR.getMessage());
                return actResult;
            }
            //输出
            outputStream = new ByteArrayOutputStream();
            String path = UUIDGenerator.generate() + "_inviCard.png";
            ImageIO.write(bufferedImage, "jpg", outputStream);
            byte[] bytes = outputStream.toByteArray();
            url = ssoUtil.putObject(path, bytes);
            //更新模板编号
            if(appId==loginAppId){
                if("0".equals(type)){
                    courseMapper.updateTemp("2",courseId);
                }else{
                    liveRoomMapper.updateTemp("2",roomId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            actResult.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
            actResult.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
        }
        Map map = new HashMap();
        map.put("url", url);
        map.put("code", inviCard.getCode());
        map.put("listInviCards", inviCardList);
        map.put("loginAppId",loginAppId);
//        map.put("shareAddress",
//                website + "/weixin/shareInviCard?invitationAppId="+appId+"&roomIdOrCourseId="+courseId+"&shareInviCardUrl="+url+"&fromType=1");
        String shareAddress = "";
        if(courseId != 0l){
            shareAddress = website + "/weixin/courseInfo?id=" + course.getId() +
                    "&invitationAppId=" + loginAppId + "&fromType=1"+"&seriesid="+course.getSeriesCourseId()+"&isSeries="+course.getIsSeriesCourse();
        }else if(roomId != 0l){
            shareAddress = website + "/weixin/liveRoom?invitationAppId="+loginAppId+"&fromType="+2+"&id="+roomId;
        }
        map.put("shareAddress",shareAddress);
        if(course != null && course.getSeriesCourseId() > 0){
            courseId = course.getSeriesCourseId();
        }
        if(courseId != null && courseId.longValue() > 0) {
            int result = courseService.findCourseIsExist(courseId);
            CourseCard card = courseService.findCardUrlByCourseId(courseId);
            if (card != null) {
                String modelUrl = card.getModelUrl();
                if(StringUtils.isEmpty(modelUrl)){
                    map.put("cardUrl", "");
                } else {
                    map.put("cardUrl", card.getCardUrl() == null ? "" : card.getCardUrl());
                }
            } else {
                map.put("cardUrl", "");
            }
            map.put("isPermit", result);
        }
        actResult.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        actResult.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        actResult.setData(map);
        return actResult;
    }



    /**
     * 判断是否是手机号
     *
     * @param mobiles
     * @return
     */
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((17[0-9])|(14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(16[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    // 产生6位长度的验证码
    public String getCheckCode() {
        String checkCode = "";
        for (int i = 0; i < 6; i++) {
            String code = (int) Math.floor(Math.random() * 10) + "";
            checkCode += code;
        }
        return checkCode;
    }

    /**
     * 设置相关用户的yunxintoken
     *
     * @param id
     * @param yunxinToken
     */
    public void updateYunxinToken(long id, String yunxinToken) {
        appUserMapper.updateYunXinToken(id, yunxinToken);
    }

    /**
     * 查出所有的appUser导入云信账号
     *
     * @return
     */
    public List<AppUser> getAllAppUser() {
        return appUserMapper.getAllUser();
    }

    @Override
    public ActResultDto getCourseListByAppId(Long appId, Integer pageNum, Integer pageSize, long roomId) {
        ActResultDto actResultDto = new ActResultDto();
        Map map = new HashMap();
        DataGridPage dg = new DataGridPage();
        if (pageNum != null) dg.setCurrentPage(pageNum);
        if (pageSize != null) dg.setPageSize(pageSize);
        String openId="";
        //获取聊天室信息
        LiveRoom liveRoom = liveRoomMapper.findById(roomId);
        //加载第一页的时候
        if (pageNum == 1) {
            //获取个人信息
            AppUser appUser = appUserMapper.selectByPrimaryKey(appId);
            map.put("appUser", appUser);

            map.put("liveRoom", liveRoom);
            openId = appUser.getOpenid();
            if(liveRoom!=null){
                AppUser liveAppUser = appUserMapper.selectByPrimaryKey(liveRoom.getAppId());
                map.put("liveAppUser", liveAppUser);
            }
            if (userFollowService.isFollowRoom(roomId, appId)) {//判断聊天室是否关注过
                map.put("isFollow", 1);
            } else {
                map.put("isFollow", 0);
            }
            //获取直播间关注数
            map.put("followNum", userFollowService.getCountByRoomId(roomId));
        }

        //获取列表
        List<Map> list = new ArrayList<>();
        if (liveRoom!=null&&liveRoom.getAppId() != appId) {
            list = appUserMapper.getCourseListByAppIdPage(dg, liveRoom.getAppId(), roomId);
        } else {
            list = appUserMapper.getCourseListByAppIdPage(dg, appId, roomId);
        }
        if (list != null && list.size() > 0) {
            for (Map m : list) {
                Date startTime =(Date) m.get("startTime");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                m.put("startTime",  DateUtil.transFormationStringDate2(DateUtil.format(startTime)));
                m.put("courseStatus",1);
                if (StringUtils.isEmpty(m.get("endTime"))) {
                    if (startTime.getTime() > new Date().getTime()) {
                        m.put("time", "课程预告");
                        m.put("courseStatus",0);
                    } else {
                        m.put("time", "已开播");
                    }
                } else {
                    m.put("time", "点击回看");
                }
                Date endTime = (Date) m.get("endTime");
                String liveTimeStatus = getLiveTimeStatus((Date) startTime, endTime);
                m.put("liveTimeStatus", liveTimeStatus);

                if(m.get("id")!=null && String.valueOf(m.get("id")).length()==32){

                }else{
                    m.put("count" ,joinCourseRecordService.getCountByCourseId((Long)m.get("id")));
                }
            }
        }else{
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        if(liveRoom!=null){
            Map result = userFollowWechatOfficialService.getUserFollowInfoByCourse( 0l,appId ,roomId , liveRoom.getAppId(),"");
            map.put("isFollowResult", result);
        }
            map.put("courseList", list);
            actResultDto.setData(map);
        return actResultDto;
    }
    /**
     * 通用视频时间状态 0-预告 1-直播中 2-已结束
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public String getLiveTimeStatus(Date startTime, Date endTime) {
        Date currDate = new Date();
        String liveTimeStatus = "2";//结束的
        if (endTime == null) {
            if (currDate.compareTo(startTime) < 0) {
                liveTimeStatus = "0";//预告
            } else {
                liveTimeStatus = "1";//直播中的
            }
        }
        return liveTimeStatus;
    }

    /**
     * 根据 weixin  openid 获取 用户ID
     * @param openid
     * @return
     */
    @Override
    public long getAppIdByOpenid(String openid) {
        return appUserMapper.getAppIdByOpenid(openid);
    }

    @Override
    public  void updateForAppUserById(long id , String cardNo, String realName){
        Map map = new HashMap();
        map.put("cardNo", cardNo);
        map.put("realName", realName);
        map.put("teacherCreateTime", new Date());
        appUserMapper.updateForAppUserById(id, map);
    }



    /**
     * 绑定手机号
     * @param appId
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public ActResultDto bindingMobile(long appId, String mobile, String code ,String password) {
        ActResultDto resultDto = new ActResultDto();
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code) || StringUtils.isEmpty(password)){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(!isMobileNO(mobile)){
            resultDto.setCode(ReturnMessageType.CODE_MOBILE_FALSE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MOBILE_FALSE.getMessage());
            return resultDto;
        }
        String redisCode = redisUtil.get(RedisKey.ll_binding_mobile + mobile);
        if(StringUtils.isEmpty(redisCode)){
            resultDto.setCode(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getCode());
            resultDto.setMessage(ReturnMessageType.VERIFICATION_CODE_TIMEOUT.getMessage());
            return resultDto;
        }
        if(!redisCode.equals(code)){
            resultDto.setCode(ReturnMessageType.CODE_VERIFICATION_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_VERIFICATION_TRUE.getMessage());
            return resultDto;
        }
        AppUser appUser = appUserCommonService.queryByMobile(mobile);
        if (appUser != null) {
            if(appUser.getId() != appId){
                resultDto.setCode(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getCode());
                resultDto.setMessage(ReturnMessageType.CODE_MOBILE_ISEXIST_TRUE.getMessage());
                return resultDto;
            }
        }
        appUserMapper.updateMobileAndPassword(mobile, password, appId);
        redisUtil.hset(RedisKey.ll_live_weixin_login_prefix + appId, "mobile", mobile);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    @Override
    public List<Map> getSeriesListByAppId(Long appId,Integer offset, Integer pageSize,long roomId  , boolean isHaveRecord,Integer isQueryRelay) {
        DataGridPage dg = new DataGridPage();
        if (offset != null) dg.setOffset(offset);
        if (pageSize != null) dg.setPageSize(pageSize);
        //获取聊天室信息
        LiveRoom liveRoom = liveRoomMapper.findById(roomId);
        //获取列表
        //没有录播状态的
        String isRecorded = "0";
        if (isHaveRecord) {
            //有录播状态的
            isRecorded = "";
        }
        List<Map> list =null;
        if(liveRoom!=null&&appId!=liveRoom.getAppId()){
            list = appUserMapper.getSeriesCourseListByOtherAppIdPage(dg, roomId , isRecorded,isQueryRelay);
        }else{
            list = appUserMapper.getSeriesCourseListByAppIdPage(dg, roomId , isRecorded,isQueryRelay);
        }
        if (list.size() > 0) {
            for (Map m : list) {
                long seriesId = 0;
                if(m.get("id").toString().length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                    CourseRelayDto courseRelayDto=courseRelayService.queryById((Long) m.get("id"));
                    seriesId=courseRelayDto.getOriCourseId();
                }else{
                    seriesId=Long.parseLong(m.get("id").toString());
                }
                if (m.get("startTime") != null) {
                    m.put("startTimeStr", DateUtil.transFormationStringDate(m.get("startTime").toString()));
                } else {
                    m.put("startTimeStr", "");
                }

                String liveTimeStatus = getLiveStatus((Date) m.get("startTime"), (Date) m.get("endTime"));
                m.put("liveTimeStatus", liveTimeStatus);
                Course course = courseMapper.findSeriesCourseOrderTime(seriesId);
                if(course != null){
                    m.put("startTime",course.getStartTime());
                }
//                m.put("chargeAmt", m.get("chargeAmt").toString());
                if(m.get("id")!=null && String.valueOf(m.get("id")).length()==32){

                }else{
                    m.put("count", joinCourseRecordService.getCountByCourseId((Long) m.get("id")));
                }
            }
        }
        return list;
    }

    public String getLiveStatus(Date startTime, Date endTime) {
        Date currDate = new Date();
        String liveTimeStatus = "0";//结束的
        if (endTime == null) {
            if (currDate.compareTo(startTime) < 0) {
                liveTimeStatus = "1";//预告
            } else {
                liveTimeStatus = "2";//直播中的
            }
        }
        return liveTimeStatus;
    }

    /**
     * 根据 unionId  查询用户
     * @param unionId
     * @return
     */
    @Override
    public AppUser getByUnionid(String unionId) {
        return  appUserMapper.queryByUnionid(unionId);
    }
    /**
     * 通过用户id查询其头像
     * @param id
     * @return
     */
    public Map getNameAndPhoto(Long id){
        return appUserMapper.getNameAndPhoto(id);
    }

    /**
     * 更新模糊头像
     * @param id
     * @param blurPhoto
     * @return
     */
    public int updateUserBlurPhoto(Long id,String blurPhoto){
        return appUserMapper.updateUserBlurPhoto(id, blurPhoto);
    }
    public static void  main(String[] args) throws Exception{
        System.out.println(System.currentTimeMillis()/1000);
        String url2="https://longlian-live.oss-cn-hangzhou.aliyuncs.com/upload/2018/04/01c555b3c1df448b8dd923f82bcdb8a0.jpg";
        URL url = new URL(url2);
        URLConnection urlConn = url.openConnection();
        InputStream input = urlConn.getInputStream();
        BufferedImage b1 = ImageIO.read(input);
        //模糊
       /* GaussianFilter filter = new GaussianFilter(200);
        BufferedImage blurredImage = filter.filter(b1, new BufferedImage(b1.getWidth(), b1.getHeight(), BufferedImage.TYPE_4BYTE_ABGR));
        //保存处理后的图
        ByteArrayOutputStream bos = new ByteArrayOutputStream();// 存储图片文件byte数组
        File file=new File("G:\\tmp\\test5.jpg");
        FileOutputStream fos2 = new FileOutputStream(file);
        ImageIO.write(blurredImage, "jpg", bos); // 图片写入到 ImageOutputStream
        bos.writeTo(fos2);
        fos2.close();
        bos.close();*/
        b1 = BlurPicUtil.blur(b1, 200);
        ImageIO.write(b1,"jpg",new File("G:\\tmp\\2.jpg"));
        input.close();
        System.out.println(System.currentTimeMillis()/1000);
    }

    /**
     * 获取版本号
     * @return
     */
    @Override
    public Map<String,String> getSystemVision() {
        return appUserMapper.getSystemVision();
    }

    /**
     * 查询管理员用户是否存在
     *
     * @param adminId
     */
    @Override
    public int findSystemAdminByUserId(Long adminId) {
        return systemAdminMapper.findSystemAdminByUserId(adminId);
    }

    /**
     * 根据设备序列号查询用户
     * @param deviceId
     * @return AppUser
     */
    @Override
    public AppUser selectByTouristDeviceId(String deviceId) {
        return appUserMapper.selectByTouristDeviceId(deviceId);
    }

    /**
     * 游客登录
     * @param appUser
     * @return
     * @throws Exception
     */
    @Override
    public ActResultDto touristLogin(AppUser appUser) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity identity = new AppUserIdentity();
        if(org.apache.commons.lang3.StringUtils.isBlank(appUser.getDeviceId())){
            result.setCode(ReturnMessageType.CODE_TOURIST_LOGIN_FALSE.getCode());
            result.setMessage(ReturnMessageType.CODE_TOURIST_LOGIN_FALSE.getMessage());
            return result;
        }
        AppUser selectUser = appUserMapper.selectByTouristDeviceId(appUser.getDeviceId());
        if(selectUser == null){
            //注册用户
            AppUser appUserNew = new AppUser();
            appUserNew.setName("游客"+(int)((Math.random()*9+1)*100000));
            appUserNew.setStatus("0");
            appUserNew.setCreateTime(new Date());
            appUserNew.setFromType("1");
            appUserNew.setIsVirtualUser("0");
            appUserNew.setDeviceId(appUser.getDeviceId());
            appUserNew.setUserType("0");
            appUserMapper.insert(appUserNew);
            appUserAccountDataRunner.headler(appUserNew);

            String token = yunxinUserUtil.createUser(String.valueOf(appUserNew.getId()), appUserNew.getName(), appUserNew.getPhoto());
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(token)) {
                //token = yunxinUserUtil.refreshToken(String.valueOf(appUserNew.getId()));
                appUserNew.setYunxinToken(token);
                this.updateYunxinToken(appUserNew.getId(), token);
            }
            Map send = new HashMap();
            send.put("id" , String.valueOf(appUserNew.getId()));
            send.put("name" ,appUserNew.getName());
            send.put("photo" ,appUserNew.getPhoto());
            redisUtil.lpush(RedisKey.ll_create_yunxin_user , JsonUtil.toJson(send));
            BeanUtils.copyProperties(appUserNew, identity);
        }else {
            //用户正常
            if ("0".equals(selectUser.getStatus())) {
                BeanUtils.copyProperties(selectUser, identity);
                result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            } else {
                result.setCode(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getCode());
                result.setMessage(ReturnMessageType.CODE_USERNAME_DISABLE_FALSE.getMessage());
            }
        }
        //用户登录
        result.setData(loginSuccess(identity, "TOURIST"));
        return result;
    }

}
