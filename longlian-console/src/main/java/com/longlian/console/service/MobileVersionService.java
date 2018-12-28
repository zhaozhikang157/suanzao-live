package com.longlian.console.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.dto.MobileVersionDto;
import com.longlian.model.MobileVersion;
import com.longlian.model.UserMachineInfo;

/**
 * Created by pangchao on 2017/1/22.
 */
public interface MobileVersionService {

    /**
     * 分页全查:手机版本
     *
     * @param requestModel
     * @param mobileVersion
     * @return
     */
    DatagridResponseModel getListPage(DatagridRequestModel requestModel, MobileVersionDto mobileVersion);


    /**
     * 通过id查询
     *
     * @param
     * @param id
     * @return
     */
    MobileVersion findById(long id) throws Exception;


    /**
     * 修改或添加
     *
     * @param
     * @param mobileVersion
     * @return
     */

    void doSaveAndUpdate( MobileVersion mobileVersion) throws Exception;


    MobileVersion selectMaxVersion(String versionType);

    public MobileVersion getSuperVersion(String versionType);


    public boolean isCanSend(UserMachineInfo umi );
}
