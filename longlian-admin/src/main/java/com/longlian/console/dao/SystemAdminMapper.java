package com.longlian.console.dao;

import com.longlian.model.system.SystemAdmin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by admin on 2017/2/14.
 */
public interface SystemAdminMapper {
    /** 插入超级管理员 */
    int insertSystemAdmin(SystemAdmin admin);
    /** 查询超级管理员 */
    List<SystemAdmin> getSystemAdminList();
    /** 删除管理员数据 */
    int deleteSystemAdmin(@Param("id") Long id);
    /** 查询管理员用户是否存在 */
    int findSystemAdminByUserId(@Param("adminId") Long adminId);
}
