package com.longlian.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * tablename:m_res
 */
public class MRes implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     * field:id  column:id
     * 
     */
    private Long id;

    /**
     *
     * field:name  column:name
     * 
     */
    private String name;

    /**
     *
     * field:parentId  column:parent_id
     * default:0
     * 
     */
    private Long parentId;
    
    private String url;

    /**
     * 002=角色 003=menu  001=人员
     *
     * field:type  column:type
     * 
     */
    private String type;

    private String description; //描述
    private List<MRes> children;

    public List<MRes> getChildren() {
        return children;
    }

    public void setChildren(List<MRes> children) {
        this.children = children;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String resPic;// 菜单显示图片',
    
    public String getResPic() {
        return resPic;
    }

    public void setResPic(String resPic) {
        this.resPic = resPic;
    }

    /**
     *
     * field:sort  column:sort
     * default:0
     * 
     */
    private Integer sort;

    /**
     * 0-正常 -1-删除
     *
     * field:status  column:status
     * default:0
     * 
     */
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}