package com.longlian.console.dao;

import com.longlian.model.Avatar;

import java.util.List;

/**
 * Created by admin on 2017/6/15.
 */
public interface AvatarMapper {

    void insertAcatar(List<Avatar> list);

    List<Avatar> findPthotoByName(String name);


}
