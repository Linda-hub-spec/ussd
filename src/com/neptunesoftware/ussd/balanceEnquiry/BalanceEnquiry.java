package com.neptunesoftware.ussd.balanceEnquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.Responses;

public class BalanceEnquiry {
	
	public BalanceEnquiryResponse balance(BalanceEnquiryData balanceData) { 
		
		
		PreparedStatement ps =null;
		ResultSet res = null;
		
		DbConnection connect = new DbConnection();
		Responses response = new Responses();
		BalanceEnquiryResponse balance = new BalanceEnquiryResponse();
		
		String sql = "SELECT ledger_bal "
				+ "FROM deposit_account_summary A "
				+ "JOIN Lapo_pin B"
				+ " ON A.acct_no = B.acct_no WHERE B.mobile_no = ? AND B.pin_1= ?";
		
		try {
			
			Connection con = connect.connector();
			ps = con.prepareStatement(sql);
			ps.setString(1, balanceData.getMobileNumber());
			ps.setString(2, balanceData.getPin());
			res = ps.executeQuery();
			
			if(res.next()) {
				
				balance.setAccountBalance(res.getString(1));
				
				response.setStatusCode(200);
				balance.setResponse(response);
				
				System.out.println("Successful BalanceEnquiry Transation");
				return balance;
			}else {
				
				response.setStatusCode(400);
				balance.setResponse(response);
				System.out.println("BalanceEnquiry Transation Failed");
				
				return balance;
				
			}
			
		}catch(Exception e) {
			
			e.printStackTrace();
			return null;
			
		}
		
		
	}

}
