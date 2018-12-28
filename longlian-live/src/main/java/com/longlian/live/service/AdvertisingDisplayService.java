package com.longlian.live.service;

import com.longlian.dto.AdvertisingDisplayDto;
import com.longlian.model.AdvertisingDisplay;

import java.util.List;

/**
 * Created by U on 2016/7/28.
 */
public interface AdvertisingDisplayService {
    List<AdvertisingDisplayDto> getList(String type);
    public void test();
}
