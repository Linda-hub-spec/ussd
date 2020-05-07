package com.neptunesoftware.ussd.miniStatement;

import java.util.ArrayList;
import java.util.List;

public class MiniStatementResponse {
	
	List<MiniStatement> miniResponse = new ArrayList<MiniStatement>();
	
	private int statusCode;
	private String successMessage;
	private String errorMessage;
	
	public MiniStatementResponse() {
		super();
	}
	public MiniStatementResponse(List<MiniStatement> miniResponse, int statusCode, String successMessage,
			String errorMessage) {
		super();
		this.miniResponse = miniResponse;
		this.statusCode = statusCode;
		this.successMessage = successMessage;
		this.errorMessage = errorMessage;
	}
	public List<MiniStatement> getMiniResponse() {
		return miniResponse;
	}
	public void setMiniResponse(List<MiniStatement> miniResponse) {
		this.miniResponse = miniResponse;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
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
	
	

}
