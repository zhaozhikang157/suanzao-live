package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.AdvertisingDisplay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by pangchao on 2017/1/23.
 */
public interface AdvertisingDisplayMapper {

    /**
     * 全查
     *
     * @param advertising
     * @return
     */
    List<AdvertisingDisplay> getListPage(@Param(value = "page") DatagridRequestModel page, @Param(value = "advertising") AdvertisingDisplay advertising);

    /**
     * APP缓存数据
     * @return
     */
    List<AdvertisingDisplay> getList4App();
    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(@Param(value = "id") long id);

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    AdvertisingDisplay findById(@Param(value = "id") long id);

    /**
     * 添加
     *
     * @param advertising
     * @return
     */
    int create(AdvertisingDisplay advertising);

    /**
     * 修改
     *
     * @param advertising
     * @return
     */
    int update(AdvertisingDisplay advertising);
}
