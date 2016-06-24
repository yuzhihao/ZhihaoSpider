package com.zhihao.spider.downloader;

import java.io.IOException;

import org.apache.http.HttpEntity;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EntityUtils;

import com.zhihao.spider.model.HtmlPage;
import com.zhihao.spider.queue.UrlQueue;


public class Downloader {

	private CloseableHttpClient client;
	RequestConfig requestConfig;
	CloseableHttpResponse  response; 
	
	public Downloader(){
		// 设置超时时间
		/*HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
	    HttpConnectionParams.setSoTimeout(params, 10 * 1000);	*/
	    requestConfig = RequestConfig.custom()
	            .setSocketTimeout(1000)
	            .setConnectTimeout(1000)
	            .build();
		client = HttpClients.createDefault();
	}
	
	/**
	 * 主动关闭HttpClient连接
	 */
	public void close(){
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据url爬取网页内容
	 * @param url
	 * @return
	 */
	public HtmlPage getHtmpPageFromUrl(String url){
		String content = null;
		int statusCode = 500;
		
		// 创建Get请求，并设置Header
		HttpGet getHttp = new HttpGet(url);	
		System.out.println("executing request " + getHttp.getURI());
		getHttp.setConfig(requestConfig);
		getHttp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		
		
		try{
			// 获得信息载体
			response = client.execute(getHttp);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			
			if(entity != null){
				// 转化为文本信息, 设置爬取网页的字符集，防止乱码
				content = EntityUtils.toString(entity, "UTF-8");
			}
		}catch(Exception e){
			e.printStackTrace();
			
			// 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
			//Log.info(">> Put back url: " + url);
			UrlQueue.enQueue(url);
		}finally{
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new HtmlPage(url,content,statusCode);
	}
}
