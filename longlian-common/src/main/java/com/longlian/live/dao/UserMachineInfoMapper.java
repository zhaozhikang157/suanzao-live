package com.longlian.live.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.UserMachineInfoDto;
import com.longlian.model.UserMachineInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface UserMachineInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserMachineInfo record);

    int insertSelective(UserMachineInfo record);

    UserMachineInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserMachineInfo record);

    int updateByPrimaryKey(UserMachineInfo record);

    void deleteByUserIdOrMachineCode(UserMachineInfo info);

    List getListPage(@Param("page") DatagridRequestModel datagridRequestModel,@Param("userMachineInfo") UserMachineInfoDto userMachineInfo);
}