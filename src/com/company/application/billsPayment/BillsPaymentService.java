package com.company.application.billsPayment;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.company.application.billsPayment.data.BillPaymentRequest;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction.QueryTransactionResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice.BillPaymentAdviceResponse;

public class BillsPaymentService {

	/*
	 * This Method works with the assumption that transaction amount received is in kobo
	 */
	
	public String BillsPayment(String body) throws Exception {
		System.out.println("\n**** Start BillsPayment ****");

		BillPaymentRequest billPayment = (BillPaymentRequest) CommonMethods.JSONOrXMLToObject(body,
				BillPaymentRequest.class);

		BillsPaymentDBOperation database = new BillsPaymentDBOperation();

		/*
		 * error-001 => call to INTERSWITCH failed i.e did not go through error-002 =>
		 * an exception occurred error-003 => call to INTERSWITCH returns an error
		 * object error-statusCode => call to INTERSWITCH returns a status code other
		 * than 200 or 408
		 */

		// convert transaction amount to Naira
		String tran_Amount = CommonMethods.koboToNaira(Integer.parseInt(billPayment.getTransactionAmount().trim()));

		// total amount
		String charges = String.valueOf(Double.sum(Double.parseDouble(billPayment.getChargeAmount()),
				Double.parseDouble(billPayment.getTaxAmount())));
		String totalAmount = String.valueOf(Double.sum(Double.parseDouble(tran_Amount), Double.parseDouble(charges)));

		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(billPayment.getFromAccountNumber(), totalAmount)) {

			// return insufficient funds when transaction amount is less than available
			// balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_CODE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// call procedure to deduct from the senders account
		if (!database.callProcedure(billPayment, false, "", "")) {

			// return error if deduction was not possible
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// deduction was successful proceed

		// call Interswitch to carryout transfer
		Quickteller quickteller = new Quickteller();
		
		AtomicReference<String> requestReference = new AtomicReference<String>();
		
		String BPAdviceRespStr = quickteller.sendBillPaymentAdvice(billPayment.getPaymentCode(),
				billPayment.getCustomerId(), "", "", billPayment.getTransactionAmount(), requestReference);

		// Deserialize response string
		BillPaymentAdviceResponse BPAdviceResp = (BillPaymentAdviceResponse) CommonMethods
				.JSONOrXMLToObject(BPAdviceRespStr, BillPaymentAdviceResponse.class);

		// might return error object
		if (BPAdviceResp.getResponseCode().isEmpty()) {
			// call procedure to do a reversal (credit)
			database.callProcedure(billPayment, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, "");

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
			database.callProcedure(billPayment, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);

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
			database.callProcedure(billPayment, true, transactionResponseCode, transferCode);

			// and return
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(queryTransRespStr);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// log request and responses
		CommonMethods.logContent(
				"*** BillsPayment request ***" + body + "\r\n\r\n" + "*** BillsPayment Request(Interswitch) ***"
						+ "BPAdviceReqStr" + "\r\n\r\n" + "*** BillsPayment Response(Interswitch) ***" + BPAdviceRespStr
						+ "\r\n\r\n" + "*** QueryTransaction Response ***" + queryTransRespStr);

		System.out.println("\n**** End BillsPayment (finish) ****");

		// success
		ResponseModel responseModel = new ResponseModel();
		responseModel.setStatusCode(ResponseConstants.SUCCEESS_CODE);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		return CommonMethods.ObjectToJsonString(responseModel);
	}

}
