package com.neptunesoftware.reuseableClasses.Database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DatabaseProperty")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseProperty {

	@XmlElement(name = "Type")
	private String type;
	
	@XmlElement(name = "Username")
	private String username;
	
	@XmlElement(name = "Password")
	private String password;
	
	@XmlElement(name = "IPAddress")
	private String ipAddress;
	
	@XmlElement(name = "PortNumber")
	private String portNumber;
	
	@XmlElement(name = "ServiceName")
	private String serviceName;

	public String getType() {
		return type == null ? "" : type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username == null ? "" : username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password == null ? "" : password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress == null ? "" : ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPortNumber() {
		return portNumber == null ? "" : portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getServiceName() {
		return serviceName == null ? "" : serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
}
