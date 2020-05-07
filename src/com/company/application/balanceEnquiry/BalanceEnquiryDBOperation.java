package com.company.application.balanceEnquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class BalanceEnquiryDBOperation extends DBConnection {

	public BalanceEnquiryDBOperation() {
		super("Oracle");
	}
	
	public BalanceEnquiryDBOperation(String databaseName) {
		super(databaseName);
	}
		
	public BalanceEnquiryDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	
	public String accountBalance(String accountNo) throws Exception {
		
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
				
		try {
			String sql = "SELECT Acct_no, cleared_bal + reserved_fund + earmarked_fund balance " + 
					"FROM deposit_account_summary "
					+ "WHERE acct_no = ?";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, accountNo);
			
			rs = preparedStatement.executeQuery();
			
			String availableBalance = "00";
			while(rs.next()) {
				availableBalance = rs.getBigDecimal(2) + "";
			}
			
			return availableBalance;
		} catch (SQLException e) {
			return "";
		} finally {
	
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	
}
