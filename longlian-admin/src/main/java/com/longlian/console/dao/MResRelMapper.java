package com.longlian.console.dao;

import com.longlian.model.MResRel;
import org.apache.ibatis.annotations.Param;

public interface MResRelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MResRel record);

    int insertSelective(MResRel record);

    MResRel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MResRel record);

    int updateByPrimaryKey(MResRel record);

    void updateRelId(MResRel mResRel);

    MResRel findRelId(Long resId);

    String findRoleName(Long resId);

    /**
     * 根据角色ID删除
     * @param relId
     */
    void deleteByRelId(@Param("roleId") Long relId);

    void deleteByResIdRelId(MResRel mResRel);

}
