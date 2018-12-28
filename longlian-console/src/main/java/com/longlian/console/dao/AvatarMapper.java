package com.longlian.console.dao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.AppUser;
import com.longlian.model.Avatar;
import com.longlian.model.CourseAvatarUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by admin on 2017/6/15.
 */
public interface AvatarMapper {

    void insertAcatar(List<Avatar> list);

    List<Avatar> findPthotoByName(String name);


}
