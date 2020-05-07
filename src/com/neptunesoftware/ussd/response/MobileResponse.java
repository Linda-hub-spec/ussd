package com.neptunesoftware.ussd.response;

import java.util.ArrayList;
import java.util.List;

public class MobileResponse {
	
	private List<String> accountNumber = new ArrayList<String>();
	private String accountName;
	private String contact;
	
	private String successMessage;
	private String errorMessage;
	private int statusCode;

	public List<String> getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(List<String> accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	
	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	

}
