package com.longlian.console.service;

import com.huaxin.util.DataGridPage;
import com.longlian.model.IosPayType;

import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
public interface IosPayTypeService {

    List<IosPayType> findIosPayTypeInfoPage(DataGridPage requestModel, IosPayType iosPayType);

    IosPayType findById(long id);

    void updateIosPayType(IosPayType iosPayType);

    void createIosPayType(IosPayType iosPayType);

    void updateStatus(Long id , String status , String type);
}
