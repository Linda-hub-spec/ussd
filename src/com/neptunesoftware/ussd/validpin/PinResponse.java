package com.neptunesoftware.ussd.validpin;


public class PinResponse {
	
	private String accountNumber ;
	private String accountName;
	private String contact;
	
	private String successMessage;
	private String errorMessage;
	private int statusCode;

	public PinResponse() {
		super();
	}

	public PinResponse(String accountNumber, String accountName, String contact, String successMessage,
			String errorMessage, int statusCode) {
		super();
		this.accountNumber = accountNumber;
		this.accountName = accountName;
		this.contact = contact;
		this.successMessage = successMessage;
		this.errorMessage = errorMessage;
		this.statusCode = statusCode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
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
