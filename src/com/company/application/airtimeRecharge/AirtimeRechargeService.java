package com.company.application.airtimeRecharge;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import com.company.application.airtimeRecharge.data.AirtimeRechargeRequest;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction.QueryTransactionResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice.BillPaymentAdviceRequest;
import com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice.BillPaymentAdviceResponse;


public class AirtimeRechargeService {

	/*
	 * This Method works with the assumption that transaction amount received is in kobo
	 */
	
	public String AirtimeRecharge(String body) throws Exception {
		System.out.println("\n**** Start AirtimeRecharge ****");
		
		AirtimeRechargeDBOperation database = new AirtimeRechargeDBOperation();
		
		
		AirtimeRechargeRequest airtimeRchgeXfer = (AirtimeRechargeRequest) CommonMethods.JSONOrXMLToObject(body,
				AirtimeRechargeRequest.class);
		
		/*
		 * error-001 			=> call to INTERSWITCH failed i.e did not go through
		 * error-002 			=> an exception occurred
		 * error-003 			=> call to INTERSWITCH returns an error object
		 * error-statusCode 	=> call to INTERSWITCH returns a status code other than 200 or 408
		 */
		
		// convert transaction amount to Naira
		String tran_Amount = CommonMethods.koboToNaira(Integer.parseInt(airtimeRchgeXfer.getTransactionAmount().trim()));

		// total amount
		String charges = String.valueOf(Double.sum(Double.parseDouble(airtimeRchgeXfer.getChargeAmount()),
				Double.parseDouble(airtimeRchgeXfer.getTaxAmount())));
		String totalAmount = String.valueOf(Double.sum(Double.parseDouble(tran_Amount), Double.parseDouble(charges)));

		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(airtimeRchgeXfer.getFromAccountNumber(), totalAmount)) {

			// return insufficient funds when transaction amount is less than available
			// balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_CODE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}
		
		// call procedure to deduct from the senders account
		if (!database.callProcedure(airtimeRchgeXfer, false, "", "")) {

			// return error if deduction was not possible
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}
			
		// deduction was successful proceed
		
		
		
		// call Interswitch
		Quickteller quickteller = new Quickteller();
		
//		//this is needed inorder to get request reference
//		BillPaymentAdviceRequest billPaymentAdviceRequest = quickteller.createBillPaymentAdviceRequest(airtimeRchgeXfer.getPaymentCode(),
//				airtimeRchgeXfer.getCustomerId(), airtimeRchgeXfer.getMobileNumber(), "", airtimeRchgeXfer.getTransactionAmount());
//		
		
		// call Interswitch to carryout transfer
		
		AtomicReference<String> requestReference = new AtomicReference<String>();
		
		String BPAdviceRespStr = quickteller.sendBillPaymentAdvice(airtimeRchgeXfer.getPaymentCode(),
				airtimeRchgeXfer.getCustomerId(), airtimeRchgeXfer.getMobileNumber(), "", airtimeRchgeXfer.getTransactionAmount(), requestReference);

		// Deserialize response string
		BillPaymentAdviceResponse BPAdviceResp = (BillPaymentAdviceResponse) CommonMethods
				.JSONOrXMLToObject(BPAdviceRespStr, BillPaymentAdviceResponse.class);
				
		// might return error object
		if (BPAdviceResp == null || BPAdviceResp.getResponseCode().isEmpty()) {
			// call procedure to do a reversal (credit)
			database.callProcedure(airtimeRchgeXfer, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, "");

			// and return if it is an error object
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(BPAdviceRespStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// used when saving to the db
		String transferCode = BPAdviceResp.getTransactionRef() + "|" + new Date() + "|" + new Date().getTime();

		// call query transaction to confirm transaction
		System.out.println("ReferenceCode: " + requestReference.get());
		String queryTransRespStr = quickteller.queryTransaction(requestReference.get());
		QueryTransactionResponse queryTransactionResponse = (QueryTransactionResponse) CommonMethods
				.JSONStringToObject(queryTransRespStr, QueryTransactionResponse.class);

		// might return error object
		if (queryTransactionResponse.getTransactionResponseCode().isEmpty()) {

			// call procedure to do a reversal (credit)
			database.callProcedure(airtimeRchgeXfer, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);

			// and return if it is an error object
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage("**Error: \n" + queryTransRespStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// successful, proceed
		// get transactionResponseCode and check
		String transactionResponseCode = queryTransactionResponse.getTransactionResponseCode();
		if (!(QuicktellerConstants.SUCCESS_CODES.contains(transactionResponseCode))) {
			
			// call procedure to do a reversal (credit)
			database.callProcedure(airtimeRchgeXfer, true, transactionResponseCode, transferCode);

			// and return
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(queryTransRespStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// log request and responses
		CommonMethods.logContent("*** Airtime Recharge request ***" + body + "\r\n\r\n" 
				+ "*** Airtime BillsPayment Request ***" + "BPAdviceReqStr" + "\r\n\r\n" 
				+ "*** Airtime BillsPayment Response ***" + BPAdviceRespStr + "\r\n\r\n"
				+ "*** QueryTransaction Response ***" + queryTransRespStr);

		System.out.println("\n**** End AirtimeRecharge(finish) ****");
		
		// success
		ResponseModel responseModel = new ResponseModel();
		responseModel.setStatusCode(ResponseConstants.SUCCEESS_CODE_2);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		return CommonMethods.ObjectToJsonString(responseModel);	
		

	}
	
	
}
