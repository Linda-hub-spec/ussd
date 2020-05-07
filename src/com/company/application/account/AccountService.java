package com.company.application.account;

import java.net.URL;

import com.company.application.account.data.AccountHistoryResponse;
import com.company.application.account.data.AccountResponse;
import com.company.application.account.data.MiniStatementResponse;
import com.company.application.account.data.MultiAccountResponse;
import com.company.application.balanceEnquiry.BalanceEnquiryResponse;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.supernova.ws.server.transfer.FundsTransferWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.transfer.FundsTransferWebServiceStub;
import com.neptunesoftware.supernova.ws.server.transfer.data.NameInquiryRequestData;
import com.neptunesoftware.supernova.ws.server.transfer.data.NameInquiryResponseData;

public class AccountService {

	private String ipAddress;
	private String portNo;
	private String channelCode;
	private String transactionLimitInternal;
	private String transactionLimitExternal;
	
	public AccountService() {
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		this.ipAddress = rubikonCredential.getIpAddress();
		this.portNo = rubikonCredential.getPortNumber();
		this.channelCode = rubikonCredential.getChannelCode();
		this.transactionLimitInternal = rubikonCredential.getTransferLimitInternal();
		this.transactionLimitExternal = rubikonCredential.getTransferLimitExternal();
	}
	
	
	public AccountResponse nameEnquiryRubikon(String accountNo) {
		System.out.println("\n**** In Name Enquiry ****");
		
		AccountResponse accountResponse = new AccountResponse();
		
		NameInquiryRequestData inquiryRequest = new NameInquiryRequestData();
		inquiryRequest.setSessionId(String.valueOf(System.currentTimeMillis()));
		inquiryRequest.setDestinationInstitutionCode("");
		inquiryRequest.setChannelCode(channelCode);
		inquiryRequest.setAccountNumber(accountNo);
		
		try {			
			
			URL url = new URL("http://" + ipAddress + ":" + portNo + "/supernovaws/FundsTransferWebServiceEndPointPort?wsdl");
			FundsTransferWebServiceStub accountWebServiceStub = new FundsTransferWebServiceEndPointPort(url).getFundsTransferWebServiceStubPort();
			NameInquiryResponseData inquiryResponse = accountWebServiceStub.nameenquirysingleitem(inquiryRequest);
			
//			FundsTransferWebService transferWebService = new FundsTransferWebServiceEndPointPort_Impl(
//						"http://" + ipAddress + ":" + portNo + "/supernovaws/FundsTransferWebServiceEndPointPort?wsdl")
//								.getFundsTransferWebServiceSoapPort("proxy_user".getBytes(), "proxy_password".getBytes());
//			
//			NameInquiryResponseData inquiryResponse = transferWebService.nameenquirysingleitem(inquiryRequest);

			// if customer name is not returned
			if (inquiryResponse.getAccountName() == null) {
				
				accountResponse.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
				accountResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
				
				return accountResponse;
			}
			
			//success
			accountResponse.setAccountName(inquiryResponse.getAccountName());
			accountResponse.setStatusCode(ResponseConstants.SUCCEESS_CODE);
			accountResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
						
			System.out.println("responseCode: " +  inquiryResponse.getResponseCode());

			return accountResponse;

		} catch (Exception ex) {
			accountResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			accountResponse.setResponseMessage("Service endpoint not available.");
			return accountResponse;
		}
	}
	
	public AccountResponse nameEnquiry(String accountNo) {
		System.out.println("\n**** In Name Enquiry ****");

		AccountResponse accountResponse = new AccountResponse();
		String name;

		try {		
			name = new AccountDBOperation().accountName(accountNo);
		} catch (Exception ex) {
			accountResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			accountResponse.setResponseMessage(ex.getMessage());
			return accountResponse;
		}

		// if customer name is not returned
		if (name == null) {

			accountResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			accountResponse.setResponseMessage("");

			return accountResponse;
		}

		if (name.isEmpty()) {

			accountResponse.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
			accountResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);

			return accountResponse;
		}

		// success
		accountResponse.setAccountName(name);
		accountResponse.setStatusCode(ResponseConstants.SUCCEESS_CODE);
		accountResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		return accountResponse;

	}
	
	public String getAccountName(String accountNumber) {
		System.out.println("**Start getAccountName");
		
		AccountResponse accountResponse = nameEnquiry(accountNumber);
		if (!accountResponse.getStatusCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			System.out.println("Could not get account name");
			System.out.println("**End getAccountName");
			return "";
		}
		
		System.out.println("success");
		System.out.println("**End getAccountName");
		return accountResponse.getAccountName();			
	}
	
	public MultiAccountResponse selectMultiAccount(String accountNo) throws Exception {
		System.out.println("\n**** In MultiAccount Info ****");
		
		MultiAccountResponse multipleAcct = new MultiAccountResponse();

		try {
			AccountDBOperation dbConnection = new AccountDBOperation();
			multipleAcct.setAccounts(dbConnection.selectMultiAccount(accountNo));
			
			//if accounts is empty, nothing was fetched cause account_no doesn't exist
			if(multipleAcct.getAccounts().isEmpty()) {
				multipleAcct.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
				multipleAcct.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
				return multipleAcct;
			}
			
			//iterate through the list of accounts
			for(AccountResponse nameInquiry : multipleAcct.getAccounts()) {
								
				//if account has no phone number
				if(nameInquiry.getAccountNumber().equals(accountNo) && 
					(nameInquiry.getPhoneNumber().equalsIgnoreCase(""))) {
					multipleAcct.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
					multipleAcct.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE + " - Phone Number");
					return multipleAcct;
				}
				
				//calculating remaining transaction limit
				nameInquiry.setDailyTransactionLimitInternal(transactionLimitInternal); //TransactionLimitInternal;
				nameInquiry.setDailyTransactionDoneInternal(dbConnection.selectDailyTranxDone(nameInquiry.getAccountNumber(), "InternalFundTransfer"));
				
				nameInquiry.setDailyTransactionLimitExternal(transactionLimitExternal); //TransactionLimitExternal;
				nameInquiry.setDailyTransactionDoneExternal(dbConnection.selectDailyTranxDone(nameInquiry.getAccountNumber(), "ExternalFundTransfer"));
				
				//dailyTransactionRemaining = transactionLimitInternal - (internalTransferDone + externalTransferDone)
				String dailyRemTrans = (Double.valueOf(transactionLimitInternal) -
						(Double.valueOf(nameInquiry.getDailyTransactionDoneInternal()) 
						+ Double.valueOf(nameInquiry.getDailyTransactionDoneExternal()))) + "";
				
				nameInquiry.setDailyTransactionRemaining(dailyRemTrans);
				
				//idealExternalTransferRemaining = dailyTransactionRemaining - (transactionLimitExternal - externalTransferDone)
				double idealExternalRemaining = (Double.valueOf(transactionLimitExternal) - Double.valueOf(nameInquiry.getDailyTransactionDoneExternal()));
				nameInquiry.setDailyTransactionRemainingInternal(nameInquiry.getDailyTransactionRemaining());
				
				String dailyRemTransExt = (idealExternalRemaining > Double.valueOf(nameInquiry.getDailyTransactionRemaining()))
											? nameInquiry.getDailyTransactionRemaining()
											: idealExternalRemaining + "";
				
				nameInquiry.setDailyTransactionRemainingExternal(dailyRemTransExt);
								
				//default all balances to empty
				nameInquiry.setLedgerBalance("");
				
				//call balance enquiry to get account balance				 
				BalanceEnquiryResponse response = new BalanceEnquiryService().balanceEnquiry(nameInquiry.getAccountNumber());
				if (response.getStatusCode().equals(ResponseConstants.SUCCEESS_CODE)) {
					nameInquiry.setLedgerBalance(response.getAvailableBalance());
		        }
			
			}
			
			//success
			multipleAcct.setStatusCode(ResponseConstants.SUCCEESS_CODE);
			multipleAcct.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
			return multipleAcct;
			
		} catch (Exception ex) {
			multipleAcct.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			multipleAcct.setResponseMessage(ex.getMessage());
			
			return multipleAcct;
		}
	}
    
	public AccountHistoryResponse accountHistory(String accountNo){
		System.out.println("\n**** In Account History ****");
		AccountHistoryResponse accountHistory = new AccountHistoryResponse();

		try {
			AccountDBOperation dbConnection = new AccountDBOperation();
			accountHistory.setAccountHistory(dbConnection.selectAccountHistory(accountNo));
			
			if(accountHistory.getAccountHistory().isEmpty()) {
				accountHistory.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
				accountHistory.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
				
				return accountHistory;
			}
			
			//success
			accountHistory.setStatusCode(ResponseConstants.SUCCEESS_CODE);
			accountHistory.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
			
			return accountHistory;
		} catch (Exception ex) {
			accountHistory.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			accountHistory.setResponseMessage(ex.getMessage());
			
			return accountHistory;
		}
	}
	
	public MiniStatementResponse miniStatement(String accountNo){
		System.out.println("\n**** In Mini Statement ****");
		MiniStatementResponse miniStatementResponse = new MiniStatementResponse();

		try {
			AccountDBOperation dbConnection = new AccountDBOperation();
			miniStatementResponse.setMiniStatement(dbConnection.selectMiniStatement(accountNo));
			
			if(miniStatementResponse.getMiniStatement().isEmpty()) {
				miniStatementResponse.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
				miniStatementResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
				
				return miniStatementResponse;
			}
			
			//success
			miniStatementResponse.setStatusCode(ResponseConstants.SUCCEESS_CODE);
			miniStatementResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
			
			return miniStatementResponse;
		} catch (Exception ex) {
			miniStatementResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			miniStatementResponse.setResponseMessage(ex.getMessage());
			
			return miniStatementResponse;
		}
	}
	
	
	public static void main(String[] args) {
		AccountResponse  accountResponse = new AccountService().nameEnquiry("3002000010");
		
		System.out.println("Name: " + CommonMethods.ObjectToJsonString(accountResponse));
	}
	
}
