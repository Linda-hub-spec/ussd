package com.neptunesoftware.reuseableClasses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ResponseModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseModel {
	
	@XmlElement(name = "ResponseCode")
	private String statusCode;

	@XmlElement(name = "ResponseMessage")
	private String responseMessage;
	
	public ResponseModel() {		
	}
		
	public ResponseModel(String responseCode, String responseMessage) {
		super();
		this.statusCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(String responseCode) {
		this.statusCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}
	
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	
}
