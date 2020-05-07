package com.neptunesoftware.ussd.balanceEnquiry;

import com.neptunesoftware.ussd.response.Responses;

public class BalanceEnquiryResponse {
	
	private String accountBalance;
	private Responses response;
	
	public String getAccountBalance() {
		return accountBalance;
	}
	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}
	public Responses getResponse() {
		return response;
	}
	public void setResponse(Responses response) {
		this.response = response;
	}

}
