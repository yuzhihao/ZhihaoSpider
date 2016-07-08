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
		if(initSpiderList()){
			logger.info("----------------初始化完成");
		}else{
			logger.info("----------------初始化失败");
		}
		
		
	}

	/**
	 * 初始化Spider实例
	 * @return
	 */
	private boolean initSpiderList(){
		spiderList =  new ArrayList<CommonSpider>();
		//从配置读取spider数量
		if(SpiderConfigs.seedSpiderNums.size()!=SpiderConfigs.seedUrls.size()){
			logger.error("配置文件错误");
			return false;
		}
		//！ 线程的index，实例化Spider时做参数
		int threadIndex = 0;
		//！ 遍历seedUrls
		for(int index=0;index<SpiderConfigs.seedUrls.size();index++){
			String url = SpiderConfigs.seedUrls.get(index);
			int num = SpiderConfigs.seedSpiderNums.get(index);
			for(int j=0;j<num;j++){
				if(url.contains("douban")){
					spiderList.add(new DoubanSpider(threadIndex)); 
					logger.info("---DoubanSpider added");
				}else{
					spiderList.add(new CommonSpider(threadIndex)); 
					logger.info("---CommonSpider added");
				}
				threadIndex ++;
			}
		}
		return true;
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
