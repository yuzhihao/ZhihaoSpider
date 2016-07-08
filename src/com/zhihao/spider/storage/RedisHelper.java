package com.zhihao.spider.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.zhihao.spider.SpiderConfigs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * jedis工具类
 * @author yuzhihao
 *
 */
public class RedisHelper {
	private static final Logger logger = Logger.getLogger(RedisHelper.class.getName());
	private static final JedisPoolConfig config = new JedisPoolConfig();
	public static ShardedJedisPool shardedPool;
	public static JedisPool pool;
	/**
	static{
		16         JedisPoolConfig config =new JedisPoolConfig();//Jedis池配置
		17         config.setMaxActive(500);//最大活动的对象个数
		18         config.setMaxIdle(1000 * 60);//对象最大空闲时间
		19         config.setMaxWait(1000 * 10);//获取对象时最大等待时间
		20         config.setTestOnBorrow(true);
		21         String hostA = "192.168.0.100";
		22         int portA = 6379;
		23         String hostB = "192.168.0.115";
		24         int portB = 6379;
		25         List<JedisShardInfo> jdsInfoList =new ArrayList<JedisShardInfo>(2);
		26         JedisShardInfo infoA = new JedisShardInfo(hostA, portA);
		27         infoA.setPassword("admin");
		28         JedisShardInfo infoB = new JedisShardInfo(hostB, portB);
		29         infoB.setPassword("admin");
		30         jdsInfoList.add(infoA);
		31         jdsInfoList.add(infoB);
		32         pool =new ShardedJedisPool(config, jdsInfoList);
		33      }
	*/
	
	static {
		pool = new JedisPool(config, SpiderConfigs.redisUrl,Integer.parseInt(SpiderConfigs.redisPort));
		
		List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(); 
		//此处应有个从配置中获取JedisShardInfo过程
		
		JedisShardInfo infoA = new JedisShardInfo(SpiderConfigs.redisUrl,SpiderConfigs.redisPort);
		jdsInfoList.add(infoA);
		
		shardedPool = new ShardedJedisPool(config,jdsInfoList);
		logger.debug("Redis Init pool:"+pool);

	}
	
	
	RedisHelper(){
		
	}
	
	
	
	
	/**
	 * 获取单机redis
	 */
	public static  Jedis getRedisAdapter(){
		Jedis jedis = pool.getResource(); 
		return jedis;
	}
	/**
	 *	获取集群shardedJedis 对象
	 */
	public static ShardedJedis getShardedRedisAdapter(){
		ShardedJedis shardedJedis = shardedPool.getResource();
		return shardedJedis;
	}
	
	
	/******************************************   单机操作      ***********************************/
	
	/******************* Set 操作 **************************/
	
	/**
	 * 添加 到set，可一次添加多个
	 * @param key
	 * @param values[]
	 * @return
	 */
	public static boolean addSet(String key , String[] values ){
		Jedis jedis = null;
		boolean result = false;;
		try{
			jedis = getRedisAdapter();
			for(String value : values){
				jedis.sadd(key, value);
			}
			result = true;
		}catch(Exception e){
			logger.error("jedis addSet error :");
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
			
		}finally{
			pool.returnResource(jedis);
		}
		return result;
	}
	/**
	 * set是否包含
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean existSet(String key,String value){
		Jedis jedis = null;
		boolean result = false;
		try{
			jedis = getRedisAdapter();
			result = jedis.sismember(key, value);
		}catch(Exception e){
			logger.error("jedis existSet error");
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		}finally{
			pool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * set移除元素
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean removeSet(String key, String value){
		Jedis jedis = null;
		boolean result = false;
		try{
			jedis = getRedisAdapter();
			jedis.srem(key, value);
			result = true;
		}catch (Exception e){
			logger.error("jedis removeSet error");
			e.printStackTrace();
		}finally{
			pool.returnResource(jedis);
			pool.returnBrokenResource(jedis);
		}
		return result;
	}
	
	/**
	 * 编辑Set
	 * @param key
	 * @return
	 */
	public static Set<String>getSet(String key){
		Jedis jedis = null;
		Set<String> list = null; 
		try{
			jedis = getRedisAdapter();
			list = jedis.smembers(key);
		}catch(Exception e){
			logger.error("jedis getSet error");
			pool.returnBrokenResource(jedis);
		}
		finally{
			 pool.returnResource(jedis); 
		}
		return list;
	}
	
	public static List<String> scanSet(String key,String match){
		Jedis jedis = null;
		List<String> list = null; 
		try{
			jedis = getRedisAdapter();
			ScanParams scanParam = new ScanParams();
			scanParam.match(match);
			int cursor = 0;
			
			ScanResult<String> scanResult = jedis.sscan(key, String.valueOf(cursor), scanParam);
			list = scanResult.getResult();
			//list = jedis.smembers(key);
		}catch(Exception e){
			logger.error("jedis getSet error");
			pool.returnBrokenResource(jedis);
		}
		finally{
			 pool.returnResource(jedis); 
		}
		return list;
	}
	
	/*********************************** 哈希 操作 *******************************************/
	
	public static boolean HSet(String key,String field,String value){
		Jedis jedis = null;
		boolean result = false;
		try{
			jedis = getRedisAdapter();
			jedis.hset(key, field, value);
			result = true;
			pool.returnResource(jedis); 
		}catch(Exception e){
			logger.error("jedis HSet error");
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}
		return result;
	}
	
	public static String HGet(String key ,String field){
		Jedis jedis = null;
		String value = null;
		try{
			jedis = getRedisAdapter();
			value = jedis.hget(key, field);
			 pool.returnResource(jedis); 
		}catch(Exception e){
			logger.error("jedis HGet error,key:["+key+"] value:["+field + "]");
			pool.returnBrokenResource(jedis);
		}
		return value;
	}
	
	
	public static List<Map.Entry<String,String>> scanHSet(String domain,String match){
		Jedis jedis = null;
		List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(); 
		try{
			jedis = getRedisAdapter();
			ScanParams scanParam = new ScanParams();
			scanParam.match(match);
			scanParam.count(5000);
			int cursor = 0;
			int loopCount = 0;
			ScanResult<Map.Entry<String,String>> scanResult;
			do{
				scanResult = jedis.hscan(domain, String.valueOf(cursor), scanParam);
				list.addAll(scanResult.getResult());
				cursor = Integer.parseInt(scanResult.getStringCursor());
				loopCount++;
			}while(cursor>0);
			logger.debug("loop time : "+ loopCount);
			pool.returnResource(jedis); 
			//list = jedis.smembers(key);
		}catch(Exception e){
			logger.error("jedis getSet error");
			pool.returnBrokenResource(jedis);
		}
		return list;
	}
	
	public static long getSize(String key){
		Jedis jedis = null;
		long value = 0;
		try{
			jedis = getRedisAdapter();
			value = jedis.hlen(key);
			pool.returnResource(jedis); 
		}catch(Exception e){
			logger.error("jedis getSize error,key:["+key+"] ");
			pool.returnBrokenResource(jedis);
		}
		return value;
	}
	
	/**********************  Scripts 运行*********************************/
	public static void runScript(String script,List<String>keys,List<String>args){
		
		Jedis jedis = null;
		try{
			jedis = getRedisAdapter();
			jedis.eval(script, keys, args);
			pool.returnResource(jedis);
		}catch (Exception e){
			logger.error("jedis runScript error,script:["+script+"] ");
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}
		
	}
	
	/*****************/
	
	/**
	 * 删除 key
	 * @param key
	 * @return
	 */
	public static long delKey(String key){
		Jedis jedis = null;
		long result = 0;
		try{
			jedis = getRedisAdapter();
			result = jedis.del(key);
			pool.returnResource(jedis);
		}catch (Exception e){
			logger.error("jedis delKey error,key:["+key+"] ");
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}
		return result;
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
