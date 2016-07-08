package com.zhihao.spider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class SpiderConfigs {

	public static int WORKER_NUM = 1;
	public static int DEYLAY_TIME = 1000;
	
	public static final String DB_SCHEMA = "";
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_USERNAME = "root";
	public static final String DB_PASSWORD = "123";
	//url队列的最大值
	public static final long MAX_QUEUE = 3;
	
	public static String redisUrl;
	public static String redisPort;
	public static String jdbcUrl;
	public static ArrayList<String> seedUrls = new ArrayList<String>();
	public static ArrayList<Integer> seedSpiderNums = new ArrayList<Integer>();  
	
	static {   
        Properties prop = new Properties();   
        InputStream in = Object.class.getResourceAsStream("/config.properties");   
        try {   
            prop.load(in);   
            redisUrl = prop.getProperty("redisUrl").trim();   
            redisPort = prop.getProperty("redisPort").trim(); 
            jdbcUrl = prop.getProperty("jdbcUrl").trim(); 
            //从配置中读取 seed url
            readSeedUrls(prop);
            
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }  
	
	public static void readSeedUrls( Properties prop){
		String temp1 = prop.getProperty("url");
    	if(temp1!=null && temp1!=""){
    		seedUrls.add(temp1);
    		String tempNum =  prop.getProperty("num");
    		if(tempNum!=null && tempNum!=""){
    			seedSpiderNums.add(Integer.parseInt(tempNum));
    		}
    	}
        for(int i=0;i<=20;i++){
        	String temp2 = prop.getProperty("url"+i);
        	if(temp2!=null && temp2!=""){
        		seedUrls.add(temp2);
        		String tempNum =  prop.getProperty("num"+i);
        		if(tempNum!=null && tempNum!=""){
        			seedSpiderNums.add(Integer.parseInt(tempNum));
        		}
        	}
        }
	}
}
