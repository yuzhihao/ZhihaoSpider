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
	 * Spider�߳ǳ�
	 */
	ExecutorService pool;
	
	/**
	 * Spider ����
	 */
	ArrayList<CommonSpider> spiderList;

	
	public Scheduler(){
		pool = Executors.newCachedThreadPool();
		//��ʼ��Spider����
		initSpiderList();
		logger.debug("----------------��ʼ�����");
		
	}

	private void initSpiderList(){
		spiderList =  new ArrayList<CommonSpider>();
		//�������ж�ȡ 
		spiderList.add(new CommonSpider(0)); //����Ϊ threadIndex
		
	}
	
	@Override
	public void update(Observable o, Object arg) {
		logger.debug("------------------------- Observer��Ӧ, ׼���½� �µ�thread");
		try{
			if(o instanceof CommonSpider){
				InnerMsg msg = (InnerMsg)arg;
				CommonSpider oldSpider = spiderList.get(msg.code);
				//ͨ��oldSpider�ж�Ҫ������spider
				
				CommonSpider newSpider = new CommonSpider(msg.code);
				newSpider.addObserver(this);
				spiderList.set(msg.code, newSpider);
				pool.execute(newSpider);
			}
		}catch(Exception e){
			
		}
	}
	
	/**
	 * ������������
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