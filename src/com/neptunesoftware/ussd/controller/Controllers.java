package com.neptunesoftware.ussd.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.company.application.airtimeRecharge.AirtimeRechargeService;
import com.company.application.airtimeRecharge.data.AirtimeRechargeRequest;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.company.application.fundTransfer.FundTransferService;
import com.company.application.fundTransfer.data.ExternalFTRequest;
import com.company.application.fundTransfer.data.InternalFTRequest;
import com.google.gson.Gson;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.ussd.FirstTimeUser.FirstTimeData;
import com.neptunesoftware.ussd.FirstTimeUser.FirstTimeNoData;
import com.neptunesoftware.ussd.FirstTimeUser.FirstTimeService;
import com.neptunesoftware.ussd.balanceEnquiry.BalanceEnquiry;
import com.neptunesoftware.ussd.balanceEnquiry.BalanceEnquiryData;
import com.neptunesoftware.ussd.miniStatement.MiniStatementRequest;
import com.neptunesoftware.ussd.miniStatement.MiniStatementResponse;
import com.neptunesoftware.ussd.miniStatement.MiniStatementService;
import com.neptunesoftware.ussd.mobileValidation.MobileValidation;
import com.neptunesoftware.ussd.nameEnquiry.NameEnquiryResponse;
import com.neptunesoftware.ussd.nameEnquiry.NameEnquiryService;
import com.neptunesoftware.ussd.response.MobileResponse;
import com.neptunesoftware.ussd.response.Responses;
import com.neptunesoftware.ussd.ussdUsers.UssdUsers;
import com.neptunesoftware.ussd.validpin.GetPin;
import com.neptunesoftware.ussd.validpin.PinData;
import com.neptunesoftware.ussd.validpin.PinResponse;

@Path("/")
public class Controllers {

	FirstTimeService firstData = new FirstTimeService();
	BalanceEnquiry balanceEnquiry = new BalanceEnquiry();
	MobileValidation validMobile = new MobileValidation();
	UssdUsers ussdUsers = new UssdUsers();

	@POST
	@Path("/savePin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String savePin(String body) {

		FirstTimeData newPin = new Gson().fromJson(body, FirstTimeData.class);

		FirstTimeData newData = new FirstTimeData();
		//newData.setAcct_No(newPin.getAccountNumber());
		newData.setMobileNumber(newPin.getMobileNumber());
		newData.setPin1(newPin.getPin1());
		newData.setPin2(newPin.getPin2());

		Responses responses = firstData.savePin(newData);

		String json = new Gson().toJson(responses);
		return json;
	}

	@POST
	@Path("/newCustomer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String newCustomer(String body) {

		FirstTimeNoData data = new Gson().fromJson(body, FirstTimeNoData.class);

		FirstTimeNoData customer = new FirstTimeNoData();
		customer.setFirstName(data.getFirstName());
		customer.setLastName(data.getLastName());
		customer.setGender(data.getGender());
		customer.setContact(data.getContact());

		System.out.println(data.getContact());

		Responses response = firstData.newCustomer(data);
		String json = new Gson().toJson(response);

		return json;
	}

	@GET
	@Path("/mobileValidation/{mobileNumber}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String mobileValidation(@PathParam("mobileNumber") String number) {

		// FirstTimeData mobile = new Gson().fromJson(number, FirstTimeData.class);

		FirstTimeData mobile = new FirstTimeData();
		mobile.setMobileNumber(number);

		MobileResponse response = validMobile.firstValidation(mobile);
		String json = new Gson().toJson(response);

		return json;
	}

	/*
	 * @POST
	 * 
	 * @Path("/balanceEnquiry")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public String balanceEnquiry(String
	 * body) {
	 * 
	 * //BalanceEnquiryData data = new Gson().fromJson(body,
	 * BalanceEnquiryData.class);
	 * 
	 * com.company.application.balanceEnquiry.BalanceEnquiryResponse
	 * balanceEnquiryResponse = new
	 * com.company.application.balanceEnquiry.BalanceEnquiryResponse();
	 * 
	 * 
	 * 
	 * BalanceEnquiryData balance = new BalanceEnquiryData();
	 * balance.setMobileNumber(data.getMobileNumber());
	 * balance.setPin(data.getPin());
	 * 
	 * 
	 * PinResponse response = new PinResponse(); response =
	 * validMobile.accountNumber(body);
	 * 
	 * if (response.getStatusCode() == 200) {
	 * 
	 * String accountNo = response.getAccountNumber();
	 * 
	 * BalanceEnquiryService balanceEnquiryService = new BalanceEnquiryService();
	 * balanceEnquiryResponse = balanceEnquiryService.balanceEnquiry(accountNo);
	 * 
	 * if(balanceEnquiryResponse.getStatusCode() == "00") {
	 * 
	 * balanceEnquiryResponse.setStatusCode("200");
	 * 
	 * }else { balanceEnquiryResponse.setStatusCode("400"); } String json = new
	 * Gson().toJson(balanceEnquiryResponse);
	 * 
	 * return json;
	 * 
	 * 
	 * }else
	 * 
	 * { Responses responses = new Responses();
	 * responses.setErrorMessage("Transaction Failed");
	 * responses.setStatusCode(400); String json = new Gson().toJson(responses);
	 * 
	 * return json; } }
	 */

	@GET
	@Path("/balanceEnquiry/{acctNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public String balanceEnquiry(@PathParam("acctNumber") String number) {

		com.company.application.balanceEnquiry.BalanceEnquiryResponse balanceEnquiryResponse = 
				new com.company.application.balanceEnquiry.BalanceEnquiryResponse();

		BalanceEnquiryService balanceEnquiryService = new BalanceEnquiryService();
		balanceEnquiryResponse = balanceEnquiryService.balanceEnquiry(number);

		if (balanceEnquiryResponse.getStatusCode() == "00") {

			balanceEnquiryResponse.setStatusCode("200");
			String json = new Gson().toJson(balanceEnquiryResponse);
			return json;

		} else {

			Responses responses = new Responses();
			responses.setErrorMessage("Transaction Failed");
			responses.setStatusCode(400);
			String json = new Gson().toJson(responses);

			return json;
		}

	}

	@POST
	@Path("/ussdUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String ussdUser(String body) {

		FirstTimeData data = new Gson().fromJson(body, FirstTimeData.class);

		UssdUsers ussdUsers = new UssdUsers();
		FirstTimeData response = ussdUsers.validUssdUser(data);
		String json = new Gson().toJson(response);

		return json;
	}

	@POST
	@Path("/getUserPin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserPin(String body) {

		PinResponse data = new Gson().fromJson(body, PinResponse.class);

		PinResponse userData = new PinResponse();
		//userData.setAccountNumber(data.getAccountNumber());
		userData.setContact(data.getContact());

		GetPin getpin = new GetPin();
		PinData pindata = getpin.getPin(data);
		String json = new Gson().toJson(pindata);

		return json;
	}

	@GET
	@Path("/bankCode")
	@Produces(MediaType.APPLICATION_JSON)
	public String bankCodes() {
		Quickteller quickteller = new Quickteller();
		System.out.println("BankCode Retrived");
		return quickteller.getBankCodes();

	}

	@POST
	@Path("/externalFundTransfer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String externalFundTransfer(String body) {
		int maxAmount = 100000;

		ExternalFTRequest externalFTRequest = new Gson().fromJson(body, ExternalFTRequest.class);
		int amount = Integer.valueOf(externalFTRequest.getTransactionAmount());
	
			if (amount <= maxAmount) {

				try {

					FundTransferService fundTransferService = new FundTransferService();

					return fundTransferService.ExternalTransfers(body);
					
				} catch (Exception e) {

					e.printStackTrace();
					Responses responses = new Responses();
					responses.setErrorMessage("ExtenalFundTransaction class failed");
					responses.setStatusCode(400);
					String json = new Gson().toJson(responses);
					return json;

				}
			} else {
				
				Responses responses = new Responses();
				responses.setErrorMessage("Exceeded maximum daily transaction amount");
				responses.setStatusCode(400);
				String json = new Gson().toJson(responses);
				return json;
			}

	}


	@POST
	@Path("/internalFundTransfer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String internalFundTransfer(String body) {

		int maxAmount = 100000;
		
		InternalFTRequest internalFTRequest = new Gson().fromJson(body,InternalFTRequest.class);
		System.out.println(internalFTRequest.getTransactionAmount());
		int amount = Integer.valueOf(internalFTRequest.getTransactionAmount());
		

			if (amount <= maxAmount) {
					
				FundTransferService fundTransferService = new FundTransferService();
				ResponseModel responseValue  = fundTransferService.InternalFundTransfer(body);
				if(responseValue.getStatusCode() == "00")
				{
					responseValue.setStatusCode("200");
					String json = new Gson().toJson(responseValue);
					return json;

				}else {
					
					Responses responses = new Responses();
					responses.setErrorMessage("Internal Fund Transaction Failed");
					responses.setStatusCode(400);
					String json = new Gson().toJson(responses);
					return json;

				}
					} else {
				Responses responses = new Responses();
				responses.setErrorMessage("Exceeded maximum daily transaction amount");
				responses.setStatusCode(400);
				String json = new Gson().toJson(responses);
				return json;
			}

	} 

	
	@GET
	@Path("/Statement/{acctNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String Statement(@PathParam("acctNumber") String number) {

		MiniStatementRequest miniRqst = new MiniStatementRequest();
		miniRqst.setAccountNumber(number);

		MiniStatementService miniServ = new MiniStatementService();
		MiniStatementResponse miniResp = new MiniStatementResponse();
		miniResp = miniServ.miniStatement(miniRqst);

		String json = new Gson().toJson(miniResp);
		return json;

	}
	
	
	@GET
	@Path("/nameEnquiry/{accountNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String internalNameEnquiry(@PathParam("accountNumber") String number) {
		
		NameEnquiryService nameEnquiryService = new NameEnquiryService();
		NameEnquiryResponse nameEnquiryResponse = new NameEnquiryResponse();
		
		nameEnquiryResponse = nameEnquiryService.internalNameEnquiry(number);
		String json = new Gson().toJson(nameEnquiryResponse);
		
		return json;
	}
	
	
	@GET
	@Path("/nameEnquiry/{accountNumber}/{bankcode}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String externalNameEnquiry(@PathParam("accountNumber") String number, @PathParam("bankcode") String code) {
		
		NameEnquiryService nameEnquiryService = new NameEnquiryService();
		
		return nameEnquiryService.externalNameEnquiry(number, code);
	}
	
	
	@POST
	@Path("/mobileTopup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String mobileTopup(String body) {
		int maxTopup = 5000;
		AirtimeRechargeRequest airtimeRechargeRequest =new Gson().fromJson(body,AirtimeRechargeRequest.class);
		int amount = Integer.valueOf(airtimeRechargeRequest.getTransactionAmount());
		if (amount <= maxTopup) {
		try {
			AirtimeRechargeService airtimeRechargeService = new AirtimeRechargeService();
			
				return airtimeRechargeService.AirtimeRecharge(body);
				
		} catch (Exception e) {
			
			e.printStackTrace();
			Responses responses = new Responses();
			responses.setErrorMessage("MobileTopup class failed");
			responses.setStatusCode(400);
			String json = new Gson().toJson(responses);
			return json;
		}	
		}else {
		Responses responses = new Responses();
		responses.setErrorMessage("Exceeded maximum daily transaction amount");
		responses.setStatusCode(400);
		String json = new Gson().toJson(responses);
		return json;}
		
	}

	
	public static void main(String[] sick) {
//		
//		ExternalFTRequest externalFTRequest = new ExternalFTRequest();
//		externalFTRequest.setInitiatingApp("");
//		externalFTRequest.setFromAccountNumber("3002000036");
//		externalFTRequest.setBeneficiaryAccountNumber("9999999999");
//		externalFTRequest.setBeneficiaryName("okon timi");
//		externalFTRequest.setBeneficiaryBankID("011");
//		externalFTRequest.setTransactionAmount("500");
//		externalFTRequest.setTaxAmount("2");
//		externalFTRequest.setChargeAmount("3");
//		externalFTRequest.setTransactionFee("0");
//		
//		String reqStr = CommonMethods.ObjectToJsonString(externalFTRequest);
		
		String jsonString = "{\r\n" + 
				"	\"initiatingApp\":\"\",\r\n" + 
				"	\"fromAccountNumber\":\"3002000010\",\r\n" + 
				"	\"beneficiaryBankID\":\"058\",\r\n" + 
				"	\"beneficiaryAccountNumber\":\"0014261063\",\r\n" + 
				"	\"beneficiaryName\":\"Nnamdi Olakunle\",\r\n" + 
				"	\"transactionAmount\":\"2000\",\r\n" + 
				"	\"transactionDescription\":\"\",\r\n" + 
				"	\"transactionFee\":\"0\",\r\n" + 
				"	\"chargeAmount\":\"5\",\r\n" + 
				"	\"taxAmount\":\"2.5\"\r\n" + 
				"	\r\n" + 
				"}";
		
		Controllers controllers = new Controllers();
		System.out.println(controllers.externalFundTransfer(jsonString));
	}
}
