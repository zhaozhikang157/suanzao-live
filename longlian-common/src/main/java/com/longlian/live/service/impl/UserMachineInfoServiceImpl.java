package com.longlian.live.service.impl;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.UserMachineInfoDto;
import com.longlian.live.dao.UserMachineInfoMapper;
import com.longlian.model.UserMachineInfo;
import com.longlian.live.service.UserMachineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-06-11.
 */
@Service("userMachineInfoService")
public class UserMachineInfoServiceImpl implements UserMachineInfoService {
    @Autowired
    UserMachineInfoMapper userMachineInfoMapper;
    @Override
    public void saveUserMachineInfo(UserMachineInfo info) {
        userMachineInfoMapper.deleteByUserIdOrMachineCode(info);
        userMachineInfoMapper.insert(info);
    }

    @Override
    public List<UserMachineInfoDto> getListPage(DatagridRequestModel datagridRequestModel, UserMachineInfoDto userMachineInfo) {
        return userMachineInfoMapper.getListPage(datagridRequestModel, userMachineInfo);
    }

    @Override
    public UserMachineInfo getUserMachineInfo(Long id) {
        return userMachineInfoMapper.selectByPrimaryKey(id);
    }
}
