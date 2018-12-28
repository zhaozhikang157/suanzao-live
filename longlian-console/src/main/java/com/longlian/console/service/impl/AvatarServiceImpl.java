package com.longlian.console.service.impl;

import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.AppUserMapper;
import com.longlian.console.dao.AvatarMapper;
import com.longlian.console.dao.CourseMapper;
import com.longlian.console.service.AvatarService;
import com.longlian.console.service.CourseService;
import com.longlian.dto.AppUserDto;
import com.longlian.live.dao.AccountMapper;
import com.longlian.live.dao.CourseAvatarUserMapper;
import com.longlian.live.dao.LlAccountMapper;
import com.longlian.live.service.AppUserCommonService;
import com.longlian.live.service.CourseAvatarUserService;
import com.longlian.live.service.VirtualUserService;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.*;
import com.longlian.token.ConsoleUserIdentity;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * Created by admin on 2017/6/15.
 */
@Service("avatarService")
public class AvatarServiceImpl implements AvatarService {
    private static Logger log = LoggerFactory.getLogger(AvatarServiceImpl.class);

    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    AvatarMapper avatarMapper;
    @Autowired
    CourseAvatarUserMapper courseAvatarUserMapper;
    @Autowired
    CourseService courseService;
    @Autowired
    AppUserMapper appUserMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    LlAccountMapper llAccountMapper;
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    CourseAvatarUserService courseAvatarUserService;
    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    VirtualUserService virtualUserService;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<AppUserDto> findAllAvatarPage(DataGridPage dataGridPage,  String name , String isInRoom , Long courseId) {
        int totalCount=Integer.parseInt((appUserMapper.findAllAvatarTotalCount(name,isInRoom,courseId).get("totalCount")!=null)?appUserMapper.findAllAvatarTotalCount(name,isInRoom,courseId).get("totalCount")+"":"0");
        List<AppUserDto> list = appUserMapper.findAllAvatarPage(dataGridPage, name, isInRoom, courseId);
        dataGridPage.setTotal(totalCount);
        return list;
    }

    @Override
    public String createYunxinToken(AppUser appUser) {
        if (StringUtils.isEmpty(appUser.getYunxinToken())) {
           return appUserCommonService.createYunxinUser(appUser.getId() , appUser.getName() , appUser.getPhoto());
        }
        return appUser.getYunxinToken();
    }

    @Override
    public ActResult addRobot(String courseId, Long count) throws Exception {
        Course course = courseService.getCourse(Long.parseLong(courseId));
       List<AppUser> list = appUserMapper.getUsersByCount(Long.parseLong(courseId) , count);
        if (list.size() == 0 ) {
            return ActResult.success("无可添加的对象");
        }
       String[] accids = new String[list.size()];
       for (int i = 0 ;i < list.size() ;i++) {
           accids[i] = String.valueOf(list.get(i).getId());
       }
        Map result = virtualUserService.addVirtualUser(  course, accids);
        Object[] success = (Object[]) result.get("success");
        Object[] fail = (Object[]) result.get("fail");
        String[] s =  virtualUserService.getString(success);
        String[] f =  virtualUserService.getString(fail);

        String tip = "可添加用户："+ list.size() +",成功添加虚拟用户：" + success.length + ",添加失败：" + fail.length;
        return ActResult.success(tip);
    }

    @Override
    public ActResult removeRoboot(String courseId, Long count) {
        Course course = courseService.getCourse(Long.parseLong(courseId));
        List<CourseAvatarUser> list = courseAvatarUserMapper.getUsersByCount(Long.parseLong(courseId) , count);
        if (list.size() == 0 ) {
            return ActResult.success("无可移出的对象");
        }
        String[] accids = new String[list.size()];
        for (int i = 0 ;i < list.size() ;i++) {
            accids[i] = String.valueOf(list.get(i).getUserId());
        }
        Map result = yunxinUserUtil.removeRobot(String.valueOf(course.getChatRoomId()), accids);
        Object[] success = (Object[]) result.get("success");
        Object[] fail = (Object[]) result.get("fail");
        String[] s =  virtualUserService.getString(success);
        String[] f =  virtualUserService.getString(fail);

        courseAvatarUserService.batchRemoveUsers(course.getId(), s);
        String tip = "可移出用户："+ list.size() +",成功移出虚拟用户：" + success.length + ",移出失败：" + fail.length;
        return ActResult.success(tip);
    }

    @Override
    public ActResult removeRobootByUserId(String courseId, Long userId , boolean isDeleteDB) {
        Course course = courseService.getCourse(Long.parseLong(courseId));
        Map result = yunxinUserUtil.removeRobot(String.valueOf(course.getChatRoomId()), new String[]{String.valueOf(userId)});
        Object[] success = (Object[]) result.get("success");
        Object[] fail = (Object[]) result.get("fail");
        String[] s =  virtualUserService.getString(success);
        String[] f =  virtualUserService.getString(fail);
        if (isDeleteDB) {
            courseAvatarUserService.batchRemoveUsers(course.getId() , s);
        }
        String tip = "成功移出虚拟用户：" + success.length + ",移出失败：" + fail.length;
        return ActResult.success(tip);
    }



    @Override
    public void uploadUrl(String path , String fileName) {
        List<Avatar> list = getUrl(path , fileName);
        if(list != null && list.size()>0){
            avatarMapper.insertAcatar(list);
        }
    }
    public void dealYunxinToken() {
        List<AppUserDto> appUsers = appUserMapper.findAllAvatar();
        if (appUsers.size() > 0) {
                for (AppUser a : appUsers) {
                    createYunxinToken(a);
                }
            }
    }

    @Override
    public ActResult batchImportAvatarUser(String excelAddress, String fileName, ConsoleUserIdentity userIdentity) throws Exception {
        ActResult act = new ActResult();
         /*读取office2007及以上excel版本时使用*/
        try {
            FileInputStream fis = new FileInputStream(excelAddress);
            Workbook workbook = ExcelUtil.getWorkbook(fileName, fis);
            act = ExcelUtil.importEmployeeInfo(workbook);
            List<Map> parseData = (List<Map>) act.getData();/*从excel表中解析出来的员工信息*/
            List<AppUser> mcs = new ArrayList<AppUser>();   //用户信息
            List<Account> acc = new ArrayList<Account>();   //账户信息
            List<LlAccount> lla = new ArrayList<LlAccount>();   //学币信息
            List<Map> parseData2 = new ArrayList<Map>();/*打回的信息*/
            //包括表头
            if (parseData.size() <= 1) {
                return ActResult.fail("导入数据为空");
            }

            for (int i = 1;i < parseData.size();i++) {
                if(!"".equals(String.valueOf(parseData.get(i).get(0)))
                        &&!"".equals(String.valueOf(parseData.get(i).get(1)))){
                    AppUser appUser = new AppUser();
                    String photo = String.valueOf(parseData.get(i).get(1));
                    if (!photo.startsWith("http://")) {
                        List<Avatar> avatarList = avatarMapper.findPthotoByName(photo);
                        if(avatarList!=null && avatarList.size()>0){
                            photo = avatarList.get(0).getPhoto();
                        }
                    }
                    if(!StringUtils.isEmpty(photo)){
                        appUser.setPhoto(photo);
                        appUser.setName(String.valueOf(parseData.get(i).get(0)));
                        appUser.setRealName(String.valueOf(parseData.get(i).get(0)));
                        appUser.setCreateTime(new Date());

                        //如果有第3列数据，且值为1
                        if (parseData.get(i).size() > 2
                                && "1".equals(String.valueOf(parseData.get(i).get(2)))) {
                            appUser.setGender("1");
                        } else {
                            appUser.setGender("0");
                        }
                        appUser.setFromType("1");
                        appUser.setUserType("0");
                        appUser.setIsVirtualUser("1");
                        mcs.add(appUser);
                    }else{
                        act.setSuccess(false);
                        act.setMsg("信息导入失败");
                        return act;
                    }
                }
            }
            if(mcs.size()>0){
                appUserMapper.importUser(mcs);
                dealYunxinToken();
            }
            List<Long> userList = appUserMapper.findAvatars();
            List<Long> accList = accountMapper.findAvatars();
            List<Long> llaccList = llAccountMapper.findAllLLAccountIds();
            if(!accList.retainAll(userList)){
                accountMapper.addNoExitAppIds(userList);
            }else{
                accList.retainAll(userList);
                userList.removeAll(accList);
                accountMapper.addNoExitAppIds(userList);
            }
            if(!llaccList.retainAll(userList)){
                llAccountMapper.addNoExitAppIds(userList);
            }else{
                llaccList.retainAll(userList);
                userList.removeAll(llaccList);
                llAccountMapper.addNoExitAppIds(userList);
            }
            act.setSuccess(true);
            act.setMsg("信息导入成功");
            act.setData(parseData2);

            redisUtil.del(RedisKey.ll_all_virtual_userid);

            return act;
        }catch (IOException e){
            act.setSuccess(false);
            act.setMsg("信息导入失败");
            return act;
        }
    }


    public List getUrl(String path , String fileName) {
        ZipInputStream zin = null;
        ZipFile zf = null;
        InputStream in = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        ZipEntry ze;
        List<Avatar> list = new ArrayList<Avatar>();
        try {
            zf = new ZipFile(path,"GBK");
            in = new BufferedInputStream(new FileInputStream(path));
            zin = new ZipInputStream(in);
            Enumeration<ZipEntry> zipEntries = zf.getEntries();
            while (zipEntries.hasMoreElements()) {
                ze = zipEntries.nextElement();
                if (!ze.isDirectory()) {
                    long size = ze.getSize();
                    if (size > 0) {
                        Avatar avatar = new Avatar();
                        String photoName = ze.getName();
                        String newPhotoName = photoName.substring(fileName.length() - 3, photoName.length() - 4);
                        log.info("虚拟上传图片名称:" + newPhotoName);
                        inputStream = zf.getInputStream(ze);
                        BufferedImage bufferedImage = ImageIO.read(inputStream);
                        outputStream = new ByteArrayOutputStream();
                        String photoUrl = UUIDGenerator.generate() + "_avatar.png";
                        ImageIO.write(bufferedImage, "jpg", outputStream);
                        byte[] bytes = outputStream.toByteArray();
                        String url = ssoUtil.putObject(photoUrl, bytes);
                        log.info("虚拟上传图片路径:" + url);
                        outputStream.close();
                        inputStream.close();
                        avatar.setUserName(newPhotoName);
                        avatar.setPhoto(url);
                        list.add(avatar);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream != null){
                    outputStream.close();
                }
                if(inputStream != null){
                    inputStream.close();
                }
                if (zin != null) {
                    zin.closeEntry();
                }
                if(in != null){
                    in.close();
                }
                if(zf != null){
                    zf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    @Override
    public ActResult updateAvatar(AppUser appUser) {
        ActResult result = new ActResult();
        appUserMapper.updateAvatar(appUser);
        yunxinUserUtil.updateUserInfo(String.valueOf(appUser.getId()), appUser.getName() , appUser.getPhoto());
        redisUtil.del(RedisKey.ll_all_virtual_userid);
        return result;
    }
}
