package com.longlian.console.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.console.dao.MobileVersionMapper;
import com.longlian.console.service.MobileVersionService;
import com.longlian.dto.MobileVersionDto;
import com.longlian.model.MobileVersion;
import com.longlian.model.UserMachineInfo;
import com.longlian.type.MobileVersionType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
@Service("MobileVersionService")
public class MobileVersionServiceImpl implements MobileVersionService {
    private static Logger log = LoggerFactory.getLogger(MobileVersionServiceImpl.class);
    //必须升级的版本
    public static int maxAndroidVersion = 0;

    //在线的版本
    public static int superAndroidVersion = 0;

    //必须升级的版本
    public static int maxIosVersion = 0;

    //在线的版本
    public static int superIosVersion = 0;
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MobileVersionMapper mobileVersionMapper;
    @Override
    public DatagridResponseModel getListPage(DatagridRequestModel requestModel, MobileVersionDto mobileVersion) {
        DatagridResponseModel model = new DatagridResponseModel();
        List<MobileVersionDto> list = mobileVersionMapper.getListPage(requestModel, mobileVersion);
        for (int i = 0; i < list.size(); i++) {
            String value = list.get(i).getVersionType();
            String type = MobileVersionType.getNameByValue(value);
            list.get(i).setType(type);//获取版本类型
            if ("0".equals(list.get(i).getStatus())) {//选择下线
                Date time = list.get(i).getOfflineTime();
                list.get(i).setTime(time);
            } else {
                Date time = list.get(i).getOnlineTime();
                list.get(i).setTime(time);//获取上线时间
            }
        }
        model.setRows(list);
        model.setTotal(requestModel.getTotal());
        return model;
    }

    @Override
    public MobileVersion findById(long id) throws Exception {
        return mobileVersionMapper.findById(id);
    }

    @Override
    public void doSaveAndUpdate(MobileVersion mobileVersion) throws Exception {

        String status = mobileVersion.getStatus();
        if ("1".equals(status)) {  // 上线
            String versionType = mobileVersion.getVersionType();//获取版本类型
            mobileVersionMapper.updateByVersionType(versionType);//根据版本类型下线
        }
        int i = 0;
        if (mobileVersion.getId() > 0) {//修改
            i = mobileVersionMapper.update(mobileVersion);
        } else {//添加
            if ("0".equals(mobileVersion.getStatus())) {//选择下线
                i = mobileVersionMapper.create(mobileVersion);
            }else{
                mobileVersionMapper.createOnline(mobileVersion);//选择上线
            }
        }
        if (MobileVersionType.android.getValue().equals(mobileVersion.getVersionType())) {
            redisUtil.publish("msg.app", "android_version_change");


            MobileVersion mobileMaxVersion1 =  selectMaxVersion(MobileVersionType.android.getValue());
            MobileVersion mobileSuperVersion1 =  getSuperVersion(MobileVersionType.android.getValue());

            if (mobileMaxVersion1 != null) {
                MobileVersionServiceImpl.maxAndroidVersion = getInteger(mobileMaxVersion1.getVersionNum());
            } else {
                MobileVersionServiceImpl.maxAndroidVersion = 0;
            }

            if (mobileSuperVersion1 != null) {
                MobileVersionServiceImpl.superAndroidVersion = getInteger(mobileSuperVersion1.getVersionNum());
            } else {
                MobileVersionServiceImpl.superAndroidVersion = 0;
            }

        } else if (MobileVersionType.ios.getValue().equals(mobileVersion.getVersionType())) {
            redisUtil.publish("msg.app", "ios_version_change");
            MobileVersion mobileMaxVersion1 =  selectMaxVersion(MobileVersionType.ios.getValue());
            MobileVersion mobileSuperVersion1 =  getSuperVersion(MobileVersionType.ios.getValue());

            if (mobileMaxVersion1 != null) {
                MobileVersionServiceImpl.maxIosVersion = getInteger(mobileMaxVersion1.getVersionNum());
            } else {
                MobileVersionServiceImpl.maxIosVersion = 0;
            }

            if (mobileSuperVersion1 != null) {
                MobileVersionServiceImpl.superIosVersion = getInteger(mobileSuperVersion1.getVersionNum());
            } else {
                MobileVersionServiceImpl.superIosVersion = 0;
            }
        }
    }

    public int getInteger(String value) {
        int v = 0 ;
        if (value!= null && !StringUtils.isEmpty(value)) {
            try {
                v = Integer.parseInt(value.replace(".", ""));
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return v;
    }


    @Override
    @Transactional(readOnly = true)
    public MobileVersion selectMaxVersion(String versionType) {
        return mobileVersionMapper.selectMaxVersion(versionType);
    }
    @Override
    @Transactional(readOnly = true)
    public MobileVersion getSuperVersion(String versionType){
        return mobileVersionMapper.getSuperVersion(versionType);
    }

    @Override
    public boolean isCanSend(UserMachineInfo umi) {
        if (umi == null) {
            return false;
        }
        int cannotUpdateVersion = getInteger(umi.getConnotUpdateVersion());
        int version = getInteger(umi.getVersion());

        if (umi != null) {
            if ("android".equals(umi.getMachineType() )
                    && cannotUpdateVersion < superAndroidVersion
                    && version < superAndroidVersion) {
                return true;
            } else if ("ios".equals(umi.getMachineType() )
                    && cannotUpdateVersion < superIosVersion
                    && version < superIosVersion) {
                return true;
            }

        }
        return false;
    }
}