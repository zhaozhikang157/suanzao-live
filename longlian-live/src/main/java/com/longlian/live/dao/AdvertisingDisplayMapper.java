package com.longlian.live.dao;

import com.longlian.model.AdvertisingDisplay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by syl on 2017/2/15.
 */
public interface AdvertisingDisplayMapper {
    /**
     * 全查
     * @return
     */
    List<AdvertisingDisplay> getList(@Param("type") String type);
}
