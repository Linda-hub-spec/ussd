package com.neptunesoftware.ussd.nameEnquiry;

import com.neptunesoftware.ussd.response.Responses;

public class NameEnquiryResponse {
	
	private String AccountName;
	private Responses response;
	
	public String getAccountName() {
		return AccountName;
	}
	public void setAccountName(String accountName) {
		AccountName = accountName;
	}
	public Responses getResponse() {
		return response;
	}
	public void setResponse(Responses response) {
		this.response = response;
	} 

}
