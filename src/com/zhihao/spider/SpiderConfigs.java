package com.zhihao.spider;

import java.io.IOException;
import java.io.InputStream;
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
	
	static {   
        Properties prop = new Properties();   
        InputStream in = Object.class.getResourceAsStream("/config.properties");   
        try {   
            prop.load(in);   
            redisUrl = prop.getProperty("redisUrl").trim();   
            redisPort = prop.getProperty("redisPort").trim(); 
            jdbcUrl = prop.getProperty("jdbcUrl").trim(); 
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
}
