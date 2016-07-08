package com.zhihao.spider.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhihao.spider.storage.RedisHelper;


public class JsonUtil {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RedisHelper.class.getName());

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("unchecked")
	public static String parse(Object obj){
		if(obj instanceof Set<?>){
			return parseSet((Set<Object>) obj);
		}else if(obj instanceof List<?>){
			return parseList((List<Object>) obj);
			
		}
		return "no type patch in json parse.";
	}
	
	public static String parseSet(Set<Object> set){
		//JSONArray jsonArray = new JSONArray();
		String result = "";
		try{
			result = objectMapper.writeValueAsString(set);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public static String parseList(List<Object> obj){
		String result = "";
		try{
			result = objectMapper.writeValueAsString(obj);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
