package com.zhihao.spider;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.zhihao.spider.model.InnerMsg;

public class Scheduler implements Observer{
	
	private static final Logger logger = Logger.getLogger(Scheduler.class.getName());
	
	/**
	 * Spider线城池
	 */
	ExecutorService pool;
	
	/**
	 * Spider 数组
	 */
	ArrayList<CommonSpider> spiderList;

	
	public Scheduler(){
		pool = Executors.newCachedThreadPool();
		//初始化Spider数组
		initSpiderList();
		logger.debug("----------------初始化完成");
		
	}

	private void initSpiderList(){
		spiderList =  new ArrayList<CommonSpider>();
		//从配置中读取 
		spiderList.add(new CommonSpider(0)); //参数为 threadIndex
		
	}
	
	@Override
	public void update(Observable o, Object arg) {
		logger.debug("------------------------- Observer响应, 准备新建 新的thread");
		try{
			if(o instanceof CommonSpider){
				InnerMsg msg = (InnerMsg)arg;
				CommonSpider oldSpider = spiderList.get(msg.code);
				//通过oldSpider判断要建何种spider
				
				CommonSpider newSpider = new CommonSpider(msg.code);
				newSpider.addObserver(this);
				spiderList.set(msg.code, newSpider);
				pool.execute(newSpider);
			}
		}catch(Exception e){
			
		}
	}
	
	/**
	 * 整个爬虫的入口
	 */
	public void start(){
		try{
			//
			for(int i=0;i<spiderList.size();i++){
				spiderList.get(i).addObserver(this);
				pool.execute(spiderList.get(i));	
			}
			
			while(true){
				for(CommonSpider temp : spiderList){
			
				}
				Thread.sleep(2000);	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			pool.shutdown();
		}
	}



}
