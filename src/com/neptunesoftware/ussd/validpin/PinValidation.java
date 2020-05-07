package com.neptunesoftware.ussd.validpin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.Responses;

public class PinValidation {
	
	private  DbConnection conn = new DbConnection();
	 Responses response = new Responses();
	
	public Responses validatePin(String pin) {
		
		PreparedStatement ps = null;
		ResultSet res =null;
		try {
		Connection connect = conn.connector();
		
		String sql = "SELECT acct_no FROM Lapo_pin WHERE pin2 = ?";
		ps = connect.prepareStatement(sql);
		ps.setString(1, pin);
		res = ps.executeQuery(sql);
		
		if(res.next()) {
			
		response.setSuccessMessage("valid Pin");
		response.setStatusCode(200);
		System.out.println("Successful Pin Validation");
		
		}
		return response;
		
		}catch(Exception e) {
			e.printStackTrace();
			response.setErrorMessage("Invalid Pin");
			response.setStatusCode(400);
			System.out.println("Failed pin Validation");
			
			return response;
		}
		
		
		
		
		
	}

}
