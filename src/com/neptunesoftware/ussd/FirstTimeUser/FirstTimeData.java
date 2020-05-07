package com.neptunesoftware.ussd.FirstTimeUser;

import com.neptunesoftware.ussd.response.Responses;

public class FirstTimeData {
	
		private String pin1;
		private String pin2;
		private String mobileNumber;
		private String accountNumber;
		
		private Responses response;
		
		
		public FirstTimeData() {
			
		}
		
		public FirstTimeData(String pin1, String pin2, String mobileNumber,
				String accountNumber) {
			
			this.pin1 = pin1;
			this.pin2 = pin2;
			this.mobileNumber = mobileNumber;
			this.accountNumber = accountNumber;
		}

		public String getPin1() {
			return pin1;
		}
		public void setPin1(String pin1) {
			this.pin1 = pin1;
		}
		public String getPin2() {
			return pin2;
		}
		public void setPin2(String pin2) {
			this.pin2 = pin2;
		}
		public String getMobileNumber() {
			return mobileNumber;
		}
		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
		public String getAccountNumber() {
			return accountNumber;
		}
		public void setAcct_No(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		@Override
		public String toString() {
			return "FirstTimeData [pin1=" + pin1 + ", pin2=" + pin2
					+ ", mobileNumber=" + mobileNumber + ", accountNumber=" + accountNumber + "]";
		}

		public Responses getResponse() {
			return response;
		}

		public void setResponse(Responses response) {
			this.response = response;
		}

		


}
