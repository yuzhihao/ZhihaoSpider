package com.zhihao.spider.storage;

import java.util.HashMap;
import java.util.Map;

import com.zhihao.spider.SpiderConfigs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisHelper {
	
	JedisPoolConfig config = new JedisPoolConfig();  
	public static JedisPool pool;
	
	RedisHelper(){
		pool = new JedisPool(config, SpiderConfigs.redisUrl,Integer.parseInt(SpiderConfigs.redisPort));
	}
	
	public static  Jedis getRedisAdapter(){
		Jedis jedis = pool.getResource(); 
		return jedis;
	}
	
	public static boolean set(Map<String,String> map){
		
		return true;
	}
	
	private void init(){
		
		Jedis jedis = pool.getResource(); 
		try { 
		   //随便做一些对于redis的操作 
		   //jedis.set("foo", "bar"); 
		  // String foobar = jedis.get("foo"); 
		   //jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike");  
		  // Set<String> sose = jedis.zrange("sose", 0, -1); 
			Map<String, String>  capital = new HashMap<String, String>();
			capital.put("shannxi", "xi'an");
			capital.put("henan", "zhengzhou");
			jedis.hmset("capital", capital);
			
		   System.out.println("");
		 } finally { 
		   //这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中 
		   pool.returnResource(jedis); 
		 } 
		 //程序关闭时，需要调用关闭方法 
		 pool.destroy();
	}
}
