package com.longlian.model;

/**
 *
 * tablename:page_url
 */
public class PageUrl {
    /**
     *
     * field:id  column:ID
     * 
     */
    private Long id;

    /**
     * 页面地址
     *
     * field:pageUrl  column:PAGE_URL
     * 
     */
    private String pageUrl;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    private String pageName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl == null ? null : pageUrl.trim();
    }
}