package com.zhihao.spider.model;

import java.util.Date;

public class VisitedUrl {
	private int id;
	private String url;
	private Date date;
	
	public VisitedUrl(String url){
		this.url = url;
		date = new Date();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
