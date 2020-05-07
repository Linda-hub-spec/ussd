package com.company.application;


import java.sql.SQLException;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class DatabaseOperation extends DBConnection{

	public DatabaseOperation() {
		super("Oracle");
	}
	
	public DatabaseOperation(String databaseName) {
		super(databaseName);
	}
	
	public DatabaseOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}

	
	public static void main(String[] args) {
		try {
			//System.out.println("Table Exist: " + new DatabaseOperation().tableExist("account"));
			System.out.println("Procedure Exist: " + new DatabaseOperation().procedureExist("CBS_UPLOAD_BATCh"));
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
