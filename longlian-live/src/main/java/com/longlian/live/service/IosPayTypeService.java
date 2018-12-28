package com.longlian.live.service;

import com.longlian.model.IosPayType;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/19.
 */
public interface IosPayTypeService {

    List<IosPayType> getIosPay(String type);

    IosPayType findPayInfoById(long id,String type);

    IosPayType findIosPayInfoByIosCommodityId(String iosCommodityId);
}
