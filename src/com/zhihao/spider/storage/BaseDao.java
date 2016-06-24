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
import com.zhihao.spider.storage.SqlHelper.MyColumn;

public class BaseDao<T> {

	private  Class<T> cls;
	
	public BaseDao(Class<T> cls){
		this.cls = cls;
	}
	
	public int save(Object obj) {
		StringBuffer sql = new StringBuffer( "insert into " + SpiderConfigs.DB_SCHEMA + removePath(cls.getName()).toLowerCase() + " (");
		List<MyColumn> columns = createPreparedSql(sql, obj, "");
		sql.append(") values(");
		boolean bIsFirst = true;
		for(int i=0;i<columns.size();i++){
			if(!bIsFirst){
				sql.append(",");
			}else{
				bIsFirst = false;
			}
			sql.append("?");
		}
		sql.append(")");
		
		return SqlHelper.executeIdentity(sql.toString(),columns);
	}
	
	public List<MyColumn> createPreparedSql(StringBuffer sql, Object obj,String additional){
		List<MyColumn> columns = new ArrayList<MyColumn>();
		String sqlEmpty = "select * from "  + SpiderConfigs.DB_SCHEMA + removePath(cls.getName()).toLowerCase() +" where 1=0";
		//从数据库中获取该表的栏位信息
		Connection con = SqlHelper.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null ;
		int columnCount = 0;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlEmpty);
			rsm = rs.getMetaData();
			columnCount = rsm.getColumnCount();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		boolean isFirst = true;
		Field[] fields = cls.getDeclaredFields();
		int index = 1;
		for(int i = 0; i<fields.length; i++){
			Field field = fields[i];
			String fieldName = field.getName();
			for (int j = 1; j <= columnCount; j++) {
				String columnName = null;
				String columnType = null;
				try {
					columnName = rsm.getColumnName(j);
					columnType = rsm.getColumnTypeName(j);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				if (fieldName.equalsIgnoreCase(columnName)) {
					field.setAccessible(true);
					MyColumn tempColumn = new MyColumn(index);
					index++;
					field.setAccessible(true);
					if( columnType.equalsIgnoreCase("BLOB")){
						try {
							if(field.get(obj)==null)
								tempColumn.value = "";
							else
								tempColumn.value = (String)field.get(obj);
							tempColumn.type = MyColumn.T_BLOB;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							break;
						}
					}
					else if (field.getType().equals(String.class) ){
						try {
							if(field.get(obj)==null)
								tempColumn.value = "";
							else
								tempColumn.value = (String)field.get(obj);
							tempColumn.type = MyColumn.T_STR;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							break;
						}
					}
					else if (field.getType().equals(int.class)){
						try {
							tempColumn.value = field.getInt(obj);
							tempColumn.type = MyColumn.T_INTEGER;
						}catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							break;
						}
					}
					else if (field.getType().equals(long.class)){
						try {
							tempColumn.value = field.getLong(obj);
							tempColumn.type = MyColumn.T_LONG;
						}catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							break;
						}
					}
					else if (field.getType().equals(Date.class)){
						try{
							//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							//tempColumn.value = sdf.format((Date) field.get(obj));
							//tempColumn.value = new java.sql.Date(((Date) field.get(obj)).getTime());
							tempColumn.value = (Date) field.get(obj);
							tempColumn.type = MyColumn.T_DATE;
						}catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							break;
						}
					}else{
						continue;
					}
					if(!isFirst)
						sql.append(", ");
					sql.append(field.getName()+additional);
					isFirst = false;
					columns.add(tempColumn);
					break;
				}
			}
		}
		SqlHelper.close(con, stmt, rs);
		return columns;
	}
	
	private String removePath(String name ){
		return name.substring(name.lastIndexOf(".")+1);
	}
}
