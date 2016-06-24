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
 * ���湤���߳�
 * @author dell1
 *
 */
public abstract class Worker extends Observable implements Runnable{
	private static final Logger logger = Logger.getLogger(Worker.class.getName());
	private int threadIndex = 0;
	//private UrlQueue urlQueue;
	private Downloader downloader;
	private HtmlParser parser;
	private DataStorage store;
	private ContentChecker checker;
	
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
		parser = new HtmlParser(store);//parser����Ҫ�õ�DataStorage
		checker = new ContentChecker();
	}
	
	public void setThreadIndex(int index){
		threadIndex = index;
	}
	
	/**
	 * ���ü�����ģʽ����������Ϣ��������
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
		}finally{
			//run();
		}
		
	}
	protected void crawl(){
		// ��¼
		

		// ����ץȡURL���в�Ϊ��ʱ��ִ����ȡ����
		// ע�� ����������Ϊ��ʱ��Ҳ����ȡ�����Ѿ�������
		//     ��Ϊ�п�����UrlQueue��ʱ�գ�����worker�̻߳�û�н��µ�URL�������
		//	        ���ԣ�������������ȴ�ʱ�䣬�ٽ���ץȡ�����λ��ᣩ
		while(!UrlQueue.isEmpty()){
			// �Ӵ�ץȡ��������URL
			String url = (String) UrlQueue.deQueue();
			
			// ץȡURLָ����ҳ�棬������״̬���ҳ�����ݹ��ɵ�FetchedPage����
			HtmlPage htmlPage = downloader.getHtmpPageFromUrl(url);
			
			// �����ȡҳ��ĺϷ��ԣ������Ƿ񱻽�ֹ
			if(!checker.check(htmlPage)){
				// �л�IP�Ȳ���
				// TODO
				
				//Log.info("Spider-" + threadIndex + ": switch IP to ");
				continue;
			}
			
			// ����ҳ�棬��ȡĿ������
			Object targetData = parser.parse(htmlPage);
			Spider.logger.debug(targetData.toString());
			
			// �洢Ŀ�����ݵ����ݴ洢����DB�����洢����ȡ��Url��VisitedUrlQueue
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

}