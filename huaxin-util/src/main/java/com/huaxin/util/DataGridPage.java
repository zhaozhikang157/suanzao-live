package com.huaxin.util;

import com.huaxin.util.page.DatagridRequestModel;

import java.util.List;

/**
 * 分页参数类
 * 
 */
public class DataGridPage extends DatagridRequestModel {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private int pageSize;//// 每页显示行数
    private int currentPage;//当前页数
    private List rows;//记录数
    private int prePage;//前一页
    private int nextPage;//下一页
    private boolean hasPrev = false;// 有无上一页
    private boolean hasNext = false;// 有无下一页
    private long firstResult = 0;// 本页起始行号
    private long lastResult = 0;// 本页结束行号
    private int totalPage;//总页数
    private int totalCount;//总记录数
    private boolean isCloseAutoSelectPageCount = false;//是否开启自动获取数据库分页总数

    public DataGridPage() {
        this.currentPage = 1;
        this.totalPage = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     * 
     * @param currentPage
     * @param pageSize
     */
    public DataGridPage(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    /**
     * 设置分页数据
     * @param totalCount
     */
    public void setPageParm(int  totalCount){
        int totalPage = totalCount / this.pageSize+ ((totalCount % this.pageSize == 0) ? 0 : 1);
        this.totalPage = totalPage;
        if(this.currentPage > totalPage){
            this.currentPage = totalPage;
        }
    }


    public void turnToPage(int pageIndex) {
        if (pageIndex < 1)
            currentPage = 1;
        else if (pageIndex > totalPage)
            currentPage = totalPage;
        else
            currentPage = pageIndex;

        hasPrev = (currentPage != 1);
        hasNext = (currentPage != totalPage);

       // currentPage = Math.max(1, currentPage - pageIndexNum / 2);
       // endPageIndex = Math.min(currentPage + (pageIndexNum - pageIndexNum / 2 - 1), totalPage);

        firstResult = (currentPage - 1) * pageSize;
        lastResult = Math.min(currentPage * pageSize, totalCount);

    }
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public static int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(long firstResult) {
        this.firstResult = firstResult;
    }

    public long getLastResult() {
        return lastResult;
    }

    public void setLastResult(long lastResult) {
        this.lastResult = lastResult;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public boolean isCloseAutoSelectPageCount() {
        return isCloseAutoSelectPageCount;
    }

    public void setIsCloseAutoSelectPageCount(boolean isCloseAutoSelectPageCount) {
        this.isCloseAutoSelectPageCount = isCloseAutoSelectPageCount;
    }
}
