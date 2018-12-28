package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.MobileVersionDto;
import com.longlian.model.MobileVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/1/22.
 */
public interface MobileVersionMapper {

    /**
     *查询列表
     *
     * @param mobileVersion page
     * @return
     */
    List<MobileVersionDto> getListPage(@Param(value = "page") DatagridRequestModel page, @Param(value = "mobileVersion") MobileVersionDto mobileVersion);
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    MobileVersion findById(@Param(value = "id") long id);
    /**
     * 通过版本类型修改
     *
     * @param versionType
     * @return
     */
    void updateByVersionType(@Param(value = "versionType") String versionType);


    /**
     * 选择下线添加
     *
     * @param mobileVersion
     * @return
     */
    int create(MobileVersion mobileVersion);
    /**
     * 选择上线线添加
     *
     * @param mobileVersion
     * @return
     */
    int createOnline(MobileVersion mobileVersion);
    /**
     * 修改
     *
     * @param mobileVersion
     * @return
     */
    int update(MobileVersion mobileVersion);

    MobileVersion selectMaxVersion(@Param(value = "versionType") String versionType);

    /**
     * 查询最大版本下载地址
     * @return
     */
    public  MobileVersion getSuperVersion(@Param(value = "versionType") String versionType);
}
