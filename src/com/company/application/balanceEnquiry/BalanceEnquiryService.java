package com.company.application.balanceEnquiry;

import java.math.BigDecimal;
import java.net.URL;

import com.company.application.account.AccountService;
import com.company.application.account.data.AccountResponse;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.supernova.ws.server.account.AccountWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.account.AccountWebServiceStub;
import com.neptunesoftware.supernova.ws.server.account.data.BalanceEnquiryRequestData;
import com.neptunesoftware.supernova.ws.server.account.data.BalanceEnquiryResponseData;

public class BalanceEnquiryService {
	
	private String ipAddress;
	private String portNo;
	private String channelCode;
	
	public BalanceEnquiryService() {
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		this.ipAddress = rubikonCredential.getIpAddress();
		this.portNo = rubikonCredential.getPortNumber();
		this.channelCode = rubikonCredential.getChannelCode();
	}
	
	
	public BalanceEnquiryResponse balanceEnquiryRubikon(String accountNo) {
		System.out.println("\n**** In Balance Enquiry ****");
		
		BalanceEnquiryResponse balanceEnquiryResponse = new BalanceEnquiryResponse();

		try {
			
			// Balance enquiry parameters
			BalanceEnquiryRequestData balEnqRequest = new BalanceEnquiryRequestData();
			balEnqRequest.setSessionId(String.valueOf(System.currentTimeMillis()));
			balEnqRequest.setDestinationInstitutionCode("");
			balEnqRequest.setChannelCode(channelCode);
			balEnqRequest.setAuthorizationCode("");
			balEnqRequest.setTargetAccountName("");
			balEnqRequest.setTargetBankVerificationNumber("");
			balEnqRequest.setTargetAccountNumber(accountNo);
			
			
			URL url = new URL("http://" + ipAddress + ":" + portNo + "/supernovaws/AccountWebServiceEndPointPort?wsdl");
			AccountWebServiceStub accountWebServiceStub = new AccountWebServiceEndPointPort(url).getAccountWebServiceStubPort();
			BalanceEnquiryResponseData balEnqResponse = accountWebServiceStub.balanceenquiry(balEnqRequest);
			
//			AccountWebService accountWebService = new AccountWebServiceEndPointPort_Impl(
//						"http://" + ipAddress + ":" + portNo + "/supernovaws/AccountWebServiceEndPointPort?wsdl")
//								.getAccountWebServiceSoapPort("proxy_user".getBytes(), "proxy_password".getBytes());
//			
//			BalanceEnquiryResponseData balEnqResponse = accountWebService.balanceenquiry(balEnqRequest);

			// if 00 is not returned as responseCode from balance inquiry, return error response
			String status = balEnqResponse.getResponseCode() == null ? "-9990" : balEnqResponse.getResponseCode() + "";
			if (!status.equalsIgnoreCase("00")) {
				
				balanceEnquiryResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
				balanceEnquiryResponse.setResponseMessage("Service endpoint not available.");

				return balanceEnquiryResponse;
			}
			
			// accountName
			String AccountName = balEnqResponse.getTargetAccountName() == null 
									? "" : balEnqResponse.getTargetAccountName();
			
			// do a name enquiry to collect the name and bvn since
			// balance enquiry doesn't return the name and bvn
			if (AccountName.isEmpty()) {
				AccountResponse accountResponse = new AccountService().nameEnquiry(accountNo);
				if (accountResponse.getStatusCode().equals(ResponseConstants.SUCCEESS_CODE)) {
					AccountName = accountResponse.getAccountName();
				}
			}

			balanceEnquiryResponse.setAccountName(AccountName);
			balanceEnquiryResponse.setAccountNumber(accountNo);
			balanceEnquiryResponse.setAvailableBalance(balEnqResponse.getAvailableBalance() + "");
			balanceEnquiryResponse.setStatusCode(ResponseConstants.SUCCEESS_CODE);
			balanceEnquiryResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

			return balanceEnquiryResponse;
			
		} catch (Exception ex) {
			balanceEnquiryResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			balanceEnquiryResponse.setResponseMessage("Service endpoint not available.");

			return balanceEnquiryResponse;
		}
	}

	public BalanceEnquiryResponse balanceEnquiry(String accountNo) {
		System.out.println("\n**** In Balance Enquiry ****");

		BalanceEnquiryResponse balanceEnquiryResponse = new BalanceEnquiryResponse();

		String avalableBalance = "";
		try {
			BalanceEnquiryDBOperation database = new BalanceEnquiryDBOperation();
			avalableBalance = database.accountBalance(accountNo);
		} catch (Exception ex) {
			balanceEnquiryResponse.setStatusCode(ResponseConstants.EXCEPTION_CODE);
			balanceEnquiryResponse.setResponseMessage(ex.getMessage());

			return balanceEnquiryResponse;
		}

		if (avalableBalance.equals("00")) {
			balanceEnquiryResponse.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
			balanceEnquiryResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);

			return balanceEnquiryResponse;
		}

		if (avalableBalance.isEmpty()) {
			balanceEnquiryResponse.setStatusCode(ResponseConstants.NOT_FOUND_CODE);
			balanceEnquiryResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);

			return balanceEnquiryResponse;
		}

		// accountName
		String AccountName = "";
		AccountResponse accountResponse = new AccountService().nameEnquiry(accountNo);
		if (accountResponse.getStatusCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			AccountName = accountResponse.getAccountName();
		}

		// success
		balanceEnquiryResponse.setAccountName(AccountName);
		balanceEnquiryResponse.setAccountNumber(accountNo);
		balanceEnquiryResponse.setAvailableBalance(avalableBalance);
		balanceEnquiryResponse.setStatusCode(ResponseConstants.SUCCEESS_CODE);
		balanceEnquiryResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		return balanceEnquiryResponse;

	}
	
	public boolean hasSufficientFunds(String accountNumber, String transactionAmount){
		System.out.println("**Start HasSufficientFunds");
				
		//call balance enquiry to get account balance				 
		BalanceEnquiryResponse balanceResponse = balanceEnquiry(accountNumber);
		if (!balanceResponse.getStatusCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			System.out.println("Could not verify account balance");
			System.out.println("**End HasSufficientFunds");
			return false;
        }
		
		//return insufficient funds when transaction amount is less than available balance
		BigDecimal availableBalance = new BigDecimal(balanceResponse.getAvailableBalance());
		if(availableBalance.compareTo(new BigDecimal(transactionAmount)) < 0) {
			
			System.out.println("Available balance is less than Transaction Amount.");
			System.out.println("**End HasSufficientFunds");
			return false;
		}

		System.out.println("success");
		System.out.println("**End HasSufficientFunds");
		return true;
	}
	
	public static void main(String[] args) {
		BalanceEnquiryResponse  balanceEnquiryResponse = new BalanceEnquiryService().balanceEnquiry("3002000010");
		
		System.out.println("balance: " + CommonMethods.ObjectToJsonString(balanceEnquiryResponse));
		//System.out.println("balance: " + balanceEnquiryResponse.getAvailableBalance());
	}
	
}
