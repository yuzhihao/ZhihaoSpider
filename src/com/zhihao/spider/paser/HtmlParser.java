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
		// �����ǰҳ�����Ŀ������
		if(containsTargetData(htmlPage.getUrl(), doc)){
			// ��������ȡĿ������
			// TODO
		}
		
		// ��URL��������ȡ����
		// ����VisitedQueue�൱��һ�����棬visited url�����Ƿ���Queue�У����queue���ˣ��Ͱ�FIFO�ķ�ʽ���������url���뵽Զ�����ݿ���
		if(!VisitedQueue.enQueue(htmlPage.getUrl())){
			store.storeVisitedUrl((String) VisitedQueue.deQueue());
			VisitedQueue.enQueue(htmlPage.getUrl());
		}
		
		// ���ݵ�ǰҳ���URL��ȡ��һ����ȡ��URLs
		// TODO
		
		return targetObject; 
	}

	private boolean containsTargetData(String url, Document contentDoc){
		// ͨ��URL�ж�
		// TODO
		
		
		// ͨ��content�жϣ�������ҪץȡclassΪgrid_view�е�����
		if(contentDoc.getElementsByClass("grid_view") != null){
			//System.out.println(contentDoc.getElementsByClass("grid_view").toString());
			return true;
		}
		
		return false;
		
	}
	
	//������html�е�����link,ɸѡ���󣬷��뵽UrlQueue��
	private void obtainLinks(Document doc){
		Elements links = doc.select("a[href]");
		Elements media = doc.select("[src]");
		//Elements imports = doc.select("link[href]");
		for(Element link: links){
			//�жϸ�link�Ƿ����ץȥ��ֵ
			if(link.attr("abs:href")==null || !link.attr("abs:href").startsWith("http://")){
				//�����Լ����ж�
				
				continue;
			}
			System.out.println("��ȡlink: " + link.attr("abs:href") +"  " +link.attr("rel"));
			//����link�Ǵ�������ץȡ����
			if(VisitedQueue.isContains(link.attr("abs:href")) /*|| store.isUrlVisited(link.attr("abs:href"))*/){
				
			} else {
				//�ж��Ƿ����
				
				//��link���뵽��ץȡ����
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