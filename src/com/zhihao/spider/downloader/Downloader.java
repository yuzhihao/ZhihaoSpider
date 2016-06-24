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
		// ���ó�ʱʱ��
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
	 * �����ر�HttpClient����
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
	 * ����url��ȡ��ҳ����
	 * @param url
	 * @return
	 */
	public HtmlPage getHtmpPageFromUrl(String url){
		String content = null;
		int statusCode = 500;
		
		// ����Get���󣬲�����Header
		HttpGet getHttp = new HttpGet(url);	
		System.out.println("executing request " + getHttp.getURI());
		getHttp.setConfig(requestConfig);
		getHttp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		
		
		try{
			// �����Ϣ����
			response = client.execute(getHttp);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			
			if(entity != null){
				// ת��Ϊ�ı���Ϣ, ������ȡ��ҳ���ַ�������ֹ����
				content = EntityUtils.toString(entity, "UTF-8");
			}
		}catch(Exception e){
			e.printStackTrace();
			
			// ������ʱ������������쳣����URL�Żش�ץȡ���У�������ȡ
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