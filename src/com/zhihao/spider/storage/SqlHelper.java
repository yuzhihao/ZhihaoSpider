package com.zhihao.spider.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JDBC工具类
 * @author Administrator
 *
 */
public class SqlHelper {
	

	public static String driver = ""; 
	public static String jdbc = ""; 
	public static String username = ""; 
	public static String password = ""; 
	
	//public static Connection con = null;
	
	//protected static  Logger logger=  LogManager.getLogger(SqlHelper.class.getName());;
	
	//
	public static Connection getConnection(String driver,String jdbc, String username,String password){
		Connection con = null;
		SqlHelper.driver = driver;
		SqlHelper.jdbc = jdbc;
		SqlHelper.username = username;
		SqlHelper.password = password;
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(jdbc,username,password);
			//logger.debug("connection get. :" +con);
			return con;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Connection getConnection(){
		Connection con = null;
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(jdbc,username,password);
			//logger.debug("connection get. :" +con);
			return con;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 处理rs中的第一个数据，与cls类型匹配赋值
	 * @param cls
	 * @param rs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public static <T> T executeResultSet(Class<T> cls, ResultSet rs) throws InstantiationException, IllegalAccessException, SQLException{
		T obj = cls.newInstance();
		ResultSetMetaData rsm = rs.getMetaData();
		int columnCount = rsm.getColumnCount();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			for (int j = 1; j <= columnCount; j++) {
				String columnName = rsm.getColumnName(j);
				if (fieldName.equalsIgnoreCase(columnName)) {
					String type = rsm.getColumnTypeName(j);
					field.setAccessible(true);
					if(type.toLowerCase().contains("blob")){
						Blob blob = rs.getBlob(j);
						//field.set(obj,new String(blob.getBytes((long)1, (int)blob.length())));  
						if(blob!=null){
							InputStream is = blob.getBinaryStream();
							ByteArrayInputStream bais = (ByteArrayInputStream)is;
							byte[] byte_data = new byte[bais.available()]; //bais.available()返回此输入流的字节数
							bais.read(byte_data, 0,byte_data.length);//将输入流中的内容读到指定的数组
							String strResult = "";
							try {
								strResult = new String(byte_data,"utf-8");
							} catch (UnsupportedEncodingException e) {							
								e.printStackTrace();
							} //再转为String，并使用指定的编码方式
							field.set(obj, strResult);
						}else{
							field.set(obj,"");
						}
					}
					else {
						field.set(obj, rs.getObject(j));
					}
					
					break;
				}
			}
		}
		return obj;
	}
	
	/**
	 * 执行存储过程的函数，返回非table值
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Object> executeProc(String sql,List<MyColumn> params){
		List<Object> list = new ArrayList<Object>();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		MyColumn temp = null;
		try {
			//初始化CallableStatement
			con = getConnection();
			cs = con.prepareCall(sql);
			for(int i=0;i<params.size();i++){
				temp = params.get(i);
				//判断参数类型 in、out处理方式不同
				if(temp.procParamType==MyColumn.PROC_PARAM_OUT){
					switch(temp.type){
					case MyColumn.T_INTEGER:
						cs.registerOutParameter(temp.index, Types.INTEGER);
						break;
					case MyColumn.T_STR:
						cs.registerOutParameter(temp.index, Types.VARCHAR);
						break;
					}
				}
				else if(temp.procParamType==MyColumn.PROC_PARAM_IN){
					switch(temp.type){
					case MyColumn.T_INTEGER:
						cs.setInt(temp.index, (int) temp.value);
						break;
					case MyColumn.T_LONG:
						cs.setLong(temp.index, (long) temp.value);
						break;
					case MyColumn.T_STR:
						cs.setString(temp.index, (String) temp.value);
						break;
					}
				}
			}
			//执行语句
			cs.execute();
			//获取返回值：类型为out的参数
			for(int i=0;i<params.size();i++){
				temp = params.get(i);
				if(temp.procParamType==MyColumn.PROC_PARAM_OUT){
					switch(temp.type){
					case MyColumn.T_INTEGER:
						list.add(cs.getInt(temp.index));
						break;
					case MyColumn.T_STR:
						list.add(cs.getString(temp.index));
					}
				}
			}
		}catch (Exception e) {
			 e.printStackTrace();
	 } finally {
		close(con, cs, rs);
	 }
		return list;
	}
	
	/**
	 * 执行insert/update语句，返回 插入数据的identity
	 * @param sql
	 * @return
	 */
	public static int executeIdentity(String sql ,List<MyColumn> columns) {
		 int identity = -1;
		 PreparedStatement pstmt = null;
		 ResultSet rs = null;
		 Connection con = null;
		 try {
			 con= getConnection();
			 pstmt = con.prepareStatement(sql);
			 MyColumn temp = null;
			 for(int i=0; i<columns.size(); i++){
				 temp = columns.get(i);
				 switch (temp.type){
				 case MyColumn.T_STR:
					 pstmt.setString(temp.index, (String) temp.value);
					 break;
				 case MyColumn.T_INTEGER:
					 pstmt.setInt(temp.index,  (int) temp.value);
					 break;
				 case MyColumn.T_DATE:
					 pstmt.setTimestamp(temp.index, new Timestamp(((java.util.Date)temp.value).getTime()));
					 break;
				 case MyColumn.T_LONG:
					 pstmt.setLong(temp.index, (long) temp.value);
					 break;
				 case MyColumn.T_BLOB:
					 //byte[] cert_dataBytes = ((String) temp.value).getBytes();
					 //ByteArrayInputStream bais1 = new ByteArrayInputStream(cert_dataBytes);
					 //pstmt.setBinaryStream(temp.index, bais1,cert_dataBytes.length);
					  //pstmt.setBlob(temp.index, stringToInputStream((String) temp.value));
					 Blob b = new SerialBlob(((String)temp.value).getBytes("utf-8"));//String 转 blob
					 pstmt.setBlob(temp.index, b);
					 break;
				 }
			 }
			 pstmt.executeUpdate();
			 rs = pstmt.getGeneratedKeys();
			 if (rs.next()) {
				 identity = rs.getInt(1);
			 }
		 } catch (Exception e) {
				 e.printStackTrace();
		 } finally {
			 close(con, pstmt, rs);
		 }
		 return identity;
	}
	
	public static void close(Connection con, Statement stmt, ResultSet rs){
		try{
			if(rs != null){
				rs.close();
				rs = null;
			}
			if(stmt != null){
				stmt.close();
				stmt = null;
			}
			if(con != null){
				con.close();
				con = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static InputStream stringToInputStream(String str){
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }
	
	static public class MyColumn{
		public static final int T_STR = 1;
		public static final int T_INTEGER = 2;
		public static final int T_BLOB = 3;
		public static final int T_DATE = 4;
		public static final int T_LONG = 5;
		
		public static final int PROC_PARAM_OUT = 1;
		public static final int PROC_PARAM_IN = 0;
		public static final int PROC_PARAM_INOUT = 2;
		
		public MyColumn(int i){
			index = i;
		}
		
		public MyColumn(int i, int t, Object v){
			index = i;
			type = t;
			value = v;
		}
		
		public MyColumn(int i, int t, Object v, int p){
			index = i;
			type = t;
			value = v;
			procParamType = p;
		}
		
		public int index;
		public int type;
		public Object value;
		public int procParamType = 0;
	}
}
