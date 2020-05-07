package com.neptunesoftware.ussd.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class DbConnection extends DBConnection{
	
	public Connection connector(){
		return databaseConnection();
		
//		Connection connection= null;
//		
//		try {
//			
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.152.2.116:1521/orclpdb", "addossertest", "neptune");
//			 System.out.println("successfull connection");
//		}catch (Exception er) {
//			System.out.println("Failed connection");
//			er.printStackTrace();
//		}
//		return connection;
		
		
	} 
	/*public static void main(String arg[]) {
		connector();
	}
*/
}
