package com.neptunesoftware.ussd.miniStatement;

public class MiniStatement {
	
	
		private String transactionType;
		private String transactionDate;
		private String transactionAmount;
		private String transactionRefNo;
	
		public MiniStatement() {
			super();
		}
		public MiniStatement(String transactionType, String transactionDate, String transactionAmount,
				String transactionRefNo) {
			super();
			this.transactionType = transactionType;
			this.transactionDate = transactionDate;
			this.transactionAmount = transactionAmount;
			this.transactionRefNo = transactionRefNo;
		}
		public String getTransactionType() {
			return transactionType;
		}
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}
		public String getTransactionDate() {
			return transactionDate;
		}
		public void setTransactionDate(String transactionDate) {
			this.transactionDate = transactionDate;
		}
		public String getTransactionAmount() {
			return transactionAmount;
		}
		public void setTransactionAmount(String transactionAmount) {
			this.transactionAmount = transactionAmount;
		}
		public String getTransactionRefNo() {
			return transactionRefNo;
		}
		public void setTransactionRefNo(String transactionRefNo) {
			this.transactionRefNo = transactionRefNo;
		}
		

}
