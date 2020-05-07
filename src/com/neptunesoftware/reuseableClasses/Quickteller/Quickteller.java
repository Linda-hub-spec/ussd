package com.neptunesoftware.reuseableClasses.Quickteller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.CypherCrypt;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.AccountReceivable;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Beneficiary;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.FundTransferRequest;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Initiation;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Sender;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Termination;
import com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice.BillPaymentAdviceRequest;
import com.neptunesoftware.reuseableClasses.Quickteller.data.QuicktellerCredential;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.reuseableClasses.WebserviceCall.HttpResponse;
import com.neptunesoftware.reuseableClasses.WebserviceCall.WebserviceCall;


public class Quickteller {

	private QuicktellerConstants quicktellerConstants;
	private WebserviceCall webserviceCall;
	
	
	public Quickteller() {
		QuicktellerCredential quicktellerCredential = readConfig();
		
		if(quicktellerCredential.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE))
			this.quicktellerConstants = new QuicktellerConstants()
											.baseUrl(quicktellerCredential.getBaseUrl())
											.clientId(quicktellerCredential.getClientId())
											.clientSecret(quicktellerCredential.getClientSecret())
											.transferCodePrefix(quicktellerCredential.getTransferCodePrefix())
											.terminalId(quicktellerCredential.getTerminalId())
											.initiatingEntityCode(quicktellerCredential.getInitiatingEntityCode())
											.signatureMethod(quicktellerCredential.getSignatureMethod());
		else
			this.quicktellerConstants = new QuicktellerConstants();
		
		this.webserviceCall = new WebserviceCall(quicktellerConstants.getBaseUrl());
	}
	
	public Quickteller(String baseUrl, String clientId, String clientSecret, String initiatingEntityCode,
    		String transferCodePrefix, String terminalId, String signatureMethod) {
		
		this.quicktellerConstants = new QuicktellerConstants(baseUrl, clientId, clientSecret, initiatingEntityCode,
	    													transferCodePrefix, terminalId, signatureMethod);
		this.webserviceCall = new WebserviceCall(quicktellerConstants.getBaseUrl());
	}
	
    public Quickteller baseUrl(String baseUrl) {
    	this.quicktellerConstants = quicktellerConstants.baseUrl(baseUrl);
    	return this;
    }
    
    public Quickteller clientId(String clientId) {
    	this.quicktellerConstants = quicktellerConstants.clientId(clientId);
    	return this;
    }
    
    public Quickteller clientSecret(String clientSecret) {
    	this.quicktellerConstants = quicktellerConstants.clientSecret(clientSecret);
    	return this;
    }
        
    public Quickteller initiatingEntityCode(String initiatingEntityCode) {
    	this.quicktellerConstants = quicktellerConstants.initiatingEntityCode(initiatingEntityCode);
    	return this;
    }
    
    public Quickteller transferCodePrefix(String transferCodePrefix) {
    	this.quicktellerConstants = quicktellerConstants.transferCodePrefix(transferCodePrefix);
    	return this;
    }
    
    public Quickteller terminalId(String terminalId) {
    	this.quicktellerConstants = quicktellerConstants.terminalId(terminalId);
    	return this;
    }
    
    public Quickteller signatureMethod(String signatureMethod) {
    	this.quicktellerConstants = quicktellerConstants.signatureMethod(signatureMethod);
    	return this;
    }
    
	
    public String getClientId() {
    	return this.quicktellerConstants.getBaseUrl();
    }
	
//	public static void main(String[] args) {
//		
//		Quickteller quickteller = new Quickteller();
//				//.baseUrl("hello").clientId("hi").clientSecret("there");
//		
//		System.out.println("ClientId: " + quickteller.getClientId());
//	}
//	
	
	
	
	
	
	
	
	//******* GET methods *********
	
	public String getBankCodes(){
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.GET_BANK_CODE_URL, QuicktellerConstants.GET);
		
		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.GET_BANK_CODE_URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillers(){
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.GET_BILLER_URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.GET_BILLER_URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillerCategories() {

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.GET_BILLER_CATEGORIES_URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.GET_BILLER_CATEGORIES_URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillersByCategory(String id) {
		
		String URL = QuicktellerConstants.GET_BILLER_BY_CATEGORY_URL_PREFIX + id + QuicktellerConstants.GET_BILLER_BY_CATEGORY_URL_SUFFIX;

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillersPaymentItems(String billerId) {
		
		String URL = QuicktellerConstants.GET_BILLER_PAYMENT_ITEMS_URL_PREFIX + billerId
				+ QuicktellerConstants.GET_BILLER_PAYMENT_ITEMS_URL_SUFFIX;

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String queryTransaction(String requestreference) {
		// WebserviceCall webserviceCall = new WebserviceCall();

		String URL = QuicktellerConstants.QUERY_TRANSACTION_URL + requestreference;

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = new HttpResponse();
		try {
			httpResponse = webserviceCall.getMethod(URL, extraHeaders);
		} catch (Exception e) {}

		String resp = httpResponse.getResponseBody() == null ? "" : httpResponse.getResponseBody();
		return resp;
	}
	
	public String nameEnquiry(String bankCode, String accountNo) {
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.NAME_ENQUIRY_URL, QuicktellerConstants.GET);
		extraHeaders.put("bankCode", bankCode);
		extraHeaders.put("accountId", accountNo);

		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.NAME_ENQUIRY_URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	
	
	
	//******* POST methods *********	
	
	public String fundTransfer(String beneficiaryAcctNumber, String beneficiaryName, String amount, 
			String beneficiaryBankCode, String senderName) {
		
		FundTransferRequest fundTransferRequest = createFundTransferRequest(beneficiaryAcctNumber, 
				beneficiaryName, amount, beneficiaryBankCode, senderName);
		
		String fundTransferRequestStr = CommonMethods.ObjectToJsonString(fundTransferRequest);

		HttpResponse httpResponse = fundTransferService(fundTransferRequestStr);
		
		return httpResponse.getResponseBody();
		
	}
	
	public String sendBillPaymentAdvice(String paymentCode, String customerId, String customerMobile, 
			String customerEmail, String amount, AtomicReference<String> requestReference) {
		
		BillPaymentAdviceRequest billPaymentAdviceRequest = createBillPaymentAdviceRequest(paymentCode, customerId, 
				customerMobile, customerEmail, amount);
		
		requestReference.set(billPaymentAdviceRequest.requestReference);
		
		String billPaymentAdviceRequestStr = CommonMethods.ObjectToJsonString(billPaymentAdviceRequest);
		
		HttpResponse httpResponse = sendBillPaymentAdviceInterswitch(billPaymentAdviceRequestStr);
		
		return httpResponse.getResponseBody();
		
	}
	
	public String customerValidation(String body) throws Exception {
		// WebserviceCall webserviceCall = new WebserviceCall();

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.CUSTOMER_VALIDATION_URL, QuicktellerConstants.POST);

		HttpResponse httpResponse = webserviceCall.postMethod(QuicktellerConstants.CUSTOMER_VALIDATION_URL, body, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}
	
	
	
	
	
	
	//*** used by fundTransfer ****
	public FundTransferRequest createFundTransferRequest(String beneficiaryAcctNumber,
			String beneficiaryName, String amount, String beneficiaryBankCode, String senderName) {
		FundTransferRequest fundTransfer = null;

		try {
		
			fundTransfer = new FundTransferRequest();

			Beneficiary beneficiary = new Beneficiary("", "", beneficiaryName, beneficiaryName);
			Initiation initiation = new Initiation(amount, QuicktellerConstants.CURRENCY_CODE_NUMBER,
					QuicktellerConstants.INITIATING_PAYMENT_METHOD_CODE, QuicktellerConstants.CHANNEL_LOCATION);
			Sender sender = new Sender("", "", senderName, senderName);

			AccountReceivable accountReceivable = new AccountReceivable(beneficiaryAcctNumber,
					QuicktellerConstants.ACCOUNT_TYPE_DEFAULT);
			Termination termination = new Termination(amount, beneficiaryBankCode, QuicktellerConstants.CURRENCY_CODE_NUMBER,
					QuicktellerConstants.TERMINATING_PAYMENT_METHOD_CODE, QuicktellerConstants.COUNTRY_CODE);
			termination.setAccountReceivable(accountReceivable);

			// set the MAC value for the request object
			String macCipher = "" + initiation.getAmount() + initiation.getCurrencyCode()
					+ initiation.getPaymentMethodCode() + termination.getTerminationAmount()
					+ termination.getTerminationCurrencyCode() + termination.getTerminationPaymentMethodCode()
					+ termination.getTerminationCountryCode();
			String MAC = QuicktellerConstants.SHA512(macCipher);

			fundTransfer.mac = MAC;
			fundTransfer.beneficiary = beneficiary;
			fundTransfer.initiatingEntityCode = quicktellerConstants.getInitiatingEntityCode();
			fundTransfer.initiation = initiation;
			fundTransfer.sender = sender;
			fundTransfer.termination = termination;
			fundTransfer.transferCode = quicktellerConstants.getTransferCodePrefix() + QuicktellerConstants.timeStamp();

			return fundTransfer;
			

		} catch (Exception e) {
			System.out.println("Service Endpoint Unavailable.");
			return fundTransfer;
		}
	}
	
	private HttpResponse fundTransferService(String body) {
		
		HttpResponse httpResponse = new HttpResponse();
		try {
			FundTransferRequest fundTransferReq = (FundTransferRequest) CommonMethods.JSONStringToObject(body, FundTransferRequest.class);

			// set the MAC value for the request object
			fundTransferReq.mac = generateMAC(fundTransferReq);

			// create a json string from the request object
			String request = CommonMethods.ObjectToJsonString(fundTransferReq);

			HashMap<String, String> extraHeaders = new HashMap<String, String>();
			extraHeaders = commonHeaders(QuicktellerConstants.FUNDS_TRANSFER_URL, QuicktellerConstants.POST);

			httpResponse = webserviceCall.postMethod(QuicktellerConstants.FUNDS_TRANSFER_URL, request, extraHeaders);

			// String resp = httpResponse.getResponseBody();
			return httpResponse;

		} catch (Exception ex) {
			return null;
		}

	}
	
	private String generateMAC(FundTransferRequest fundTransfer){
		try {
			// collect the iniatiation object
			Initiation initiation = new Initiation();
			initiation = fundTransfer.initiation;

			// collect the termination object
			Termination termination = new Termination();
			termination = fundTransfer.termination;

			// compute the MAC cipher
			String macCipher = "" + initiation.getAmount() + initiation.getCurrencyCode()
					+ initiation.getPaymentMethodCode() + termination.getTerminationAmount()
					+ termination.getTerminationCurrencyCode() + termination.getTerminationPaymentMethodCode()
					+ termination.getTerminationCountryCode();

			// encode MAC cipher
			return QuicktellerConstants.SHA512(macCipher);
			//return QuicktellerConstants.SHAHashValue(macCipher, "SHA-512");

		} catch (Exception ex) {
			return "";
		}
	}
		
	
	//*** used by sendBillPaymentAdvice ****
	public BillPaymentAdviceRequest createBillPaymentAdviceRequest(String paymentCode,
			String customerId, String customerMobile, String customerEmail, String amount) {
		
		BillPaymentAdviceRequest BPA = null;

		try {
			BPA = new BillPaymentAdviceRequest();

			BPA.terminalId = quicktellerConstants.getTerminalId();
			BPA.paymentCode = paymentCode;
			BPA.customerId = customerId;
			BPA.customerMobile = customerMobile;
			BPA.customerEmail = customerEmail;
			BPA.amount = amount;
			BPA.requestReference = quicktellerConstants.getTransferCodePrefix() + QuicktellerConstants.timeStamp().substring(2);

			return BPA;

		} catch (Exception e) {
			System.out.println("Service Endpoint Unavailable.");
			return BPA;
		}
	}
	
	private HttpResponse sendBillPaymentAdviceInterswitch(String request) {
		//This method is used by BillspAyment and AirtimeRecharge
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.SEND_BILL_PAYMENT_ADVICE_URL, QuicktellerConstants.POST);

		HttpResponse httpResponse = webserviceCall.postMethod(QuicktellerConstants.SEND_BILL_PAYMENT_ADVICE_URL, request, extraHeaders);

		//String resp = httpResponse.getResponseBody();
		return httpResponse;
	}

	
	//*** used by all GET and POST methods
	private HashMap<String, String> commonHeaders(String path, String httpMethod){
		
		String url = quicktellerConstants.getBaseUrl() + path;
		
		String encodedResourceUrl = "";
		try {
			encodedResourceUrl = URLEncoder.encode(url, "ISO-8859-1"); // new String(url.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {}
		
		String timestamp = QuicktellerConstants.timeStamp();
		String nonce = QuicktellerConstants.nonce();
		
		String signatureCipher = httpMethod + "&" + encodedResourceUrl + "&" + timestamp + "&" + nonce + "&" 
								+ quicktellerConstants.getClientId() + "&" + quicktellerConstants.getClientSecret();
		
		String signature = QuicktellerConstants.signature(signatureCipher, quicktellerConstants.getSignatureMethod());
		
		
		HashMap<String, String> commonHeaders = new HashMap<String, String>();
		
		commonHeaders.put("TerminalId", quicktellerConstants.getTerminalId());
				
		commonHeaders.put("Content-Type", QuicktellerConstants.CONTENT_TYPE);
		
		commonHeaders.put("Authorization", quicktellerConstants.getAuthorization());
		
		commonHeaders.put("Timestamp", timestamp);

		commonHeaders.put("Nonce", nonce);

		commonHeaders.put("SignatureMethod", quicktellerConstants.getSignatureMethod());

		commonHeaders.put("Signature", signature);
		
		return commonHeaders;
	}
	
	
	//*** used by constructor//quicktellerCredential = decryptContent(quicktellerCredential)
	public QuicktellerCredential readConfig() {
		
		QuicktellerCredential quicktellerCredential = new QuicktellerCredential();
		
		try {
		String content = CommonMethods.getInfo("QuicktellerInfo.xml", Quickteller.class);
		
		quicktellerCredential = CommonMethods.xmlStringToObject(content, QuicktellerCredential.class);
		
		quicktellerCredential = decryptContent(quicktellerCredential);
		
		quicktellerCredential.setResponseCode(ResponseConstants.SUCCEESS_CODE);
				
		} catch(Exception e) {
			System.out.println("Cannot read QuicktellerInfo.xml");
			quicktellerCredential.setResponseCode(ResponseConstants.FILE_ERROR_CODE);
		}
		
		return quicktellerCredential;
	}
	
	private QuicktellerCredential decryptContent(QuicktellerCredential quicktellerCredential) {
		QuicktellerCredential quicktellerCred = quicktellerCredential;
		try {
			String baseUrl = CypherCrypt.deCypher(quicktellerCredential.getBaseUrl()) == null || CypherCrypt.deCypher(quicktellerCredential.getBaseUrl()).equals("")
					? quicktellerCredential.getBaseUrl() : CypherCrypt.deCypher(quicktellerCredential.getBaseUrl());
					
			String clientId = CypherCrypt.deCypher(quicktellerCredential.getClientId()) == null || CypherCrypt.deCypher(quicktellerCredential.getClientId()).equals("")
					? quicktellerCredential.getClientId() : CypherCrypt.deCypher(quicktellerCredential.getClientId());
					
			String clientSecret = CypherCrypt.deCypher(quicktellerCredential.getClientSecret()) == null || CypherCrypt.deCypher(quicktellerCredential.getClientSecret()).equals("")
					? quicktellerCredential.getClientSecret() : CypherCrypt.deCypher(quicktellerCredential.getClientSecret());
					
			String initiatingEntityCode = CypherCrypt.deCypher(quicktellerCredential.getInitiatingEntityCode()) == null || CypherCrypt.deCypher(quicktellerCredential.getInitiatingEntityCode()).equals("")
					? quicktellerCredential.getInitiatingEntityCode() : CypherCrypt.deCypher(quicktellerCredential.getInitiatingEntityCode());
					
			String signatureMethod = CypherCrypt.deCypher(quicktellerCredential.getSignatureMethod()) == null || CypherCrypt.deCypher(quicktellerCredential.getSignatureMethod()).equals("")
					? quicktellerCredential.getSignatureMethod() : CypherCrypt.deCypher(quicktellerCredential.getSignatureMethod());
					
			String terminalId = CypherCrypt.deCypher(quicktellerCredential.getTerminalId()) == null || CypherCrypt.deCypher(quicktellerCredential.getTerminalId()).equals("")
					? quicktellerCredential.getTerminalId() : CypherCrypt.deCypher(quicktellerCredential.getTerminalId());
					
			String transferCodePrefix = CypherCrypt.deCypher(quicktellerCredential.getTransferCodePrefix()) == null || CypherCrypt.deCypher(quicktellerCredential.getTransferCodePrefix()).equals("")
					? quicktellerCredential.getTransferCodePrefix() : CypherCrypt.deCypher(quicktellerCredential.getTransferCodePrefix());
			
			quicktellerCredential.setBaseUrl(baseUrl);
			quicktellerCredential.setClientId(clientId);
			quicktellerCredential.setClientSecret(clientSecret);
			quicktellerCredential.setInitiatingEntityCode(initiatingEntityCode);
			quicktellerCredential.setSignatureMethod(signatureMethod);
			quicktellerCredential.setTerminalId(terminalId);
			quicktellerCredential.setTransferCodePrefix(transferCodePrefix);
			
			return quicktellerCredential;
			
		} catch (Exception e) {
			System.out.println("QuicktellerCredential: \n" + CommonMethods.objectToXml(quicktellerCredential));
			System.out.println("Cannot decrypt content");
			
			return quicktellerCred;
		}
		
	}
	
	
	
	public static RubikonCredential readRubikonConfig() {
		
		RubikonCredential rubikonCredential = new RubikonCredential();
		
		try {
		String content = CommonMethods.getInfo("RubikonInfo.xml", Quickteller.class);
		
		rubikonCredential = CommonMethods.xmlStringToObject(content, RubikonCredential.class);
		
		rubikonCredential = decryptContent(rubikonCredential);
		
  		rubikonCredential.setResponseCode(ResponseConstants.SUCCEESS_CODE);
				
		} catch(Exception e) {
			System.out.println("Cannot read RubikonInfo.xml");
			rubikonCredential.setResponseCode(ResponseConstants.FILE_ERROR_CODE);
		}
		
		return rubikonCredential;
	}
	
	private static RubikonCredential decryptContent(RubikonCredential rubikonCredential) {
		RubikonCredential rubikonCred = rubikonCredential;
		try {
			String ipAddress = CypherCrypt.deCypher(rubikonCredential.getIpAddress()) == null || CypherCrypt.deCypher(rubikonCredential.getIpAddress()).equals("")
					? rubikonCredential.getIpAddress() : CypherCrypt.deCypher(rubikonCredential.getIpAddress());
					
			String portNumber = CypherCrypt.deCypher(rubikonCredential.getPortNumber()) == null || CypherCrypt.deCypher(rubikonCredential.getPortNumber()).equals("")
					? rubikonCredential.getPortNumber() : CypherCrypt.deCypher(rubikonCredential.getPortNumber());
					
			String channelId = CypherCrypt.deCypher(rubikonCredential.getChannelId()) == null || CypherCrypt.deCypher(rubikonCredential.getChannelId()).equals("")
					? rubikonCredential.getChannelId() : CypherCrypt.deCypher(rubikonCredential.getChannelId());
					
			String channelCode = CypherCrypt.deCypher(rubikonCredential.getChannelCode()) == null || CypherCrypt.deCypher(rubikonCredential.getChannelCode()).equals("")
					? rubikonCredential.getChannelCode() : CypherCrypt.deCypher(rubikonCredential.getChannelCode());
					
			String transactionFee = CypherCrypt.deCypher(rubikonCredential.getTransactionFee()) == null || CypherCrypt.deCypher(rubikonCredential.getTransactionFee()).equals("")
					? rubikonCredential.getTransactionFee() : CypherCrypt.deCypher(rubikonCredential.getTransactionFee());
					
			String chargeCode = CypherCrypt.deCypher(rubikonCredential.getChargeCode()) == null || CypherCrypt.deCypher(rubikonCredential.getChargeCode()).equals("")
					? rubikonCredential.getChargeCode() : CypherCrypt.deCypher(rubikonCredential.getChargeCode());
					
			String taxCode = CypherCrypt.deCypher(rubikonCredential.getTaxCode()) == null || CypherCrypt.deCypher(rubikonCredential.getTaxCode()).equals("")
					? rubikonCredential.getTaxCode() : CypherCrypt.deCypher(rubikonCredential.getTaxCode());
					
			String currencyCode = CypherCrypt.deCypher(rubikonCredential.getCurrencyCode()) == null || CypherCrypt.deCypher(rubikonCredential.getCurrencyCode()).equals("")
					? rubikonCredential.getCurrencyCode() : CypherCrypt.deCypher(rubikonCredential.getCurrencyCode());
			
					
			String creditTransCode = CypherCrypt.deCypher(rubikonCredential.getCreditTransCode()) == null || CypherCrypt.deCypher(rubikonCredential.getCreditTransCode()).equals("")
					? rubikonCredential.getCreditTransCode() : CypherCrypt.deCypher(rubikonCredential.getCreditTransCode());
							
			String debitTransCode = CypherCrypt.deCypher(rubikonCredential.getDebitTransCode()) == null || CypherCrypt.deCypher(rubikonCredential.getDebitTransCode()).equals("")
					? rubikonCredential.getDebitTransCode() : CypherCrypt.deCypher(rubikonCredential.getDebitTransCode());				
			
			String internalAcctTransfer = CypherCrypt.deCypher(rubikonCredential.getInternalAcctTransfer()) == null || CypherCrypt.deCypher(rubikonCredential.getInternalAcctTransfer()).equals("")
							? rubikonCredential.getInternalAcctTransfer(): CypherCrypt.deCypher(rubikonCredential.getInternalAcctTransfer());
					
					
			String externalAcctTransReversal = CypherCrypt.deCypher(rubikonCredential.getExternalAcctTransReversal()) == null || CypherCrypt.deCypher(rubikonCredential.getExternalAcctTransReversal()).equals("")
					? rubikonCredential.getExternalAcctTransReversal() : CypherCrypt.deCypher(rubikonCredential.getExternalAcctTransReversal());
					
			String externalAcctTransfer = CypherCrypt.deCypher(rubikonCredential.getExternalAcctTransfer()) == null || CypherCrypt.deCypher(rubikonCredential.getExternalAcctTransfer()).equals("")
					? rubikonCredential.getExternalAcctTransfer() : CypherCrypt.deCypher(rubikonCredential.getExternalAcctTransfer());
					
			String billsPaymentCredit = CypherCrypt.deCypher(rubikonCredential.getBillsPaymentCredit()) == null || CypherCrypt.deCypher(rubikonCredential.getBillsPaymentCredit()).equals("")
					? rubikonCredential.getBillsPaymentCredit() : CypherCrypt.deCypher(rubikonCredential.getBillsPaymentCredit());
					
			String billsPaymentDebit= CypherCrypt.deCypher(rubikonCredential.getBillsPaymentDebit()) == null || CypherCrypt.deCypher(rubikonCredential.getBillsPaymentDebit()).equals("")
					? rubikonCredential.getBillsPaymentDebit() : CypherCrypt.deCypher(rubikonCredential.getBillsPaymentDebit());
					
			String mobileRechargeCredit = CypherCrypt.deCypher(rubikonCredential.getMobileRechargeCredit()) == null || CypherCrypt.deCypher(rubikonCredential.getMobileRechargeCredit()).equals("")
					? rubikonCredential.getMobileRechargeCredit() : CypherCrypt.deCypher(rubikonCredential.getMobileRechargeCredit());
					
			String mobileRechargeDebit = CypherCrypt.deCypher(rubikonCredential.getMobileRechargeDebit()) == null || CypherCrypt.deCypher(rubikonCredential.getMobileRechargeDebit()).equals("")
					? rubikonCredential.getMobileRechargeDebit() : CypherCrypt.deCypher(rubikonCredential.getMobileRechargeDebit());
					
			String authenticationUsername = CypherCrypt.deCypher(rubikonCredential.getAuthenticatedUsername()) == null || CypherCrypt.deCypher(rubikonCredential.getAuthenticatedUsername()).equals("")
					? rubikonCredential.getAuthenticatedUsername() : CypherCrypt.deCypher(rubikonCredential.getAuthenticatedUsername());
					
			String authenticationPassword = CypherCrypt.deCypher(rubikonCredential.getAuthenticatedPassword()) == null || CypherCrypt.deCypher(rubikonCredential.getAuthenticatedPassword()).equals("")
					? rubikonCredential.getAuthenticatedPassword() : CypherCrypt.deCypher(rubikonCredential.getAuthenticatedPassword());
					
			String transferLimitInternal = CypherCrypt.deCypher(rubikonCredential.getTransferLimitInternal()) == null || CypherCrypt.deCypher(rubikonCredential.getTransferLimitInternal()).equals("")
					? rubikonCredential.getTransferLimitInternal() : CypherCrypt.deCypher(rubikonCredential.getTransferLimitInternal());
					
			String transferLimitExternal = CypherCrypt.deCypher(rubikonCredential.getTransferLimitExternal()) == null || CypherCrypt.deCypher(rubikonCredential.getTransferLimitExternal()).equals("")
					? rubikonCredential.getTransferLimitExternal() : CypherCrypt.deCypher(rubikonCredential.getTransferLimitExternal());
					
			String applicationUsername = CypherCrypt.deCypher(rubikonCredential.getApplicationUsername()) == null || CypherCrypt.deCypher(rubikonCredential.getApplicationUsername()).equals("")
					? rubikonCredential.getApplicationUsername() : CypherCrypt.deCypher(rubikonCredential.getApplicationUsername());
			
			String chargeCodeBillsPayment = CypherCrypt.deCypher(rubikonCredential.getChargeCodeBillsPayment()) == null || CypherCrypt.deCypher(rubikonCredential.getChargeCodeBillsPayment()).equals("")
					? rubikonCredential.getChargeCodeBillsPayment() : CypherCrypt.deCypher(rubikonCredential.getChargeCodeBillsPayment());
					
			String deditTransfer = CypherCrypt.deCypher(rubikonCredential.getDeditTransfer()) == null || CypherCrypt.deCypher(rubikonCredential.getDeditTransfer()).equals("")
					? rubikonCredential.getDeditTransfer() : CypherCrypt.deCypher(rubikonCredential.getDeditTransfer());
			
			String creditTransfer = CypherCrypt.deCypher(rubikonCredential.getCreditTransfer()) == null || CypherCrypt.deCypher(rubikonCredential.getCreditTransfer()).equals("")
					? rubikonCredential.getCreditTransfer() : CypherCrypt.deCypher(rubikonCredential.getCreditTransfer());
					
			//ExternalAcctTransReversal
			rubikonCredential.setIpAddress(ipAddress);
			rubikonCredential.setPortNumber(portNumber);
			rubikonCredential.setChannelId(channelId);
			rubikonCredential.setChannelCode(channelCode);
			rubikonCredential.setTransactionFee(transactionFee);
			rubikonCredential.setChargeCode(chargeCode);
			rubikonCredential.setTaxCode(taxCode);
			rubikonCredential.setCurrencyCode(currencyCode);
			rubikonCredential.setCreditTransCode(creditTransCode);//
			rubikonCredential.setDebitTransCode(debitTransCode);//
			rubikonCredential.setInternalAcctTransfer(internalAcctTransfer);//
			rubikonCredential.setExternalAcctTransfer(externalAcctTransfer);//
			rubikonCredential.setExternalAcctTransReversal(externalAcctTransReversal);//
			rubikonCredential.setDeditTransfer(deditTransfer);//
			rubikonCredential.setCreditTransfer(creditTransfer);//
			rubikonCredential.setBillsPaymentCredit(billsPaymentCredit);
			rubikonCredential.setBillsPaymentDebit(billsPaymentDebit);
			rubikonCredential.setMobileRechargeCredit(mobileRechargeCredit);
			rubikonCredential.setMobileRechargeDebit(mobileRechargeDebit);
			rubikonCredential.setAuthenticatedUsername(authenticationUsername);
			rubikonCredential.setAuthenticatedPassword(authenticationPassword);
			rubikonCredential.setTransferLimitInternal(transferLimitInternal);
			rubikonCredential.setTransferLimitExternal(transferLimitExternal);
			rubikonCredential.setApplicationUsername(applicationUsername);
			rubikonCredential.setChargeCodeBillsPayment(chargeCodeBillsPayment);
			
			return rubikonCredential;
			
		} catch (Exception e) {
			System.out.println("RubikonCredential: \n" + CommonMethods.objectToXml(rubikonCredential));
			System.out.println("Cannot decrypt content");
			
			return rubikonCred;
		}		
		
	}
	
	
	public static void main(String[] args) {
		//QuicktellerCredential credentials = new QuicktellerCredential();
		//credentials = new Quickteller().readConfig();
		//System.out.println(CommonMethods.ObjectToJsonString(new Quickteller().getBankCodes()));
		
		System.out.println(CommonMethods.ObjectToJsonString(new Quickteller().fundTransfer("9999999999", 
				"Anari Sammy", "100000", "011", "Testing Test")));
		
	}
	
}
