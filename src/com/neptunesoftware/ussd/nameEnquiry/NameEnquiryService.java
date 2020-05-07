package com.neptunesoftware.ussd.nameEnquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.company.application.account.data.AccountResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.Responses;

public class NameEnquiryService {
	
	
	
	public NameEnquiryResponse internalNameEnquiry(String accountNumber) {
		
		DbConnection connect = new DbConnection();
		Connection con = null;
		ResultSet result = null;
		PreparedStatement statement = null;
		
		NameEnquiryResponse response = new NameEnquiryResponse();
		String sql = "SELECT acct_nm FROM account where acct_no = ?";
		
		try {
			
		con = connect.connector();	
		statement = con.prepareStatement(sql);
		statement.setString(1, accountNumber);
		result = statement.executeQuery();
		
		if (result.next()) {
			
			Responses responses = new Responses();
			responses.setSuccessMessage("Successful");
			responses.setStatusCode(200);
			
			response.setAccountName(result.getString(1));
			response.setResponse(responses);
			return response;
			
		}else {
			
			Responses responses = new Responses();
			responses.setSuccessMessage("Account Does Not Exist");
			responses.setStatusCode(400);
			
			response.setResponse(responses);
			return response;
		}
		
		} catch (SQLException e) {
			
			e.printStackTrace();
			Responses responses = new Responses();
			responses.setSuccessMessage("Failed");
			responses.setStatusCode(400);
			
			response.setResponse(responses);
			return response;
			
		}
	}
	
	public String externalNameEnquiry(String accountNumber, String bankCode) {
		
		
		Quickteller quickteller = new Quickteller();
		String httpResponse = quickteller.nameEnquiry(bankCode, accountNumber);
		
		
		return httpResponse;
	}

}
