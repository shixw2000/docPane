package my.demo.test.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

public class TestMysql {
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://192.168.1.201/data";
	private String user = "shixw";
	private String passwd = "shixw";
	
	public TestMysql() { 
		try {
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, user, passwd);
			
			System.out.println("mysql:" + conn);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	public static void main(String argv[]) {
		TestMysql test = new TestMysql();
		
	}
}

