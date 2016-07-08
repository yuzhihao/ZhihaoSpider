package com.zhihao.spider.paser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.zhihao.spider.storage.DataStorage;

public class DoubanParser extends HtmlParser{
	protected  static final Logger logger = Logger.getLogger(DoubanParser.class.getName());
	
	
	protected Matcher matcher ;
	public DoubanParser(DataStorage store) {
		super(store);
		pattern = Pattern.compile("^(http://|https://)?(www\\.)?[A-Za-z]+\\.douban\\.com/[A-Za-z]+/[0-9]+(/)?$",Pattern.CASE_INSENSITIVE);
	}

	@Override
	protected boolean checkUrl(String url) {
		logger.debug("checkUrl called");
		matcher = pattern.matcher(url);
		if(matcher.matches()){
			logger.debug(url + "match succeed");
			return true;
		};
		logger.debug(url + "match fail");
		return false;
	}

}
