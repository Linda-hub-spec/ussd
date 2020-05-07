package com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer;

public class FundTransferRequest {
	
	public String mac ;
	public Beneficiary beneficiary = new Beneficiary();
	public String initiatingEntityCode ;
	public Initiation initiation = new Initiation();
	public Sender sender = new Sender();
	public Termination termination = new Termination();
	public String transferCode ;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public Beneficiary getBeneficiary() {
		return beneficiary;
	}
	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}
	public String getInitiatingEntityCode() {
		return initiatingEntityCode;
	}
	public void setInitiatingEntityCode(String initiatingEntityCode) {
		this.initiatingEntityCode = initiatingEntityCode;
	}
	public Initiation getInitiation() {
		return initiation;
	}
	public void setInitiation(Initiation initiation) {
		this.initiation = initiation;
	}
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	public Termination getTermination() {
		return termination;
	}
	public void setTermination(Termination termination) {
		this.termination = termination;
	}
	public String getTransferCode() {
		return transferCode;
	}
	public void setTransferCode(String transferCode) {
		this.transferCode = transferCode;
	}
	
	
}