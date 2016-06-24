package com.zhihao.spider.model;



public class HtmlPage {
	private String url;
	private String content;
	private int statusCode;
	
	public HtmlPage(){
		
	}
	
	public HtmlPage(String url, String content, int statusCode){
		this.url = url;
		this.content = content;
		this.statusCode = statusCode;
		
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
