package com.huaxin.util.page;

/**
 * 数据列表request模型
 * Created by kakalion on 2016-1-10.
 */
public class DatagridRequestModel<T> {
    private int limit = 10;//分页大小
    private int offset = 0;//偏移量
    private String sort = null;// 排序字段名
    private String order = "";// 按什么排序(asc,desc)
    private int total = 0;//返回的总记录数

    private boolean isCloseAutoSelectPageCount = false;//是否关闭获取分页数量，默认false

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isCloseAutoSelectPageCount() {
        return isCloseAutoSelectPageCount;
    }

    public void setIsCloseAutoSelectPageCount(boolean isCloseAutoSelectPageCount) {
        this.isCloseAutoSelectPageCount = isCloseAutoSelectPageCount;
    }


}
