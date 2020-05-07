package com.neptunesoftware.ussd.balanceEnquiry;

public class BalanceEnquiryData {
	
	private String beneficiaryAccount;
	private String amount;
	private String pin;
	private String mobileNumber;
	
	public BalanceEnquiryData() {
		super();
	}
	
	public BalanceEnquiryData(String pin, String mobileNumber) {
		super();
		this.pin = pin;
		this.mobileNumber = mobileNumber;
	}

	public BalanceEnquiryData(String beneficiaryAccount, String amount, String pin, String mobileNumber) {
		super();
		this.beneficiaryAccount = beneficiaryAccount;
		this.amount = amount;
		this.pin = pin;
		this.mobileNumber = mobileNumber;
	}
	public String getBeneficiaryAccount() {
		return beneficiaryAccount;
	}
	public void setBeneficiaryAccount(String beneficiaryAccount) {
		this.beneficiaryAccount = beneficiaryAccount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}


}
