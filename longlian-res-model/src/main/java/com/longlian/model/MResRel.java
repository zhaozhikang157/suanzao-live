package com.longlian.model;

import java.io.Serializable;

/**
 *
 * tablename:m_res_rel
 */
public class MResRel implements Serializable {
    /**
     *
     * field:id  column:id
     * 
     */
    private Long id;

    /**
     *
     * field:resId  column:res_id
     * 
     */
    private Long resId;

    /**
     *
     * field:relId  column:rel_id
     * 
     */
    private Long relId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }
}