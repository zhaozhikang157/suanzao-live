package com.longlian.console.dao;

import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.IosPayType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
public interface IosPayTypeMapper {

    List<IosPayType> findIosPayTypeInfoPage(@Param("page") DatagridRequestModel requestModel, @Param("iosPayType") IosPayType iosPayType);

    IosPayType findById(long id);

    void updateIosPayType(IosPayType iosPayType);

    void createIosPayType(IosPayType iosPayType);

    void updateStatus(@Param("id") Long id, @Param("status") String status);

    List<IosPayType> findAllPayInfo(String type);


}
