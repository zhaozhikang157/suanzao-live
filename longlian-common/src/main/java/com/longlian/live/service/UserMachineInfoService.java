package com.longlian.live.service;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.dto.UserMachineInfoDto;
import com.longlian.model.UserMachineInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by liuhan on 2017-06-11.
 */
public interface UserMachineInfoService {

    public void saveUserMachineInfo(UserMachineInfo info);

    List<UserMachineInfoDto> getListPage(DatagridRequestModel datagridRequestModel, UserMachineInfoDto userMachineInfo);

    public UserMachineInfo getUserMachineInfo(Long id);

}
