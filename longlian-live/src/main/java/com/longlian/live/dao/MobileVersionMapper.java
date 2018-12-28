package com.longlian.live.dao;

import com.longlian.model.MobileVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MobileVersionMapper {
    MobileVersion selectMaxVersion(@Param(value = "versionType") String versionType);

    List<MobileVersion> selectMaxVersionList(@Param(value = "versionType") String versionType);

    /**
     * 查询最大版本下载地址
     * @return
     */
    public  MobileVersion getSuperVersion(@Param(value = "versionType") String versionType);
}