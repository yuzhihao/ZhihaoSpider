package com.zhihao.spider;

import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

import com.zhihao.spider.queue.UrlQueue;
import com.zhihao.spider.storage.DataStorage;
import com.zhihao.spider.storage.SqlHelper;

//主线程
public class Spider {
	public static final Logger logger = Logger.getLogger("Spider ");
	/*
	 * 
	 * 错误修复
	 * 
	 */
	
	
	
	public static void main(String[] args){
		logger.debug("test"+ SpiderConfigs.jdbcUrl);
		//初始化配置参数
		initParam();
		//初始化爬取队列
		initQueue();
		Scheduler scheduler = new Scheduler();
		//scheduler.start();
		testRedis();
	}
	
	private static void testRedis(){
		
	}
	
	private static void initParam(){
		//初始化SqlHelper工具类
		//SqlHelper.getConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/yuzhihao", "root", "123");
		//System.out.println("connection built");
	}
	
	private static void initQueue(){
		UrlQueue.enQueue("https://www.douban.com/");
		
	}

	

	
}

