package com.longlian.live.dao;

import com.longlian.model.IosPayType;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/21.
 */
public interface IosPayTypeMapper {

    List<IosPayType> findAllIosPayInfo(String type);

    IosPayType findPayTypeInfoById(long id);

    IosPayType findIosPayInfo(String iosCommodityId);
}
