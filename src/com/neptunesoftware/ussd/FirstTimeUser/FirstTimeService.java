package com.neptunesoftware.ussd.FirstTimeUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.neptunesoftware.ussd.connection.DbConnection;
import com.neptunesoftware.ussd.response.Responses;

public class FirstTimeService {
	
	private  Connection connection = null;
	private  PreparedStatement ps = null;
	private  ResultSet res = null;
	
	private  DbConnection conn = new DbConnection();
	
	   Responses response = new Responses();
	
	public  Responses savePin(FirstTimeData data){
		//saves pin value entered in a database
		
	try{
		String x = data.getPin1();
		String y = data.getPin2();
		
		connection = conn.connector();
		
		if (x == y ){
			
		String sql= "INSERT INTO Lapo_pin ( mobile_no, pin_1, pin_2) values (?,?,?)";
		
		ps = connection.prepareStatement(sql);
		//ps.setString(1, data.getAccountNumber());
		ps.setString(1, data.getMobileNumber());
		ps.setString(2, data.getPin1());
		ps.setString(3, data.getPin2());
		res = ps.executeQuery();
		
		response.setSuccessMessage("Successful Pin Registration");
		response.setStatusCode(200);
		System.out.println("Successful Pin Registration");
		
		}
		return response;
		
		}catch(Exception e)
		{
			e.printStackTrace();
			response.setErrorMessage("Failed Pin Registration");
			response.setStatusCode(400);
			System.out.println("Failed Pin Registration");
			return response;
		}
	
		
	}
	
	/*
	 * public static void main(String arg[]){ FirstTimeData data = new
	 * FirstTimeData(); try{ data.setAcct_No("098987766");
	 * data.setMobile_No("0987655"); data.setPin1("1234"); data.setPin2("1234");
	 * savePin(data); System.out.println("successfull"); }catch(Exception e){
	 * e.printStackTrace(); System.out.println("failed transaction"); } }
	 */
	 
		/*,,,*/
	
	
	public Responses newCustomer(FirstTimeNoData data){
		//saves details of new customer
		
		try{
		connection = conn.connector();
		String sql = "INSERT INTO new_Lapo(first_name, last_name, gender, mobile_no) values (?,?,?,?)";
		
		ps = connection.prepareStatement(sql);
		ps.setString(1, data.getFirstName());
		ps.setString(2, data.getLastName());
		ps.setString(3, data.getGender());
		ps.setString(4, data.getContact());
		res = ps.executeQuery();
		
		response.setSuccessMessage("Successful Data Submittion");
		response.setStatusCode(200);
		System.out.println("Successful Submittion");
		System.out.println(data.getContact());
		return response;
		
		}catch(Exception e)
		{
			e.printStackTrace();
			response.setErrorMessage("Failed Data Submittion");
			response.setStatusCode(400);
			System.out.println("Failed submittion");
			return response;
			
		}
	}
	
	
	
	

}
