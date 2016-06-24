package com.zhihao.spider.paser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.zhihao.spider.model.HtmlPage;
import com.zhihao.spider.queue.UrlQueue;
import com.zhihao.spider.queue.VisitedQueue;
import com.zhihao.spider.storage.DataStorage;


public class HtmlParser {
	
	private DataStorage store;
	
	public HtmlParser(DataStorage store){
		this.store = store;
	}
	public Object parse(HtmlPage htmlPage) {
		Object targetObject = null;
		Document doc = Jsoup.parse(htmlPage.getContent());
		obtainLinks(doc);
		// 如果当前页面包含目标数据
		if(containsTargetData(htmlPage.getUrl(), doc)){
			// 解析并获取目标数据
			// TODO
		}
		
		// 将URL放入已爬取队列
		// 这里VisitedQueue相当于一个缓存，visited url首先是放在Queue中，如果queue满了，就按FIFO的方式，将溢出的url存入到远程数据库中
		if(!VisitedQueue.enQueue(htmlPage.getUrl())){
			store.storeVisitedUrl((String) VisitedQueue.deQueue());
			VisitedQueue.enQueue(htmlPage.getUrl());
		}
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return targetObject; 
	}

	private boolean containsTargetData(String url, Document contentDoc){
		// 通过URL判断
		// TODO
		
		
		// 通过content判断，比如需要抓取class为grid_view中的内容
		if(contentDoc.getElementsByClass("grid_view") != null){
			//System.out.println(contentDoc.getElementsByClass("grid_view").toString());
			return true;
		}
		
		return false;
		
	}
	
	//解析出html中的所有link,筛选过后，放入到UrlQueue中
	private void obtainLinks(Document doc){
		Elements links = doc.select("a[href]");
		Elements media = doc.select("[src]");
		//Elements imports = doc.select("link[href]");
		for(Element link: links){
			//判断该link是否存在抓去价值
			if(link.attr("abs:href")==null || !link.attr("abs:href").startsWith("http://")){
				//还可以继续判断
				
				continue;
			}
			System.out.println("获取link: " + link.attr("abs:href") +"  " +link.attr("rel"));
			//检查该link是存在于已抓取队列
			if(VisitedQueue.isContains(link.attr("abs:href")) /*|| store.isUrlVisited(link.attr("abs:href"))*/){
				
			} else {
				//判断是否过滤
				
				//将link放入到待抓取队列
				UrlQueue.enQueue(link.attr("abs:href"));
			}
			
			
		}
		
		/*for (Element src : media) {
            if (src.tagName().equals("img")){
            	System.out.println(src.tagName());
            	System.out.println(src.attr("abs:src"));
            	System.out.println(src.attr("width"));
            	System.out.println(src.attr("height"));
            	System.out.println(src.attr("alt"));
            }
            else{
            	System.out.println(src.tagName() + src.attr("abs:src"));
            }
        }*/
	}
}
