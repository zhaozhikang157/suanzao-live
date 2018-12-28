package com.huaxin.util.page;

public class PageUtil {

	public static final int DEFAULT_PAGE_SIZE = 20;// 默认每页显示行数
	public static final int DEFAULT_PAGE_INDEX_NUM = 20;// 默认页号数目

	private long totalRowNum = 0;// 总行数

	private int pageSize = DEFAULT_PAGE_SIZE;// 每页显示行数
	private int currentPageIndex = 1;// 当前页

	private int totalPageNum = 1;// 总页数

	private int beginPageIndex = 1;// 起始页

	private int endPageIndex = 1;// 结束页

	private boolean hasPrev = false;// 有无上一页

	private boolean hasNext = false;// 有无下一页

	private long firstResult = 0;// 本页起始行号
	private long lastResult = 0;// 本页结束行号
	private int pageIndexNum = DEFAULT_PAGE_INDEX_NUM;// 页号数目


	/**
	 * 获取索引  暂不起作用
	 * @param total
	 * @param pageNo
	 * @return
	 */
	public  int[] getIndex(int total,int pageNo) {
		int firstResult = (pageNo-1)* pageSize;
		int totalPages = 1;
		if ((total % pageSize) == 0) {
			totalPages = total / pageSize;
		} else {
			totalPages = total / pageSize + 1;
		}
		int maxResults = pageSize;
		if(pageNo>=totalPages){
			maxResults = total-pageSize*(totalPages-1);
		}
		if(total == 0){
			maxResults = total;
		}
		int lastResults = firstResult + maxResults;
		return new int[] {firstResult, lastResults};
	}

	/**
	 * 调用此文件构造方法
	 *
	 * @param pageSize
	 *          一个页面显示的数目
	 * @param count
	 *          查找总数据
	 * @param pageIndex
	 *          页号参数
	 */
	public PageUtil(int pageSize, long count, int pageIndex) {
		this.pageSize = pageSize;
		totalRowNum = count;
		resetTotalPageNum();
		turnToPage(pageIndex);
	}

	public void turnToPage(int pageIndex) {
		if (pageIndex < 1)
			currentPageIndex = 1;
		else if (pageIndex > totalPageNum)
			currentPageIndex = totalPageNum;
		else
			currentPageIndex = pageIndex;

		hasPrev = (currentPageIndex != 1);
		hasNext = (currentPageIndex != totalPageNum);

		beginPageIndex = Math.max(1, currentPageIndex - pageIndexNum / 2);
		endPageIndex = Math.min(currentPageIndex + (pageIndexNum - pageIndexNum / 2 - 1), totalPageNum);

		firstResult = (currentPageIndex - 1) * pageSize;
		lastResult = Math.min(currentPageIndex * pageSize, totalRowNum);

	}

	public void resetTotalPageNum() {
		if (totalRowNum == 0)
			totalPageNum = 1;
		else {
			totalPageNum = (int) ((totalRowNum + pageSize - 1) / pageSize);
		}
		turnToPage(1);
	}

	public void search(long count) {
		totalRowNum = count;
		resetTotalPageNum();
	}

	public void changePageSize(int pageSize) {
		this.pageSize = pageSize;
		resetTotalPageNum();
	}

	public void display() {

		//System.out.println(totalRowNum + "条查询结果，以下是第 " + (firstResult + 1) + "-" + lastResult + " 条。");
		StringBuffer sb = new StringBuffer();
		if (hasPrev)
			sb.append("上一页 ");
		for (int i = beginPageIndex; i <= endPageIndex; i++) {
			if (i == currentPageIndex)
				sb.append("[").append(i).append("] ");
			else
				sb.append(i).append(" ");
		}
		if (hasNext)
			sb.append("下一页");
		//System.out.println(sb.toString());
	}

	public PageUtil() {
		super();
	}

	public static void main(String[] args) {
		PageUtil page = new PageUtil(20, 4567, 10);
		page.display();
		page.turnToPage(23);
		page.display();
	}

	public long getTotalRowNum() {
		return totalRowNum;
	}

	public void setTotalRowNum(long totalRowNum) {
		this.totalRowNum = totalRowNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
	}

	public int getTotalPageNum() {
		return totalPageNum;
	}

	public void setTotalPageNum(int totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

	public int getBeginPageIndex() {
		return beginPageIndex;
	}

	public void setBeginPageIndex(int beginPageIndex) {
		this.beginPageIndex = beginPageIndex;
	}

	public int getEndPageIndex() {
		return endPageIndex;
	}

	public void setEndPageIndex(int endPageIndex) {
		this.endPageIndex = endPageIndex;
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

	public int getPageIndexNum() {
		return pageIndexNum;
	}

	public void setPageIndexNum(int pageIndexNum) {
		this.pageIndexNum = pageIndexNum;
	}

}
