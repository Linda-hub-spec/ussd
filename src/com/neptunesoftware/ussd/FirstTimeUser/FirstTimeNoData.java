package com.neptunesoftware.ussd.FirstTimeUser;

public class FirstTimeNoData {
	
	private String firstName;
	private String lastName;
	private String gender;
	private String contact;
	
	public FirstTimeNoData() {
		
	}
	
	public FirstTimeNoData(String firstName, String lastName, String gender, String contact) {
	
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.contact = contact;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
	

}
