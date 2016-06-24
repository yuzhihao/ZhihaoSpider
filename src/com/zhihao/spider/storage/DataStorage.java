package com.zhihao.spider.storage;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhihao.spider.SpiderConfigs;
import com.zhihao.spider.model.Resource;
import com.zhihao.spider.model.VisitedUrl;
import com.zhihao.spider.storage.SqlHelper.MyColumn;

public class DataStorage {

	private Connection con;
	private Statement stmt;
	private String sql;
	private ResultSet rs;
	
	private BaseDao <Resource> resourceDao;
	private BaseDao <VisitedUrl> visitedurlDao;
	
	public DataStorage(){
		resourceDao = new BaseDao<Resource>(Resource.class);
		visitedurlDao = new BaseDao<VisitedUrl>(VisitedUrl.class);
	}
	
	public void store(Object targetData) {
		// TODO Auto-generated method stub
		
	}

	public boolean storeVisitedUrl(String url){
		VisitedUrl visitedUrl = new VisitedUrl(url);
		if(visitedurlDao.save(visitedUrl)>0)
			return true;
		else
			return false;
		
	}
	
	public boolean isUrlVisited(String url){
		sql = "SELECT 1 FROM yuzhihao.visitedurl WHERE URL = '" + url + "'";
		try {
			con = SqlHelper.getConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				SqlHelper.close(con, stmt, rs);
				return true;
	        }
			SqlHelper.close(con, stmt, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
