package com.neptunesoftware.ussd.ussdUsers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.neptunesoftware.ussd.FirstTimeUser.FirstTimeData;
import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.Responses;

public class UssdUsers {
	
	
	public FirstTimeData validUssdUser(FirstTimeData data) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		DbConnection con = new DbConnection();
		FirstTimeData userdata = new FirstTimeData();

		
		String sql = "SELECT acct_no,pin_1 FROM Lapo_pin  WHERE mobile_no = ?";
		
		try {
			
			connection = con.connector();
			ps = connection.prepareStatement(sql);
			ps.setString(1, data.getMobileNumber());
			res = ps.executeQuery();
			
			if (res.next()) {
				
				/*
				 * userdata.setAcct_No(res.getString("acct_no"));
				 * userdata.setPin1(res.getString("pin_1"));
				 */
				Responses response = new Responses();
				response.setStatusCode(200);
				response.setSuccessMessage("Successful");
				userdata.setResponse(response);
				System.out.println("UssdUser check successful");
				
				return userdata;
				
			}else {
				
				Responses response = new Responses();
				response.setStatusCode(400);
				response.setErrorMessage("Failed");
				userdata.setResponse(response);
				System.out.println("UssdUser check Failed");
				
				return userdata;
			}
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return null;
			
		}
		
	}
}
