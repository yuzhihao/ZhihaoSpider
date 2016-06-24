package com.zhihao.spider.checker;

import com.zhihao.spider.model.HtmlPage;
import com.zhihao.spider.queue.UrlQueue;



public class ContentChecker {

	public boolean check(HtmlPage htmlPage) {
		// 如果抓取的页面包含反爬取内容，则将当前URL放入待爬取队列，以便重新爬取
		if(isAntiScratch(htmlPage)){
			UrlQueue.addFirstElement(htmlPage.getUrl());
			return false;
		}
		
		return true;
	}

	
	private boolean isStatusValid(int statusCode){
		if(statusCode >= 200 && statusCode < 400){
			return true;
		}
		return false;
	}
	
	private boolean isAntiScratch(HtmlPage fetchedPage){
		// 403 forbidden
		if((!isStatusValid(fetchedPage.getStatusCode())) && fetchedPage.getStatusCode() == 403){
			return true;
		}
		
		// 页面内容包含的反爬取内容
		if(fetchedPage.getContent().contains("<div>禁止访问</div>")){
			return true;
		}
		
		return false;
	}
}
