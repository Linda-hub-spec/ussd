package com.neptunesoftware.ussd.validpin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.MobileResponse;
import com.neptunesoftware.ussd.response.Responses;

public class GetPin {
	
	public PinData getPin(PinResponse data) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		DbConnection con = new DbConnection();
		PinData pinData = new PinData();
	
	String sql = "SELECT pin_1 FROM Lapo_pin WHERE mobile_no = ? ";
	
	try {
		
		connection = con.connector();
		ps = connection.prepareStatement(sql);
		ps.setString(1, data.getContact());
		//ps.setString(2, data.getAccountNumber());
		res = ps.executeQuery();
		
		if (res.next()) {
			
			pinData.setPin(res.getString(1));
			
			Responses response = new Responses();
			response.setStatusCode(200);
			
			pinData.setResponse(response);
			System.out.println("Pin Retrieved");
			return pinData;
			
		}else {
			Responses error = new Responses();
			error.setStatusCode(400);
			pinData.setResponse(error);
			System.out.println("Pin Retrieval failed");
			return pinData;
		}
		
		
		
	}catch(Exception e) {
		
		e.printStackTrace();
	return null;
	}
	
}

}
