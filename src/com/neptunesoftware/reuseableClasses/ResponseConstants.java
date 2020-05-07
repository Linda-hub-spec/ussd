package com.neptunesoftware.reuseableClasses;

public class ResponseConstants {
	/*
	 * Response Codes and Messages
	 */

	// success
	public static final String SUCCEESS_CODE = "00";
	public static final String SUCCEESS_CODE_2 = "200";
	public static final String SUCCEESS_MESSAGE = "Successful";

	// not found
	public static final String NOT_FOUND_CODE = "01";
	public static final String NOT_FOUND_MESSAGE = "Not Found";

	// query error
	public static final String QUERY_CODE = "02";
	public static final String QUERY_MESSAGE = "Check your query";

	// exception
	public static final String EXCEPTION_CODE = "03";

	// procedure error
	public static final String PROCEDURE_CODE = "04";
	public static final String PROCEDURE_MESSAGE = "Procedure failed with response ";

	// already exist
	public static final String EXIST_CODE = "05";
	public static final String EXIST_MESSAGE = "Record already exist ";

	// web service unavailable
	public static final String WEBSERVICE_UNAVAILABLE_CODE = "06";
	public static final String WEBSERVICE_UNAVAILABLE_MESSAGE = "Web Service unreachable";

	// web service returned response code other than success
	public static final String WEBSERVICE_FAILED_RESPONSE_CODE = "07";

	// converting string to java object
	public static final String UNMARSHAL_CODE = "08";
	public static final String UNMARSHAL_MESSAGE = "Unmarshalling/Deserialize error: Could not convert String to class object."
													+ "\n Check xml/json String to object class created";

	// mandatory parameter has not been passed
	public static final String MANDATORY_CODE = "09";
	public static final String MANDATORY_MESSAGE = "A mandatory parameter has not been passed: ";

	//file error
	public static final String FILE_ERROR_CODE = "10";
	public static final String FILE_ERROR_MESSAGE = "Cannot read from file. Check whether it exist: ";

	//insufficient funds
	public static final String INSUFFICIENT_CODE = "11";
	public static final String INSUFFICIENT_MESSAGE = "Insufficient Funds";
	
	
	// zenith web service returned response calling authentication endpoint
	public static final String ZENITH_AUTHENTICATION_MESSAGE = "00-Success";

	public static final String CREDIT_SCORE_RATING_SUCCESS = "GOOD";
	public static final String CREDIT_SCORE_RATING_FAILURE = "BAD";

	public static final String SERVICE_TYPE_CREDIT_BUREAU = "CREDIT_BUREAU_SCORE";

	// 00 - Success
	// 01 - Not found
	// 02 - wrong query
	// 03 - exception
	// 04 - procedure failure
	// 05 - record already exist
	// 06 - Zenith's web service is unreachable
	// 07 - Zenith's web service returned a failure response code
	// 08 - unexpected response received from Zenith's web service
	// 09 - mandatory parameter has not been passed

	/*
	 * Database Types
	 */

	// oracle
	public static final String ORACLE_DATABASE = "ORACLE";
	public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String ORACLE_CONNECTION_URL_PREFIX = "jdbc:oracle:thin:@";

	// sybase
	public static final String SYBASE_DATABASE = "SYBASE";
	public static final String SYBASE_DRIVER = "com.sybase.jdbc3.jdbc.SybDriver";
	public static final String SYBASE_CONNECTION_URL_PREFIX = "jdbc:sybase:Tds:";

}
