package com.longlian.live.page;

/**
 * @ClassName: WebPageTool 
 * @Description: 分页工具
 * @author: zhangzhipeng
 * @date: 2016年7月6日 下午1:27:21
 */
public class WebPageTool {
	public static PageIndex getPageIndex(int currentPage,long totalPage,long pageCode){
		long firstIndex=1;
		long endIndex=pageCode;
	    if(currentPage>endIndex/2+1&&totalPage>pageCode){
	    	if(currentPage+5>totalPage){
	    		firstIndex=totalPage-(endIndex-1);
	    	}else{
	    	firstIndex=currentPage-endIndex/2;
	    	}
	        endIndex=currentPage+endIndex/2;
	    }
	    if(endIndex>=totalPage){
	    	endIndex=totalPage;
	    }
//		long startpage = currenPage-(viewpagecount%2==0? viewpagecount/2-1 : viewpagecount/2);
//		long endpage = currenPage+viewpagecount/2;
//		if(startpage<1){
//			startpage = 1;
//			if(totalpage>=viewpagecount) endpage = viewpagecount;
//			else endpage = totalpage;
//		}
//		if(endpage>totalpage){
//			endpage = totalpage;
//			if((endpage-viewpagecount)>0) startpage = endpage-viewpagecount+1;
//			else startpage = 1;
//		}
//		return new PageIndex(startpage, endpage);		
//  }
	    return new PageIndex(firstIndex,endIndex);
}
}