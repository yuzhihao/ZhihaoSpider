package com.zhihao.spider.model;

/**
 * 内部用消息体
 * @author yuzhihao
 *
 */
public class InnerMsg {
	public int code;
	public String msg;
	public Object obj;
	
	public InnerMsg(int code,String msg){
		this.code = code;
		this.msg = msg;
	}
}
