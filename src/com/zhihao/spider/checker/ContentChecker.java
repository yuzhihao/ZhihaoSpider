package com.zhihao.spider.checker;

import com.zhihao.spider.model.HtmlPage;
import com.zhihao.spider.queue.UrlQueue;



public class ContentChecker {

	public boolean check(HtmlPage htmlPage) {
		// ���ץȡ��ҳ���������ȡ���ݣ��򽫵�ǰURL�������ȡ���У��Ա�������ȡ
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
		
		// ҳ�����ݰ����ķ���ȡ����
		if(fetchedPage.getContent().contains("<div>��ֹ����</div>")){
			return true;
		}
		
		return false;
	}
}