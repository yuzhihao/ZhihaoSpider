package com.zhihao.spider;

import com.zhihao.spider.paser.DoubanParser;
import com.zhihao.spider.paser.HtmlParser;

public class DoubanSpider extends CommonSpider{

	private String urlPattern = "";
	
	public DoubanSpider(int index) {
		super(index);
		parser = new DoubanParser(store);//parser中需要用到DataStorage
		
	}
	
	

}
