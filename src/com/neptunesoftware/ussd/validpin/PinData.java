package com.neptunesoftware.ussd.validpin;

import com.neptunesoftware.ussd.response.Responses;

public class PinData {
	
	private String pin;
	private Responses response;
	
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public Responses getResponse() {
		return response;
	}
	public void setResponse(Responses response) {
		this.response = response;
	}

}
