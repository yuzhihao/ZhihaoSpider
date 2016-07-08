package com.zhihao.spider;


import java.util.Observable;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.zhihao.spider.checker.ContentChecker;
import com.zhihao.spider.downloader.Downloader;
import com.zhihao.spider.model.HtmlPage;
import com.zhihao.spider.model.InnerMsg;
import com.zhihao.spider.paser.HtmlParser;
import com.zhihao.spider.queue.UrlQueue;
import com.zhihao.spider.storage.DataStorage;





/**
 * 爬虫工作线程 抽象类
 * @author dell1
 *
 */
public abstract class Worker extends Observable implements Runnable{
	private static final Logger logger = Logger.getLogger(Worker.class.getName());
	
	protected int threadIndex = 0;
	//private UrlQueue urlQueue;
	protected Downloader downloader;
	protected HtmlParser parser;
	protected DataStorage store;
	protected ContentChecker checker;
	
	public Worker(){
		init();
	}
	
	public Worker(int index ){
		threadIndex = index;
		init();
	}
	
	private void init(){
		downloader = new Downloader();
		store = new DataStorage();
		//HtmlParser的初始化放在子类中进行
		
		checker = new ContentChecker();
	}
	
	public void setThreadIndex(int index){
		threadIndex = index;
	}
	
	/**
	 * 采用监听者模式，发送听消息给监听者
	 */
	public void sendMessage(String msg){
		setChanged();
		logger.debug("---------------------sendMessage to sheduler");
		notifyObservers(new InnerMsg(threadIndex,msg));
	}
	
	
	
	@Override
	public void run() {
		try{
			crawl();
		}catch(Exception e){
			Spider.logger.error("crawl exception");
			sendMessage("crawl exception");
			e.printStackTrace();
		}finally{
			//run();
		}
		
	}
	protected void crawl(){
		// 登录
		

		// 当待抓取URL队列不为空时，执行爬取任务
		// 注： 当队列内容为空时，也不爬取任务已经结束了
		//     因为有可能是UrlQueue暂时空，其他worker线程还没有将新的URL放入队列
		//	        所以，这里可以做个等待时间，再进行抓取（二次机会）
		while(!UrlQueue.isEmpty()){
			// 从待抓取队列中拿URL
			String url = (String) UrlQueue.deQueue();
			
			// 抓取URL指定的页面，并返回状态码和页面内容构成的FetchedPage对象
			HtmlPage htmlPage = downloader.getHtmpPageFromUrl(url);
			if(htmlPage==null){
				logger.debug("fetchPage get null");
				continue;
			}
			// 检查爬取页面的合法性，爬虫是否被禁止
			if(!checker.check(htmlPage)){
				// 切换IP等操作
				// TODO
				
				//Log.info("Spider-" + threadIndex + ": switch IP to ");
				continue;
			}
			
			// 解析页面，获取目标数据
			Object targetData = parser.parse(htmlPage);
			Spider.logger.debug(targetData.toString());
			
			// 存储目标数据到数据存储（如DB）、存储已爬取的Url到VisitedUrlQueue
			//store.store(targetData);
			
			// delay
			try {
				Thread.sleep(SpiderConfigs.DEYLAY_TIME);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	abstract protected boolean checkUrl(String url);

}
