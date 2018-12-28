package com.longlian.console.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.DataGridPage;
import com.longlian.dto.AppUserDto;
import com.longlian.model.AppUser;
import com.longlian.token.ConsoleUserIdentity;

import java.util.List;

/**
 * Created by admin on 2017/6/15.
 */
public interface AvatarService {

    void uploadUrl(String path, String fileName) throws Exception;

    ActResult batchImportAvatarUser(String excelAddress, String fileName, ConsoleUserIdentity userIdentity)throws  Exception;

    List<AppUserDto> findAllAvatarPage(DataGridPage dataGridPage, String name, String isInRoom, Long courseId);

    public String createYunxinToken(AppUser appUser);

    ActResult addRobot(String courseId, Long count) throws Exception;

    ActResult removeRoboot(String courseId, Long count);

    ActResult removeRobootByUserId(String courseId, Long userId, boolean isDeleteDB);

    public void dealYunxinToken();

    ActResult updateAvatar(AppUser appUser);
}
