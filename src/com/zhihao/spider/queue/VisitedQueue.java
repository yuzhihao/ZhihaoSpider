package com.zhihao.spider.queue;

import java.util.LinkedList;

import com.zhihao.spider.SpiderConfigs;

public class VisitedQueue {
	private static LinkedList<Object> visitedQueue = new LinkedList<Object>();
	
	public static synchronized boolean enQueue(Object t){
		if(visitedQueue.size()>SpiderConfigs.MAX_QUEUE){
			return false;
		}else{
			visitedQueue.add(t);
			return true;	
		}
	}
	
	public static synchronized Object deQueue(){
		return visitedQueue.removeFirst();
	}
	
	public static synchronized boolean isEmpty(){
		return visitedQueue.isEmpty();
	}
	
	public static synchronized int size(){
		return visitedQueue.size();
	}
	
	public static synchronized boolean isContains(Object o){
		return visitedQueue.contains(o);
	}
}
