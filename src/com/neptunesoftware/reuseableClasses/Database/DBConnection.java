package com.neptunesoftware.reuseableClasses.Database;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.company.application.airtimeRecharge.data.AirtimeRechargeRequest;
import com.company.application.billsPayment.data.BillPaymentRequest;
import com.company.application.fundTransfer.data.ExternalFTRequest;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.CypherCrypt;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;

public class DBConnection {

	private static String driver = "";
	private static String username = "";
	private static String password = "";
	private static String connectionURL = "";
	private static String databaseType = "";
	private static String ipAddress = "";
	private static String portNumber = "";
	private static String serviceName = "";

	// //private String connectionURL = "jdbc:oracle:thin:@localhost:1521:orcl";

	public DBConnection() {
		this("Oracle");
	}
	
	public DBConnection(String driver, String connectionURL, String username, String password, String databaseType) {
		DBConnection.driver = driver;
		DBConnection.username = username;
		DBConnection.password = password;
		DBConnection.connectionURL = connectionURL;
		DBConnection.databaseType = databaseType;
	}

	public DBConnection(String databaseType) {
		
		Database database = readConfig();
			
		if (!database.getDatabaseProps().isEmpty())
			for (DatabaseProperty dbProperty : database.getDatabaseProps()) {
				if (dbProperty.getType().trim().toUpperCase().equals(databaseType.toUpperCase())) {

					DBConnection.driver = getDatabaseDriver(databaseType.toUpperCase());
					DBConnection.username = dbProperty.getUsername().trim();
					DBConnection.password = dbProperty.getPassword().trim();
					DBConnection.ipAddress = dbProperty.getIpAddress().trim();
					DBConnection.portNumber = dbProperty.getPortNumber().trim();
					DBConnection.serviceName = dbProperty.getServiceName().trim();
					DBConnection.connectionURL = getDatabaseConnectionUrl(databaseType, ipAddress, portNumber,
							serviceName);
					DBConnection.databaseType = databaseType;
				}
			}
		else {
			DBConnection.driver = "";
			DBConnection.username = "";
			DBConnection.password = "";
			DBConnection.ipAddress = "";
			DBConnection.portNumber = "";
			DBConnection.serviceName = "";
			DBConnection.connectionURL = "";
			DBConnection.databaseType = databaseType;
		}
	}
	
	
 	protected static Connection databaseConnection() {
		Connection connection = null;

		try {
			Class.forName(driver);

			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			props.setProperty("charset", "iso_1");
			
			connection = DriverManager.getConnection(connectionURL, props);
			//connection = DriverManager.getConnection(connectionURL, username, password);
			
			System.out.println("connection to " + databaseType.toUpperCase() + " database established");

		} catch (ClassNotFoundException e) {
			System.out.println("Make sure the appropriate jar file for your database connection has been added");
			System.out.println("\nconnection to " + databaseType.toUpperCase() + " database failed");
			
		} catch (SQLException e) {
			System.out.println("Please Verify your connection parameters and that your " + databaseType.toUpperCase() + " database is started");
			System.out.println("driver: " + driver + "\nusername: " + username + "\npassword: " + password);
			System.out.println("ipAddress: " + ipAddress + "\nportNumber: " + portNumber+ "\nserviceName: " + serviceName);
			System.out.println("\nconnection to " + databaseType.toUpperCase() + " database failed");
		
		} catch (Exception e) {
			System.out.println("This error pass me!");
			System.out.println("*** Stack Trace *** \n" + e);
			System.out.println("\nconnection to " + databaseType.toUpperCase() + " database failed");
		}
		
		return connection;
	}

	
	public static void main(String [] args) {
		
		System.out.println("Start of Main Method");
	
		//DBConnection dbConnection = new DBConnection("com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:10.152.2.36:5000/banking", "sa", "csa123", "Sybase");
		
		//DBConnection dbConnection = new DBConnection("ORAcle");
		
		//DBConnection.databaseConnection();
		
		System.out.println("End of Main Method");
	}
	
	
	
	
	
	public String selectProcessingDate() throws Exception {

		Connection dbConnection = null;
		dbConnection = databaseConnection();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
				
		try {
			
			String sql = "SELECT to_char(to_date(DISPLAY_VALUE, 'DD/MM/YYYY'), 'YYYYMMDD') FROM CTRL_PARAMETER WHERE PARAM_CD = 'S65'";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			
			rs = preparedStatement.executeQuery();
			
			String processingDate = "01/01/1900";
			while (rs.next()) {
				processingDate = rs.getString(1);			
			}

			System.out.println("Rubikon's Processing date:" + processingDate);
			
			return processingDate;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	public boolean tableExist(String tableName) throws SQLException {
	    boolean tExists = false;
	    
	    tableName = tableName.toUpperCase();
	    
	    Connection dbConnection = null;
		dbConnection = databaseConnection();
		
	    try (ResultSet rs = dbConnection.getMetaData().getTables(null, null, tableName, null)) {
	        while (rs.next()) { 
	            String tName = rs.getString("TABLE_NAME");
	            if (tName != null && tName.equals(tableName)) {
	                tExists = true;
	                break;
	            }
	        }
	    }
	    return tExists;
	}
	
	public boolean procedureExist(String procedureName) throws SQLException {
	    boolean tExists = false;
	    
	    procedureName = procedureName.toUpperCase();
	    
	    Connection dbConnection = null;
		dbConnection = databaseConnection();
		
	    try (ResultSet rs = dbConnection.getMetaData().getProcedures(null, null, procedureName)) {
	        while (rs.next()) { 
	            String tName = rs.getString("PROCEDURE_NAME");
	            if (tName != null && tName.equals(procedureName)) {
	                tExists = true;
	                break;
	            }
	        }
	    }
	    return tExists;
	}

	public boolean createDatabaseObject(String query) throws SQLException {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement pst = null;
		
		try {

			pst = dbConnection.prepareStatement(query);
			int result = pst.executeUpdate();

			if(!(result == 0)) {
				System.out.println("failed to create object");
				return false;
			}
			
			System.out.println("object created!");			
			return true;
		}

		catch (Exception e) {
			System.out.println("failed to create object(check query)");
			return false;
		} finally {

			if (dbConnection != null) {
				dbConnection.close();
			}
			if (pst != null) {
				pst.close();
			}
		}
		
	}
	
	
	
	// start very specific methods.
	// not really reusable except you are implementing quickteller
	
	public boolean callProcedure(AirtimeRechargeRequest airtimeRchgeXfer, boolean isReversal, String responseCode, String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		String chargeCodeBillsPayment = rubikonCredential.getChargeCode(); //rubikonCredential.getChargeCodeBillsPayment();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String billsPaymentCredit = rubikonCredential.getMobileRechargeCredit();
		String billsPaymentDebit = rubikonCredential.getMobileRechargeDebit();
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "Airtime recharge for mobile no " + airtimeRchgeXfer.getCustomerId();
		
		String transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(airtimeRchgeXfer.getTransactionAmount().trim()));
		String chargeAmount = airtimeRchgeXfer.getChargeAmount().trim();
		String taxAmount = airtimeRchgeXfer.getTaxAmount().trim();
		String serviceCode = billsPaymentDebit;
		String transactionType =rubikonCredential.getDebitTransCode();
		String reveralFlag = "N";
		
		if(isReversal) {
			narration = "(Reversal) " + narration;
			
			String reversalCharges = String.valueOf(Double.sum(Double.parseDouble(chargeAmount), Double.parseDouble(taxAmount)));
			transactionAmount =		String.valueOf(Double.sum(Double.parseDouble(CommonMethods.koboToNaira(Integer.parseInt(transactionAmount))), Double.parseDouble(reversalCharges)));
			chargeAmount = "0";
			taxAmount = "0";
			serviceCode = billsPaymentCredit;
			transactionType = "CR";
			//reveralFlag = "Y";
		}
		
		
		DBRequest dbRequest = new DBRequest(airtimeRchgeXfer.getFromAccountNumber(), transactionAmount, "0",
				airtimeRchgeXfer.getCustomerId(), narration, chargeAmount, taxAmount, airtimeRchgeXfer.getInitiatingApp());
		
		
		transactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = XAPI_POSTING_SERVICE_DR(dbRequest, "", serviceCode, transactionRef, reveralFlag, chargeCodeBillsPayment);
		
		responseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			addRecord(dbRequest, responseCode, transactionRef, "MobileRecharge", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
	
	public boolean callProcedure(BillPaymentRequest billPayment, boolean isReversal, String responseCode, String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		String chargeCodeBillsPayment = rubikonCredential.getChargeCodeBillsPayment();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String billsPaymentCredit = rubikonCredential.getBillsPaymentCredit();
		String billsPaymentDebit = rubikonCredential.getBillsPaymentDebit();
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "Bills payment #" + billPayment.getCustomerId(); 
		narration = narration + "\nSub Desc: " + billPayment.getTransactionDescription();
		
		String transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(billPayment.getTransactionAmount().trim()));
		String chargeAmount = billPayment.getChargeAmount().trim();
		String taxAmount = billPayment.getTaxAmount().trim();
		String serviceCode = billsPaymentDebit;
		String transactionType = "DR";
		String reveralFlag = "N";
		
		if(isReversal) {
			narration = "(Reversal) " + narration;
			
			String reversalCharges = String.valueOf(Double.sum(Double.parseDouble(chargeAmount), Double.parseDouble(taxAmount)));
			transactionAmount =		String.valueOf(Double.sum(Double.parseDouble(CommonMethods.koboToNaira(Integer.parseInt(transactionAmount))), Double.parseDouble(reversalCharges)));
			chargeAmount = "0";
			taxAmount = "0";
			serviceCode = billsPaymentCredit;
			transactionType = "CR";
			//reveralFlag = "Y";
		}
		
		
		DBRequest dbRequest = new DBRequest(billPayment.getFromAccountNumber(), transactionAmount, "0",
				billPayment.getCustomerId(), narration, chargeAmount, taxAmount, billPayment.getInitiatingApp());
		
		
		transactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = XAPI_POSTING_SERVICE_DR(dbRequest, "", serviceCode, transactionRef, reveralFlag, chargeCodeBillsPayment);
		
		responseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			addRecord(dbRequest, responseCode, transactionRef, "BillsPayment", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
		
	public boolean callProcedure(String senderName, ExternalFTRequest externalTransfer, boolean isReversal, String responseCode, String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();	
		String chargeCode = rubikonCredential.getChargeCode();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String fundXferCredit = rubikonCredential.getInternalAcctTransfer();//
		String fundXferDebit = rubikonCredential.getExternalAcctTransfer();//
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "External Transfer from " + senderName + ", #acctno " + externalTransfer.getFromAccountNumber() + 
				" to #acctno " + externalTransfer.getBeneficiaryAccountNumber(); 
		narration = narration + "\nSub Desc: " + externalTransfer.getTransactionDescription();	
		
		String transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.getTransactionAmount().trim()));
		String chargeAmount = externalTransfer.getChargeAmount().trim();
		String taxAmount = externalTransfer.getTaxAmount().trim();
		String serviceCode = fundXferDebit;
		String transactionType = "DR";
		String reversalFlag = "N";
		
		/*
		 * if(isReversal == true) { narration = "(Reversal) " + narration;
		 * 
		 * String reversalCharges =
		 * String.valueOf(Double.sum(Double.parseDouble(chargeAmount),
		 * Double.parseDouble(taxAmount))); transactionAmount =
		 * String.valueOf(Double.sum(Double.parseDouble(CommonMethods.koboToNaira(
		 * Integer.parseInt(transactionAmount))), Double.parseDouble(reversalCharges)));
		 * chargeAmount = "0"; taxAmount = "0"; serviceCode = fundXferCredit;
		 * transactionType = "CR"; //reversalFlag = "Y"; }
		 */
		
		
		DBRequest dbRequest = new DBRequest(externalTransfer.getFromAccountNumber(), transactionAmount, "0",
				externalTransfer.getBeneficiaryAccountNumber(), narration, chargeAmount, taxAmount,
				externalTransfer.getInitiatingApp());
		
		
		transactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = XAPI_POSTING_SERVICE_DR(dbRequest, senderName, serviceCode, transactionRef, reversalFlag, chargeCode);
		
		responseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			addRecord(dbRequest, responseCode, transactionRef, "ExternalFundTransfer", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
	
	
	public boolean callProcedureReversal(String senderName, ExternalFTRequest externalTransfer, boolean isReversal, String responseCode, String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();	
		String chargeCode = rubikonCredential.getChargeCode();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String fundXferCredit = rubikonCredential.getInternalAcctTransfer();//
		String fundXferDebit = rubikonCredential.getExternalAcctTransfer();//
		String fundXferReversal = rubikonCredential.getExternalAcctTransReversal();//
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "External Transfer from " + senderName + ", #acctno " + externalTransfer.getFromAccountNumber() + 
				" to #acctno " + externalTransfer.getBeneficiaryAccountNumber(); 
		narration = narration + "\nSub Desc: " + externalTransfer.getTransactionDescription();	
		
		String transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.getTransactionAmount().trim()));
		String chargeAmount = externalTransfer.getChargeAmount().trim();
		String taxAmount = externalTransfer.getTaxAmount().trim();
		String serviceCode = fundXferReversal;
		String transactionType = "";
		String reversalFlag = "";
		
		if(isReversal == true) {
			narration = "(Reversal) " + narration;
			
			String reversalCharges = String.valueOf(Double.sum(Double.parseDouble(chargeAmount), Double.parseDouble(taxAmount)));
			transactionAmount =		String.valueOf(Double.sum(Double.parseDouble(CommonMethods.koboToNaira(Integer.parseInt(transactionAmount))), Double.parseDouble(reversalCharges)));
			chargeAmount = "0";
			taxAmount = "0";
			//serviceCode = fundXferCredit;
			transactionType = "CR";
			reversalFlag = "Y";
		}
		
		
		DBRequest dbRequest = new DBRequest(externalTransfer.getFromAccountNumber(), transactionAmount, "0",
				externalTransfer.getBeneficiaryAccountNumber(), narration, chargeAmount, taxAmount,
				externalTransfer.getInitiatingApp());
		
		
		transactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = XAPI_POSTING_SERVICE_CR(dbRequest, senderName, serviceCode, transactionRef, reversalFlag, chargeCode);
		
		responseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			addRecord(dbRequest, responseCode, transactionRef, "ExternalFundTransfer", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
	
	public String XAPI_POSTING_SERVICE_CR(DBRequest dbRequest, String senderName,
			String serviceCode, String transactionRef, String reversalFlag, String chargeCode) {
		
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();		
		String channelCode = rubikonCredential.getChannelCode();
		String taxCode = rubikonCredential.getTaxCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String appUsername = rubikonCredential.getApplicationUsername();
		String transType = rubikonCredential.getCreditTransCode();
		/*
		 * String transTypeCode; String a = rubikonCredential.getInternalAcctTransfer();
		 * String b = rubikonCredential.getExternalAcctTransfer(); String c = a||b;
		 */
		
		String contraCurrencyCode = !dbRequest.getContraAmount().equals("0") ? currencyCode : "";
		transactionRef = transactionRef.isEmpty() 
				? "INT-TXN-|" + new Date() + "|" + new Date().getTime()
				: transactionRef;
				
		DBResponse dbResponse = new DBResponse();
		
		try {
			dbResponse = ProcedureXAPI_POSTING_SERVICE(
					channelCode, // PV_CHANNEL_CD
					"", // PV_CHANNEL_PWD
					"123456789", // PV_DEVICE_ID
					serviceCode, // PV_SERVICE_CD
					transType, // PV_TRANS_TYPE_CD
					dbRequest.getSenderAcctNo(), // PV_ACCOUNT_NO,
					currencyCode, // PV_ACCOUNT_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getAmount()))), // PV_ACCOUNT_AMOUNT,
					currencyCode, // PV_TRANS_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getAmount()))), // PV_TRANS_AMOUNT,
					
					new Date(), // PV_VALUE_DATE,
					new Date(), // PV_TRANS_DATE,
					transactionRef, // PV_TRANS_REF,
					transactionRef, // PV_SUPPLEMENTARY_REF,
					dbRequest.getNarration(), // PV_NARRATIVE,
					dbRequest.getBeneficiaryAcctNo(), // PV_CONTRA_ACCOUNT_NO,
					contraCurrencyCode, // PV_CONTRA_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getContraAmount()))), // PV_CONTRA_AMOUNT,
					new BigDecimal(0), // PV_EXCHANGE_RATE,
					chargeCode, // PV_CHARGE_CD,
					
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getChargeAmount()))), // PV_CHARGE_AMOUNT,
					taxCode, // PV_TAX_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getTaxAmount()))), // PV_TAX_AMOUNT,
					reversalFlag, // PV_REVERSAL_FLG,
					"", // PV_ORIGIN_BANK_CD,
					"", // PV_SERVICE_PROVIDER,
					"", // PV_SERVICE_PROVIDER_SVC,
					"", // PV_USER_TYPE,
					appUsername, // PV_USER_ID,
					"", // PV_USER_PWD,
					
				   "12:45:30", // PV_TRANS_TIME
				   "N", // PV_EXTERNAL_COMMIT
				   "Y", // PV_PREVENT_DUPLICATES
				   "123456789", //PV_REQUEST_PWD
				   "DB", // PV_LOG_REPOSITORY
				   0, // PV_TEST_CALL_LEVEL
				   3, // PV_TRACE_LEVEL
					"", // PV_ERROR_CODE,
					0, // PV_ERROR_SEVERITY,
					"" // PV_ERROR_MESSAGE,
					);
		} catch (Exception e) {
			
			return "";
		}

		String responseCode = dbResponse.PV_ERROR_SEVERITY == null || dbResponse.PV_ERROR_SEVERITY.equals("0") ? "00"
				: dbResponse.PV_ERROR_SEVERITY;
		
		System.out.println("callProdecure_TEST_TRANS_SERVICE Response: " + responseCode);

		//return response
		return responseCode;
	}
	
	public DBResponse ProcedureXAPI_POSTING_SERVICE(String PV_CHANNEL_CD,
			String PV_CHANNEL_PWD, String PV_DEVICE_ID, String PV_SERVICE_CD,
			String PV_TRANS_TYPE_CD, String PV_ACCOUNT_NO,
			String PV_ACCOUNT_CURRENCY_CD, BigDecimal PV_ACCOUNT_AMOUNT,
			String PV_TRANS_CURRENCY_CD, BigDecimal PV_TRANS_AMOUNT,
			Date PV_VALUE_DATE, Date PV_TRANS_DATE, String PV_TRANS_REF,
			String PV_SUPPLEMENTARY_REF, String PV_NARRATIVE,
			String PV_CONTRA_ACCOUNT_NO, String PV_CONTRA_CURRENCY_CD,
			BigDecimal PV_CONTRA_AMOUNT, BigDecimal PV_EXCHANGE_RATE,
			String PV_CHARGE_CD, BigDecimal PV_CHARGE_AMOUNT,
			String PV_TAX_CD, BigDecimal PV_TAX_AMOUNT,
			String PV_REVERSAL_FLG, String PV_ORIGIN_BANK_CD,
			String PV_SERVICE_PROVIDER, String PV_SERVICE_PROVIDER_SVC,
			String PV_USER_TYPE, String PV_USER_ID, String PV_USER_PWD,
			String PV_TRANS_TIME, String PV_EXTERNAL_COMMIT, String PV_PREVENT_DUPLICATES, 
			String PV_REQUEST_PWD, String PV_LOG_REPOSITORY, int PV_TEST_CALL_LEVEL,
			int PV_TRACE_LEVEL, String PV_ERROR_CODE, int PV_ERROR_SEVERITY,
			String PV_ERROR_MESSAGE
			 
			 
			) throws Exception {
		
		

		String XAPI_TRANS_SERVICE = "{call XAPI_POSTING_SERVICE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

		Connection dbConnection = null;
		dbConnection = databaseConnection();

		CallableStatement callableStatement = null;
		DBResponse dbResponse = new DBResponse();
			
		try {
			callableStatement = dbConnection.prepareCall(XAPI_TRANS_SERVICE);
			
			callableStatement.setString(1, PV_CHANNEL_CD); // PV_CHANNEL_CD
			callableStatement.setString(2, PV_CHANNEL_PWD); // PV_CHANNEL_PWD
			callableStatement.setString(3, PV_DEVICE_ID); // PV_DEVICE_ID
			callableStatement.setString(4, PV_SERVICE_CD); // PV_SERVICE_CD
			callableStatement.setString(5, PV_TRANS_TYPE_CD); // PV_TRANS_TYPE_CD
			callableStatement.setString(6, PV_ACCOUNT_NO); // PV_ACCOUNT_NO
			callableStatement.setString(7, PV_ACCOUNT_CURRENCY_CD); // PV_ACCOUNT_CURRENCY_CD
			callableStatement.setBigDecimal(8, PV_ACCOUNT_AMOUNT); // PV_ACCOUNT_AMOUNT
			callableStatement.setString(9, PV_TRANS_CURRENCY_CD); // PV_TRANS_CURRENCY_CD
			callableStatement.setBigDecimal(10, PV_TRANS_AMOUNT); // PV_TRANS_AMOUNT

			callableStatement.setDate(11, getCurrentDate()); // PV_VALUE_DATE
			callableStatement.setDate(12, getCurrentDate());// (12,// getCurrentDate());// //PV_TRANS_DATE
			callableStatement.setString(13, PV_TRANS_REF); // PV_TRANS_REF
			callableStatement.setString(14, PV_SUPPLEMENTARY_REF); // PV_SUPPLEMENTARY_REF
			callableStatement.setString(15, PV_NARRATIVE); // PV_NARRATIVE
			callableStatement.setString(16, PV_CONTRA_ACCOUNT_NO);// (16, "");// //PV_CONTRA_ACCOUNT_NO
			callableStatement.setString(17, PV_CONTRA_CURRENCY_CD); // PV_CONTRA_CURRENCY_CD
			callableStatement.setBigDecimal(18, PV_CONTRA_AMOUNT); // PV_CONTRA_AMOUNT// BigDecimal
			callableStatement.setBigDecimal(19, PV_EXCHANGE_RATE); // PV_EXCHANGE_RATE// BigDecimal
			callableStatement.setString(20, PV_CHARGE_CD); // PV_CHARGE_CD

			callableStatement.setBigDecimal(21, PV_CHARGE_AMOUNT); // PV_CHARGE_AMOUNT // BigDecimal
			callableStatement.setString(22, PV_TAX_CD);	//PV_TAX_CD               IN STRING,    
			callableStatement.setBigDecimal(23, PV_TAX_AMOUNT);	//PV_TAX_AMOUNT           IN NUMBER,
			callableStatement.setString(24, PV_REVERSAL_FLG); // PV_REVERSAL_FLG
			callableStatement.setNull(25, Types.VARCHAR); // PV_ORIGIN_BANK_CD
			callableStatement.setNull(26, Types.VARCHAR); // PV_SERVICE_PROVIDER
			callableStatement.setNull(27, Types.VARCHAR); // PV_SERVICE_PROVIDER_SVC
			callableStatement.setString(28, PV_USER_TYPE); // PV_USER_TYPE
			callableStatement.setString(29, PV_USER_ID); // PV_USER_ID
			callableStatement.setString(30, PV_USER_PWD); // PV_USER_PWD
			
			callableStatement.setString(31, PV_TRANS_TIME); //PV_TRANS_TIME           IN STRING,
			callableStatement.setString(32, PV_EXTERNAL_COMMIT); //PV_EXTERNAL_COMMIT      IN STRING,
			callableStatement.setString(33, PV_PREVENT_DUPLICATES);	//PV_PREVENT_DUPLICATES   IN STRING,
			callableStatement.setString(34, PV_REQUEST_PWD);	//PV_REQUEST_PWD          IN STRING,    
			callableStatement.setString(35, PV_LOG_REPOSITORY);	//PV_REQUEST_PWD 
			
			callableStatement.registerOutParameter(35, java.sql.Types.VARCHAR);  //PV_LOG_REPOSITORY       IN OUT STRING,
			callableStatement.registerOutParameter(36, java.sql.Types.NUMERIC); 	//PV_TEST_CALL_LEVEL      IN OUT NUMBER,      
			callableStatement.registerOutParameter(37, java.sql.Types.NUMERIC); // PV_TRACE_LEVEL	PV_TRACE_LEVEL          IN OUT NUMBER,   
			callableStatement.registerOutParameter(38, java.sql.Types.VARCHAR); // PV_ERROR_CODE	PV_ERROR_CODE           IN OUT STRING,    
			callableStatement.registerOutParameter(39, java.sql.Types.NUMERIC); // PV_ERROR_SEVERITY
			callableStatement.registerOutParameter(40, java.sql.Types.VARCHAR); // PV_ERROR_MESSAGE

			callableStatement.executeUpdate();

			int PV_ERROR_SEVERITY_lc = -1;
			
			int PV_TEST_CALL_LEVEL_lc = callableStatement.getInt(36);
			String PV_ERROR_CODE_lc = callableStatement.getString(38);
			PV_ERROR_SEVERITY_lc = callableStatement.getInt(39);
			String PV_ERROR_MESSAGE_lc = callableStatement.getString(40);
			int PV_TRACE_LEVEL_lc = callableStatement.getInt(37);
			String PV_LOG_REPOSITORY_lc = callableStatement.getString(35);

			dbResponse.PV_TEST_CALL_LEVEL = Integer.toString(PV_TEST_CALL_LEVEL_lc);
			dbResponse.PV_ERROR_CODE = PV_ERROR_CODE_lc;
			dbResponse.PV_ERROR_SEVERITY = Integer.toString(PV_ERROR_SEVERITY_lc);
			dbResponse.PV_ERROR_MESSAGE = PV_ERROR_MESSAGE_lc;
			dbResponse.PV_TRACE_LEVEL = Integer.toString(PV_TRACE_LEVEL_lc);
			dbResponse.PV_LOG_REPOSITORY = PV_LOG_REPOSITORY_lc;

			return dbResponse;

		} catch (SQLException e) {
			e.printStackTrace();
			return dbResponse;
		} finally {

			if (callableStatement != null) {
				callableStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	public int addRecord(DBRequest dbRequest, String responseCd, String paymentReference, String trans_method_name,
			String trans_type, String channelCode, String Curr) throws Exception {
		
		String Isreversal = dbRequest.getNarration().startsWith("(Reversal)") ?  "True" : "False";
		
		//String content = CommonMethods.getInfo("core_systeminfo.txt", IntegrationSoapImpl.class);
		//content = CypherCrypt.deCypher(content);
		
		//String[] ipAndPort = content.split(",");
		//String	channelCode = ipAndPort[4].split("=>")[1].trim();
		//String Curr = ipAndPort[8].split("=>")[1].trim();

		Connection dbConnection = null;
		dbConnection = databaseConnection();

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String sql = "INSERT INTO  "
					+ "ALT_QUICKTELLER(TRAN_APPL,TRAN_REF,FROM_ACCT_NUM,TRAN_RECEIVER,TRAN_AMOUNT,TRAN_STATUS,TRAN_METHOD,NARRATION,TRAN_PURPOSE,CHANNEL_NAME ,ISREVERSAL,TRAN_DATE ,TRAN_TYPE ,PAYMENT_CURR, CHARGE_AMOUNT,TAX_AMOUNT) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = dbConnection.prepareStatement(sql);
			pst.setString(1, dbRequest.getInitiatingApp()); // TRAN_APPL
			pst.setString(2, paymentReference); // TRAN_REF
			pst.setString(3, dbRequest.getSenderAcctNo()); // FROM_ACCT_NUM
			pst.setString(4, dbRequest.getBeneficiaryAcctNo()); // TRAN_RECEIVER
			pst.setDouble(5, Double.valueOf(dbRequest.getAmount())); // TRAN_AMOUNT
			pst.setString(6,responseCd); // TRAN_STATUS
			pst.setString(7, trans_method_name); // TRAN_NETHOD
			pst.setString(8, dbRequest.getNarration()); // NARRATION
			pst.setString(9, ""); // TRAN_PURPOSE
			pst.setString(10, channelCode); // CHANNEL_NAME
			pst.setString(11, Isreversal); // ISREVERSAL
			pst.setDate(12, getCurrentDate()); // TRAN_DATE
			pst.setString(13, trans_type); // TRAN_TYPE
			pst.setString(14, Curr); // PAYMENT_CURR	,		
			pst.setString(15, dbRequest.getChargeAmount()); //CHARGE_AMOUNT
			pst.setString(16, dbRequest.getTaxAmount()); //TAX_AMOUNT
			int result = pst.executeUpdate();

			System.out.println("Table ALT_QUICKTELLER Insert successful");
			
			return result;
		}

		catch (Exception e) {
			 e.printStackTrace();
			System.out.println("Table ALT_QUICKTELLER Insert failed");
			return -1;
		} finally {

			if (dbConnection != null) {
				dbConnection.close();
			}
			if (pst != null) {
				pst.close();
			}
			if (rs != null) {
				rs.close();
			}
		}

	}
	
	// end very specific methods.
	

	
	
	
	protected static java.sql.Date getCurrentDate() {
	    java.util.Date today = new java.util.Date();
	    //System.out.println("dateString1: " + new java.sql.Date(today.getTime()));
	    return new java.sql.Date(today.getTime());
	}
	
	protected static java.sql.Date getCurrentDate(String inputDate, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		
		try {
			if (inputDate.trim().length() > 0)
				javaDate = sdf.parse(inputDate);
			
		} catch (ParseException e) {}		
	    return new java.sql.Date(javaDate.getTime());
	}
	
	protected static java.sql.Date getCurrentDate(String inputDate) {		
	    return getCurrentDate(inputDate, "dd/MM/yyyy");
	}
	
	protected static java.sql.Date getCurrentTimestamp(String inputDate, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		
		try {
			javaDate = sdf.parse(inputDate);
		} catch (ParseException e) {}
		
	    return new java.sql.Date(javaDate.getTime());
	}
	
	protected static java.sql.Date getCurrentTimestamp(String inputDate) {
	    return getCurrentTimestamp(inputDate, "dd/MM/yyyy HH:mm:ss a");
	} 
	
	
	
	private String getDatabaseDriver(String databaseType) {
		String driver = "";
		switch(databaseType.toUpperCase()) {
		case ResponseConstants.ORACLE_DATABASE : 
			driver = ResponseConstants.ORACLE_DRIVER;
			break;
		case ResponseConstants.SYBASE_DATABASE : 
			driver = ResponseConstants.SYBASE_DRIVER;
			break;
		}
		
		return driver;
	}

	private String getDatabaseConnectionUrl(String databaseType, String ipAddress, String portNo, String serviceName) {
		String connectionUrl = "";
		switch(databaseType.toUpperCase()) {
		case ResponseConstants.ORACLE_DATABASE : 
			connectionUrl = ResponseConstants.ORACLE_CONNECTION_URL_PREFIX + ipAddress + ":" + portNo + "/" + serviceName;
			break;
		case ResponseConstants.SYBASE_DATABASE : 
			connectionUrl = ResponseConstants.SYBASE_CONNECTION_URL_PREFIX + ipAddress + ":" + portNo + "/" + serviceName;
			break;
		}
		//10.152.2.32:5000/banking
		return connectionUrl;
	}
	
	public Database readConfig() {
		Database database = new Database();
		
		try {
			String content = CommonMethods.getInfo("DatabaseInfo.xml", DBConnection.class);
			
			database = CommonMethods.xmlStringToObject(content, Database.class);
			database = decryptContent(database);
			
			database.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		} catch (Exception e) {
			System.out.println("Cannot read DatabaseInfo.xml");
			database.setResponseCode(ResponseConstants.FILE_ERROR_CODE);
		}
		
		return database;
	}
	
	private Database decryptContent(Database database) {
		
		Database databaseDup  = database;
		try {
			if (!database.getDatabaseProps().isEmpty())
				for (DatabaseProperty dbProperty : database.getDatabaseProps()) {
					String type = CypherCrypt.deCypher(dbProperty.getType().trim()) == null || CypherCrypt.deCypher(dbProperty.getType().trim()).equals("")
							? dbProperty.getType() : CypherCrypt.deCypher(dbProperty.getType());
							
					String username = CypherCrypt.deCypher(dbProperty.getUsername().trim()) == null || CypherCrypt.deCypher(dbProperty.getUsername().trim()).equals("")
							? dbProperty.getUsername() : CypherCrypt.deCypher(dbProperty.getUsername());
							
					String password = CypherCrypt.deCypher(dbProperty.getPassword().trim()) == null || CypherCrypt.deCypher(dbProperty.getPassword().trim()).equals("")
							? dbProperty.getPassword() : CypherCrypt.deCypher(dbProperty.getPassword());
							
					String ipAddress = CypherCrypt.deCypher(dbProperty.getIpAddress().trim()) == null || CypherCrypt.deCypher(dbProperty.getIpAddress().trim()).equals("")
							? dbProperty.getIpAddress() : CypherCrypt.deCypher(dbProperty.getIpAddress());
							
					String portNumber = CypherCrypt.deCypher(dbProperty.getPortNumber().trim()) == null || CypherCrypt.deCypher(dbProperty.getPortNumber().trim()).equals("")
							? dbProperty.getPortNumber() : CypherCrypt.deCypher(dbProperty.getPortNumber());
							
					String serviceName = CypherCrypt.deCypher(dbProperty.getServiceName().trim()) == null || CypherCrypt.deCypher(dbProperty.getServiceName().trim()).equals("")
							? dbProperty.getServiceName() : CypherCrypt.deCypher(dbProperty.getServiceName());
										
					dbProperty.setType(type);
					dbProperty.setUsername(username);
					dbProperty.setPassword(password);
					dbProperty.setIpAddress(ipAddress);
					dbProperty.setPortNumber(portNumber);
					dbProperty.setServiceName(serviceName);
				}

			return database;
		} catch (Exception e) {
			System.out.println("DatabaseCredential: \n" + CommonMethods.objectToXml(database));
			System.out.println("Cannot decrypt content");
			
			return databaseDup;
		}
		
	}
	
	public String XAPI_POSTING_SERVICE_DR(DBRequest dbRequest, String senderName,
			String serviceCode, String transactionRef, String reversalFlag, String chargeCode) {
		
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();		
		String channelCode = rubikonCredential.getChannelCode();
		String taxCode = rubikonCredential.getTaxCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String appUsername = rubikonCredential.getApplicationUsername();
		String transType = rubikonCredential.getDebitTransCode();
		
		
		String contraCurrencyCode =!dbRequest.getContraAmount().equals("0") ? currencyCode : "";
		transactionRef = transactionRef.isEmpty() 
				? "INT-TXN-|" + new Date() + "|" + new Date().getTime()
				: transactionRef;
				
		DBResponse dbResponse = new DBResponse();
		
		try {
			dbResponse = ProcedureXAPI_POSTING_SERVICE(
					channelCode, // PV_CHANNEL_CD
					"", // PV_CHANNEL_PWD
					"123456789", // PV_DEVICE_ID
					serviceCode, // PV_SERVICE_CD
					transType, // PV_TRANS_TYPE_CD ***
					dbRequest.getSenderAcctNo(), // PV_ACCOUNT_NO,
					currencyCode, // PV_ACCOUNT_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getAmount()))), // PV_ACCOUNT_AMOUNT,
					currencyCode, // PV_TRANS_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getAmount()))), // PV_TRANS_AMOUNT,
					
					new Date(), // PV_VALUE_DATE,
					new Date(), // PV_TRANS_DATE,
					transactionRef, // PV_TRANS_REF,
					transactionRef, // PV_SUPPLEMENTARY_REF,
					dbRequest.getNarration(), // PV_NARRATIVE,
					dbRequest.getBeneficiaryAcctNo(), // PV_CONTRA_ACCOUNT_NO,
					contraCurrencyCode, // PV_CONTRA_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getContraAmount()))), // PV_CONTRA_AMOUNT,
					new BigDecimal(0), // PV_EXCHANGE_RATE,
					chargeCode, // PV_CHARGE_CD,
					
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getChargeAmount()))), // PV_CHARGE_AMOUNT,
					taxCode, // PV_TAX_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getTaxAmount()))), // PV_TAX_AMOUNT,
					reversalFlag, // PV_REVERSAL_FLG,
					"", // PV_ORIGIN_BANK_CD,
					"", // PV_SERVICE_PROVIDER,
					"", // PV_SERVICE_PROVIDER_SVC,
					"", // PV_USER_TYPE,
					appUsername, // PV_USER_ID,
					"", // PV_USER_PWD,
					
				   "12:45:30", // PV_TRANS_TIME
				   "N", // PV_EXTERNAL_COMMIT
				   "Y", // PV_PREVENT_DUPLICATES
				   "123456789", //PV_REQUEST_PWD
				   "DB", // PV_LOG_REPOSITORY
				   0, // PV_TEST_CALL_LEVEL
				   3, // PV_TRACE_LEVEL
					"", // PV_ERROR_CODE,
					0, // PV_ERROR_SEVERITY,
					"" // PV_ERROR_MESSAGE,
					);
		} catch (Exception e) {
			
			return "";
		}

		String responseCode = dbResponse.PV_ERROR_SEVERITY == null || dbResponse.PV_ERROR_SEVERITY.equals("0") ? "00"
				: dbResponse.PV_ERROR_SEVERITY;
		
		System.out.println("callProdecure_TEST_TRANS_SERVICE Response: " + responseCode);

		//return response
		return responseCode;
	}
	
	
}
