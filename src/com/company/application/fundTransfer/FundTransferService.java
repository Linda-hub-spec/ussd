package com.company.application.fundTransfer;

import java.util.Date;

import com.company.application.account.AccountService;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.company.application.fundTransfer.data.ExternalFTRequest;
import com.company.application.fundTransfer.data.FundTransferDBRequest;
import com.company.application.fundTransfer.data.InternalFTRequest;
import com.google.gson.Gson;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.FundTransferRequest;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.FundTransferResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction.QueryTransactionResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.reuseableClasses.WebserviceCall.HttpResponse;

public class FundTransferService {

	private String channelCode;
	private String currencyCode;
	private String fundXferDebit;

	public FundTransferService() {
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();

		this.channelCode = rubikonCredential.getChannelCode();
		this.currencyCode = rubikonCredential.getCurrencyCode();
		this.fundXferDebit = rubikonCredential.getExternalAcctTransfer();
	}

	/*
	 * This Method works with the assumption that transaction amount received is in
	 * kobo
	 */
	public ResponseModel InternalFundTransfer(String body) {
		System.out.println("\n**** Start Internal Transfer ****");

		InternalFTRequest internalTransfer = (InternalFTRequest) CommonMethods.JSONStringToObject(body,
				InternalFTRequest.class);

		FundTransferDBOperation database = new FundTransferDBOperation();

		String amount = internalTransfer.getTransactionAmount();
		String tran_Amount = CommonMethods.koboToNaira(Integer.parseInt(amount));

		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(internalTransfer.getFromAccountNumber(), tran_Amount)) {

			// return insufficient funds when transaction amount is less than available
			// balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_CODE);

			return responseModel;
			// CommonMethods.ObjectToJsonString(responseModel);
		}

		// call procedure to do deduction
		System.out.println("In Internal Fund Transfer Debit Sender And Credit Receiver");

		String narration = "Internal Transfer from #acctno " + internalTransfer.getFromAccountNumber() + " to #acctno "
				+ internalTransfer.getToAccountNumber();
		narration = narration + "\nSub Desc: " + internalTransfer.getTransactionDescription();

		FundTransferDBRequest dbRequest = new FundTransferDBRequest(internalTransfer.getFromAccountNumber(),
				tran_Amount, tran_Amount, internalTransfer.getToAccountNumber(), narration,
				internalTransfer.getChargeAmount(), internalTransfer.getTaxAmount(),
				internalTransfer.getInitiatingApp());

		String dbResponse = database.XAPI_POSTING_SERVICE_DR(dbRequest, "", fundXferDebit, "", "N", "");

		ResponseModel responseModel = new ResponseModel();
		String response = "";

		// return error if deduction was not possible
		if (dbResponse == null || !dbResponse.equals("00")) {

			responseModel.setStatusCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);

			response = CommonMethods.ObjectToJsonString(responseModel);

			// log request and responses
			CommonMethods.logContent("*** InternalFundsTransfer Request ***" + body + "\r\n\r\n"
					+ "*** InternalFundsTransfer Response ***" + response);

			return responseModel;
		}

		// success
		responseModel.setStatusCode(ResponseConstants.SUCCEESS_CODE);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		response = CommonMethods.ObjectToJsonString(responseModel);

		String transactionRef = new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|"
				+ new Date().getTime();
		try {

			// save to the db
			database.addRecord(dbRequest, dbResponse, transactionRef, "InternalFundTransfer", "DR", channelCode,
					currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}

		// log request and responses
		CommonMethods.logContent("*** InternalFundsTransfer Request ***" + body + "\r\n\r\n"
				+ "*** InternalFundsTransfer Response ***" + response);

		System.out.println("**** End Internal Transfer");
		return responseModel;

	}

	/*
	 * This Method works with the assumption that transaction amount received is in
	 * kobo
	 */

	/*
	 * public String OldExternalTransfers(String body) throws Exception {
	 * System.out.println("\n**** Start ExternalTransfers ****");
	 * 
	 * ExternalFTRequest externalTransfer = (ExternalFTRequest)
	 * CommonMethods.JSONStringToObject(body, ExternalFTRequest.class);
	 * 
	 * FundTransferDBOperation database = new FundTransferDBOperation();
	 * 
	 * 
	 * error-001 => call to INTERSWITCH failed i.e did not go through error-002 =>
	 * an exception occurred error-003 => call to INTERSWITCH returns an error
	 * object error-statusCode => call to INTERSWITCH returns a status code other
	 * than 200 or 408
	 * 
	 * 
	 * // to get sender's accountName String senderName = ""; senderName = new
	 * AccountService().getAccountName(externalTransfer.getFromAccountNumber());
	 * 
	 * // convert transaction amount to Naira String tran_Amount =
	 * CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.
	 * getTransactionAmount().trim()));
	 * 
	 * // total amount String charges =
	 * String.valueOf(Double.sum(Double.parseDouble(externalTransfer.getChargeAmount
	 * ()), Double.parseDouble(externalTransfer.getTaxAmount()))); String
	 * totalAmount = String.valueOf(Double.sum(Double.parseDouble(tran_Amount),
	 * Double.parseDouble(charges)));
	 * 
	 * 
	 * // check to make sure account has sufficient balance if (!new
	 * BalanceEnquiryService().hasSufficientFunds(externalTransfer.
	 * getFromAccountNumber(), totalAmount)) {
	 * 
	 * // return insufficient funds when transaction amount is less than available
	 * balance ResponseModel responseModel = new ResponseModel();
	 * responseModel.setStatusCode(ResponseConstants.INSUFFICIENT_CODE);
	 * responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_CODE);
	 * 
	 * return CommonMethods.ObjectToJsonString(responseModel); }
	 * 
	 * // call procedure to deduct from the senders account if
	 * (!database.callProcedure(senderName, externalTransfer, false, "", "")) {
	 * 
	 * // return error if deduction was not possible ResponseModel responseModel =
	 * new ResponseModel();
	 * responseModel.setStatusCode(ResponseConstants.PROCEDURE_CODE);
	 * responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);
	 * 
	 * return CommonMethods.ObjectToJsonString(responseModel); }
	 * 
	 * // deduction was successful proceed
	 * 
	 * // call Interswitch to carryout transfer Quickteller quickteller = new
	 * Quickteller(); HttpResponse fundTransferResponseStr =
	 * quickteller.fundTransfer(externalTransfer.getBeneficiaryAccountNumber(),
	 * externalTransfer.getBeneficiaryName(),
	 * externalTransfer.getTransactionAmount().trim(),
	 * externalTransfer.getBeneficiaryBankID(), senderName);
	 * 
	 * //Deserialize response string //FundTransferResponse fundTransferResponse =
	 * new Gson().fromJson (fundTransferResponseStr, FundTransferResponse.class);
	 * //(FundTransferResponse)CommonMethods.JSONStringToObject
	 * 
	 * 
	 * // might return error object if(!(fundTransferResponseStr.getStatusCode()==
	 * 0)) {
	 * 
	 * // call procedure to do a reversal (credit)
	 * database.callProcedureReversal(senderName, externalTransfer, true,
	 * ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, "");
	 * 
	 * // and return if it is an error object ResponseModel responseModel = new
	 * ResponseModel();
	 * responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE
	 * ); responseModel.setResponseMessage("**interswitch failed**");
	 * 
	 * return CommonMethods.ObjectToJsonString(responseModel); } Quickteller
	 * quickTeller = new Quickteller(); FundTransferRequest fundTransferRequest =
	 * new FundTransferRequest(); String beneficiaryAcctNumber =
	 * externalTransfer.getBeneficiaryAccountNumber(); String beneficiaryName =
	 * externalTransfer.getBeneficiaryName(); String amount = tran_Amount; String
	 * beneficiaryBankCode = externalTransfer.getBeneficiaryBankID();
	 * 
	 * fundTransferRequest =
	 * quickTeller.createFundTransferRequest(beneficiaryAcctNumber, beneficiaryName,
	 * amount, beneficiaryBankCode, senderName); // used when saving transaction to
	 * the db String transferCode = fundTransferRequest.getTransferCode() + "|" +
	 * new Date() + "|" + new Date().getTime();
	 * 
	 * // call query transaction to confirm transaction
	 * System.out.println("TransferCode: " + fundTransferRequest.getTransferCode());
	 * String queryTransRespStr =
	 * quickteller.queryTransaction(fundTransferRequest.getTransferCode());
	 * QueryTransactionResponse queryTransactionResponse =
	 * (QueryTransactionResponse)
	 * CommonMethods.JSONStringToObject(queryTransRespStr,
	 * QueryTransactionResponse.class);
	 * 
	 * //might return error object
	 * if(queryTransactionResponse.getTransactionResponseCode().isEmpty()) {
	 * 
	 * // call procedure to do a reversal (credit)
	 * database.callProcedureReversal(senderName, externalTransfer, true,
	 * ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);
	 * 
	 * // and return if it is an error object ResponseModel responseModel = new
	 * ResponseModel();
	 * responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE
	 * ); responseModel.setResponseMessage("**Error: \n" +queryTransRespStr);
	 * 
	 * return CommonMethods.ObjectToJsonString(responseModel); }
	 * 
	 * // successful, proceed // get transactionResponseCode and check String
	 * transactionResponseCode =
	 * queryTransactionResponse.getTransactionResponseCode(); if
	 * (!(transactionResponseCode.equals("90000") ||
	 * transactionResponseCode.equals("90010") ||
	 * transactionResponseCode.equals("90011") ||
	 * transactionResponseCode.equals("90016") ||
	 * transactionResponseCode.equals("90009") ||
	 * transactionResponseCode.equals("900A0"))) {
	 * 
	 * // call procedure to do a reversal (credit)
	 * database.callProcedure(senderName, externalTransfer, true,
	 * transactionResponseCode, transferCode);
	 * 
	 * // and return ResponseModel responseModel = new ResponseModel();
	 * responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE
	 * ); responseModel.setResponseMessage(queryTransRespStr);
	 * 
	 * return CommonMethods.ObjectToJsonString(responseModel); }
	 * 
	 * // log request and responses
	 * CommonMethods.logContent("*** ExternalFundsTransfer Request ***" + body +
	 * "\r\n\r\n" + "*** ExternalFundsTransfer Request(Interswitch) ***" +
	 * "fundTransferReqStr" + "\r\n\r\n" +
	 * "*** ExternalFundsTransfer Response(Interswitch) ***" +
	 * fundTransferResponseStr + "\r\n\r\n" + "*** QueryTransaction Response ***" +
	 * queryTransRespStr);
	 * 
	 * System.out.println("\n**** End ExternalTransfers(finish) ****");
	 * 
	 * // success ResponseModel responseModel = new ResponseModel();
	 * responseModel.setStatusCode(ResponseConstants.SUCCEESS_CODE);
	 * responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
	 * 
	 * return CommonMethods.ObjectToJsonString(responseModel);
	 * 
	 * }
	 */

	// new
	public String ExternalTransfers(String body) throws Exception {
		System.out.println("\n*** Start ExternalTransfers ***");

		ExternalFTRequest externalTransfer = (ExternalFTRequest) CommonMethods.JSONStringToObject(body,
				ExternalFTRequest.class);

		FundTransferDBOperation database = new FundTransferDBOperation();

		/*
		 * error-001 => call to INTERSWITCH failed i.e did not go through error-002 =>
		 * an exception occurred error-003 => call to INTERSWITCH returns an error
		 * object error-statusCode => call to INTERSWITCH returns a status code other
		 * than 200 or 408
		 */

		// to get sender's accountName
		String senderName = "";
		senderName = new AccountService().getAccountName(externalTransfer.getFromAccountNumber());

		// convert transaction amount to Naira
		int amountInKobo = Integer.parseInt(externalTransfer.getTransactionAmount().trim());
		String amountInNaira = CommonMethods.koboToNaira(amountInKobo);

		// total amount
		String charges = String.valueOf(Double.sum(Double.parseDouble(externalTransfer.getChargeAmount()),
				Double.parseDouble(externalTransfer.getTaxAmount())));
		String totalAmount = String.valueOf(Double.sum(Double.parseDouble(amountInNaira), Double.parseDouble(charges)));

		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(externalTransfer.getFromAccountNumber(), totalAmount)) {

			// return insufficient funds when transaction amount is less than available
			// balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_CODE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// call procedure to deduct from the senders account
		if (!database.callProcedure(senderName, externalTransfer, false, "", "")) {

			// return error if deduction was not possible
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// deduction was successful proceed

		// call Interswitch to carryout transfer
		Quickteller quickteller = new Quickteller();
		String fundTransferResponseStr = quickteller.fundTransfer(externalTransfer.getBeneficiaryAccountNumber(),
				externalTransfer.getBeneficiaryName(), String.valueOf(amountInKobo),
				externalTransfer.getBeneficiaryBankID(), senderName);

		// Deserialize response string
		FundTransferResponse fundTransferResponse = (FundTransferResponse) CommonMethods
				.JSONStringToObject(fundTransferResponseStr, FundTransferResponse.class);

		// might return error object
		if (fundTransferResponse.getResponseCode().isEmpty()) {

			// call procedure to do a reversal (credit)
			database.callProcedure(senderName, externalTransfer, true,
					ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, "");

			// and return if it is an error object
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(fundTransferResponseStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// used when saving transaction to the db
		String transferCode = fundTransferResponse.getTransferCode() + "|" + new Date() + "|" + new Date().getTime();

		// call query transaction to confirm transaction
		System.out.println("TransferCode: " + fundTransferResponse.getTransferCode());
		String queryTransRespStr = quickteller.queryTransaction(fundTransferResponse.getTransferCode());
		QueryTransactionResponse queryTransactionResponse = (QueryTransactionResponse) CommonMethods
				.JSONStringToObject(queryTransRespStr, QueryTransactionResponse.class);

		// might return error object
		if (queryTransactionResponse.getTransactionResponseCode().isEmpty()) {

			// call procedure to do a reversal (credit)
			database.callProcedure(senderName, externalTransfer, true,
					ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);

			// and return if it is an error object
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage("**Error: \n" + queryTransRespStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// successful, proceed
		// get transactionResponseCode and check
		String transactionResponseCode = queryTransactionResponse.getTransactionResponseCode();
		if (!(transactionResponseCode.equals("90000") || transactionResponseCode.equals("90010")
				|| transactionResponseCode.equals("90011") || transactionResponseCode.equals("90016")
				|| transactionResponseCode.equals("90009") || transactionResponseCode.equals("900A0"))) {

			// call procedure to do a reversal (credit)
			database.callProcedure(senderName, externalTransfer, true, transactionResponseCode, transferCode);

			// and return
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(queryTransRespStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// log request and responses
		CommonMethods.logContent("** ExternalFundsTransfer Request **" + body + "\r\n\r\n"
				+ "** ExternalFundsTransfer Request(Interswitch) **" + "fundTransferReqStr" + "\r\n\r\n"
				+ "** ExternalFundsTransfer Response(Interswitch) **" + fundTransferResponseStr + "\r\n\r\n"
				+ "** QueryTransaction Response **" + queryTransRespStr);

		System.out.println("\n*** End ExternalTransfers(finish) ***");

		// success
		ResponseModel responseModel = new ResponseModel();
		responseModel.setStatusCode(ResponseConstants.SUCCEESS_CODE_2);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		return CommonMethods.ObjectToJsonString(responseModel);

	}

	public static void main(String[] args) {

	}

}
