package com.zhihao.spider;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;





import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.zhihao.spider.queue.UrlQueue;
import com.zhihao.spider.storage.DataStorage;
import com.zhihao.spider.storage.RedisHelper;
import com.zhihao.spider.storage.SqlHelper;
import com.zhihao.spider.utils.JsonUtil;

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
		scheduler.start();
		
		//testRegex();
		//testRedis();
	}
	
	private static void testRegex(){
		Pattern pattern = Pattern.compile("^(http://|https://)?(www\\.)?[A-Za-z]+\\.douban\\.com/[A-Za-z]+/[0-9]+(/)?$",Pattern.CASE_INSENSITIVE);
		String url1 = "movie.douban.com/subject/26235349/";
		String url2 = "http://www.movie.douban.com/subject/26235349";
		Matcher m = pattern.matcher(url2);
		if(m.matches()){
			logger.debug("url2 matched");
		};
		m = pattern.matcher(url1);
		if(m.matches()){
			logger.debug("url1 matched");
		};
		
		
		
		
	}
	
	private static void testRedis(){
		String [] test = new String[]{"value值1","value2值","value3值","value值4","value5值","value值6","value值7"};
		/*
		if(RedisHelper.addSet("test3",test)){
			logger.debug("addSet succeed.");
		};*/
		/*if(RedisHelper.existSet("test","value值1" )){
			logger.debug("------------Exist value:");
		};*/
		/*Set<String> temp = RedisHelper.getSet("test");
		logger.debug(JsonUtil.parse(temp));*/
		
		/*List<String> list = RedisHelper.scanSet("test2","*");
		logger.debug("scanResult:size:"+list.size()+"  "+JsonUtil.parse(list));*/
		
		/*String addressSeeds[] = new String[]{"Hollywood基地","厕所","召唤师峡谷","滨海之窗","香缤广场","京基100","USA","秋叶原","长隆欢乐世界","德玛西亚","多拉多","守望之海","艾泽拉斯"};
		Random random = new Random();
		for(int i=0;i<90000;i++){
			if(RedisHelper.HSet("address", random.nextInt(10000)+":"+random.nextInt(10000), addressSeeds[random.nextInt(addressSeeds.length)])){
				//logger.debug("HSet succeed.");
			};
		}*/
		
		logger.debug("key length :"+RedisHelper.getSize("address"));
		
		
		long a=System.currentTimeMillis();
		
		/*List<Entry<String, String>> hList = RedisHelper.scanHSet("address", "100*");
		logger.debug("hscanResult:size:"+hList.size()+"  "+JsonUtil.parse(hList));
		*/
		/*logger.debug("delKey:"+RedisHelper.delKey("address"));*/
		
		if(RedisHelper.addSet("samekey",test)){
			logger.debug("addSet succeed.");
		};
		logger.debug("samekey test getSet:" + JsonUtil.parse(RedisHelper.getSet("samekey")));
		if(RedisHelper.HSet("samekey", "field1", "value1")){
			logger.debug("hset succeed");
		};
		logger.debug("samekey test HGet:" + JsonUtil.parse(RedisHelper.HGet("samekey","field1")));
		logger.debug("samekey test getSet after HSet:" + JsonUtil.parse(RedisHelper.getSet("samekey")));
		
		logger.debug("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
	}
	
	private static void initParam(){
		//初始化SqlHelper工具类
		//SqlHelper.getConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/yuzhihao", "root", "123");
		//System.out.println("connection built");
	}
	
	private static void initQueue(){
		ArrayList<String> seeds = SpiderConfigs.seedUrls;
		logger.debug("seeds size:" + seeds.size());
		for(String url: seeds){
			UrlQueue.enQueue("http://"+ url);
		}
		return;
	}




	
}

