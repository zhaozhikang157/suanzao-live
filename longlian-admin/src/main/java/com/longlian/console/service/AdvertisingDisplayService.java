package com.longlian.console.service;

import com.huaxin.util.ActResult;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.longlian.model.AdvertisingDisplay;

/**
 * Created by pangchao on 2017/1/23.
 */
public interface AdvertisingDisplayService {

    /**
     * 分页全查:营业类型
     *
     * @param requestModel advertising
     * @return
     */
    DatagridResponseModel getListPage(DatagridRequestModel requestModel, AdvertisingDisplay advertising);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    void deleteById(long id) throws Exception;

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    AdvertisingDisplay findById(long id);

    /**
     * 修改或删除
     *
     * @param advertising
     * @return
     */

    ActResult doSaveAndUpdate(AdvertisingDisplay advertising) throws Exception;
}
