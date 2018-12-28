package com.longlian.live.dao;

import com.longlian.model.InviCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by admin on 2017/2/15.
 */
public interface InviCardMapper {

    InviCard findOneCardByCode(@Param("code")String code,@Param("type")String type);

    List<InviCard> getCourseOrRoomCard(String type);


}
