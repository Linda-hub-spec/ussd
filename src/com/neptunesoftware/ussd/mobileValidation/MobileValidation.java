package com.neptunesoftware.ussd.mobileValidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.neptunesoftware.ussd.FirstTimeUser.FirstTimeData;
import com.neptunesoftware.ussd.balanceEnquiry.BalanceEnquiryData;
import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.MobileResponse;
import com.neptunesoftware.ussd.validpin.PinResponse;

public class MobileValidation {
	
	 DbConnection conn = new DbConnection();
	 Connection connection = null;
	 PreparedStatement ps = null;
	 ResultSet res = null;
	
	public MobileResponse  firstValidation(FirstTimeData mobile){
		//first check for account associated with the mobile number

		MobileResponse resp = new MobileResponse();
		List<String> acct = new ArrayList<String>();
		
		try{
			
		connection = conn.connector();
		
		String sql = "SELECT A.acct_nm, A.acct_no, B.contact "
				+ "FROM account A "
				+ "JOIN customer_contact_mode B "
				+"ON A.cust_id = B.cust_id WHERE B.contact = ? AND A.PROD_CAT_TY = 'DP'";
		
		ps =connection.prepareStatement(sql);
		ps.setString(1, mobile.getMobileNumber());
		res = ps.executeQuery();
		System.out.println("we meet here");
		
		while (res.next()){
			
			
			String acctNumber =res.getString(2);
			acct.add(acctNumber);
			
			
			resp.setAccountName(res.getString(1));
			resp.setContact(res.getString(3));
			resp.setStatusCode(200);
			System.out.println("valid first mobile number");
			
		}
		resp.setAccountNumber(acct);
		return resp;
		
		}catch(Exception e){
			
		e.printStackTrace();
		
		resp.setStatusCode(400);
		System.out.println("first mobile validation failed");
		
		return resp;
		}
		
	}
	
	
	public  PinResponse  mobileValidation(BalanceEnquiryData mobile){
		//second check for account of Ussd user

		DbConnection conn = new DbConnection();
		 PinResponse resp = new PinResponse();
		Connection connection = null;
		 PreparedStatement ps = null;
		 ResultSet res ;
		
		try{
			
		connection = conn.connector();
		
		String sql = "SELECT acct_no,mobile_no FROM lapo_pin WHERE mobile_no = ? ";
			
		ps =connection.prepareStatement(sql);
		ps.setString(1, mobile.getMobileNumber());
		
		res = ps.executeQuery();
		
		if (res.next()){


			resp.setAccountNumber(res.getString(1));
			resp.setContact(res.getString(2));
			
			resp.setStatusCode(200);
			System.out.println(resp.getStatusCode());
			System.out.println(resp.getAccountNumber());
			System.out.println("Ussd valid mobile number");
			return resp;
		}else {
			resp.setStatusCode(400);
			resp.setErrorMessage("Invalid Pin");
			return resp;
		}
		
		}catch(Exception e){
			
		e.printStackTrace();
		
		resp.setStatusCode(400);
		resp.setErrorMessage("Ussd mobile validation failed");
		System.out.println("Ussd mobile validation failed");
		
		return resp;
		}
		
	}
	
	public PinResponse accountNumber(String number) {
		
	
		PinResponse resp = new PinResponse();
	
		
		String sql = "SELECT acct_no FROM Lapo_pin WHERE mobile_no = ? ";
		
		
		try {
			connection = conn.connector();
			ps =connection.prepareStatement(sql);
			ps.setString(1,number);
			res = ps.executeQuery();
			
			if (res.next()){


				resp.setAccountNumber(res.getString(1));
				
				resp.setStatusCode(200);
				System.out.println("Ussd valid mobile number");
				return resp;
			}else {
				resp.setStatusCode(400);
				System.out.println("invalid mobile number");
				return resp;
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.setStatusCode(400);
			resp.setErrorMessage("Ussd mobile validation failed");
			return resp;
		}
		
		
	}
	
	/*
	 * public static void main(String arg[]) {
	 * 
	 * BalanceEnquiryData mobile = new BalanceEnquiryData();
	 * mobile.setMobileNumber("+2348096321456"); mobile.setPin("0101");
	 * mobileValidation(mobile);
	 * 
	 * }
	 */
	 

}
