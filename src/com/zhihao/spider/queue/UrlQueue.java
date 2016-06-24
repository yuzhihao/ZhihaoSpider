package com.zhihao.spider.queue;

import java.util.LinkedList;

import com.zhihao.spider.SpiderConfigs;

public class UrlQueue {

	private static LinkedList<Object>  queue = new LinkedList<Object>();
	
	public static synchronized  void enQueue(Object t){
		//判断queue队列是否超过限制
		if(queue.size() > SpiderConfigs.MAX_QUEUE){
			System.out.println("多出url，抛弃掉了");
			return ;
		}
		queue.add(t);
	}
	
	public static synchronized void addFirstElement(Object o) {
		queue.addFirst(o);
	}
	
	public static synchronized Object deQueue(){
		return queue.removeFirst();
	}
	
	public static synchronized boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public static synchronized int size(){
		return queue.size();
	}
	
	public static synchronized boolean isContains(Object o){
		return queue.contains(o);
	}


}
