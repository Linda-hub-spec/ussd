package com.company.application.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.application.account.data.AccountHistory;
import com.company.application.account.data.AccountResponse;
import com.company.application.account.data.MiniStatement;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class AccountDBOperation extends DBConnection{

	
	public AccountDBOperation() {
		super("Oracle");
	}
	
	public AccountDBOperation(String databaseName) {
		super(databaseName);
	}
		
	public AccountDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
    //select mini statement
	public List<MiniStatement> selectMiniStatement(String accountNo) throws Exception {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		List<MiniStatement> miniStatementLst = new ArrayList<MiniStatement>();
		
		try {
			String sql = "SELECT TO_CHAR(SYS_CREATE_TS, 'DD/MM/YYYY') \"DATE\", DR_CR_IND CR_DR, TXN_AMT AMOUNT, TRAN_REF_TXT REF_NO \r\n" + 
					"FROM DEPOSIT_ACCOUNT_HISTORY \r\n" + 
					"WHERE TO_CHAR(SYS_CREATE_TS, 'MM') = TO_CHAR(SYSDATE, 'MM') \r\n" + 
					"AND DEPOSIT_ACCT_ID = (SELECT ACCT_ID FROM ACCOUNT WHERE ACCT_NO = ?)";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, accountNo);
			
			rs = preparedStatement.executeQuery();
			while(rs.next()) {
				MiniStatement miniStatement = new MiniStatement();
				miniStatement.setDate(rs.getString(1));
				miniStatement.setCreditDebit(rs.getString(2));
				miniStatement.setAmount(rs.getString(3));
				miniStatement.setRefNo(rs.getString(4));
				
				miniStatementLst.add(miniStatement);
			}
			
			return miniStatementLst;
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
	
	
    //select multi-account
	public List<AccountResponse> selectMultiAccount(String accountNo) throws Exception {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		List<AccountResponse> accounts = new ArrayList<AccountResponse>();
		
		try {
			String sql = "SELECT T1.CUST_ID, T2.ACCT_NO, T2.ACCT_NM,T2.REC_ST,T2.PROD_CAT_TY, T3.LEDGER_BAL, T4.CRNCY_CD_ISO,T5.CONTACT, T7.ACCESSPIN,T8.REF_DESC\r\n" + 
					"FROM CUSTOMER T1 join ACCOUNT T2 on T1.CUST_ID = T2.CUST_ID\r\n" + 
					"join DEPOSIT_ACCOUNT_SUMMARY T3 on T2.ACCT_ID = T3.DEPOSIT_ACCT_ID\r\n" + 
					"join CURRENCY T4 on T2.CRNCY_ID = T4.CRNCY_ID AND T4.CRNCY_CD_ISO = 'NGN'\r\n" + 
					"left join CUSTOMER_CONTACT_MODE T5 on T1.CUST_ID = T5.CUST_ID AND REGEXP_LIKE (T5.CONTACT, '^(\\+|[0-9])') \r\n" + 
					"left join CONTACT_MODE_REF T6 on T5.CONTACT_MODE_ID = T6.CONTACT_MODE_ID\r\n" + 
					"and T6.CONTACT_MODE_ID IN (237,231,236) left join ALT_MAPP_DEVICE T7 on T2.ACCT_NO = T7.ACCT_NUM\r\n" + 
					"left join PRODUCT_CATEGORY_REF T8 on T8.REF_KEY = T2.PROD_CAT_TY\r\n" + 
					"where T1.CUST_ID IN (SELECT CUST_ID FROM ACCOUNT WHERE ACCT_NM =\r\n" + 
					"(SELECT ACCT_NM FROM ACCOUNT WHERE ACCT_NO = ?))\r\n" + 
					//"AND T1.CUST_NM = (SELECT ACCT_NM FROM ACCOUNT WHERE ACCT_NO = ?)\r\n" + 
					"order by 8 desc";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, accountNo);
			//preparedStatement.setString(2, accountNo);
			
			rs = preparedStatement.executeQuery();
			while(rs.next()) {
				AccountResponse nameInquiry = new AccountResponse();
				
				nameInquiry.setAccountNumber(rs.getString(2));
				nameInquiry.setAccountName(rs.getString(3));
				nameInquiry.setAccountStatus(rs.getString(4));
				nameInquiry.setAccountType(rs.getString(10));
				nameInquiry.setLedgerBalance(rs.getBigDecimal(6) + "");
				nameInquiry.setCurrencyCode(rs.getString(7));
				nameInquiry.setPhoneNumber(rs.getString(8));
				nameInquiry.setAccessPin(rs.getString(9));
				
				accounts.add(nameInquiry);
			}
			
			return accounts;
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
	
    //select account history
	public List<AccountHistory> selectAccountHistory(String accountNo) throws Exception {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		List<AccountHistory> accountHistory = new ArrayList<AccountHistory>();
		
		try {
			String sql = "select T2.PROD_CAT_TY,T3.REF_DESC,T1.tran_desc, T1.txn_amt, T1.stmnt_bal, T1.sys_create_ts,to_char(T1.sys_create_ts,'dd-MON-yyyy')\r\n" + 
					"from deposit_account_history T1 join ACCOUNT T2 on T1.acct_no = T2.acct_no \r\n" + 
					"left join PRODUCT_CATEGORY_REF T3 on T3.REF_KEY = T2.PROD_CAT_TY\r\n" + 
					"where t1.acct_no = ?  \r\n" + 
					"order by sys_create_ts desc";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, accountNo);
			
			rs = preparedStatement.executeQuery();
			while(rs.next()) {
				AccountHistory acctHist = new AccountHistory();
				
				acctHist.setAccountType(rs.getString(2));
				acctHist.setTransactionDesc(rs.getString(3));
				acctHist.setTransactionAmount(rs.getString(4));
				acctHist.setBalanceAfter(rs.getString(5));
				acctHist.setTransactionDate(rs.getString(7));
				
				accountHistory.add(acctHist);
			}
			
			return accountHistory;
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
	
    //select the sum of daily transaction done
	public String selectDailyTranxDone(String accountNo, String transactionMethod) throws Exception {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
				
		try {
			String sql = "select nvl(sum(tran_amount),0)\r\n" + 
					"from alt_quickteller\r\n" + 
					"where from_acct_num = ? and tran_nethod = ? \r\n" + 
					"and to_char(system_ts,'dd-MON-yyyy') = to_char(sysdate,'dd-MON-yyyy')";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, accountNo);
			preparedStatement.setString(2, transactionMethod);
			
			rs = preparedStatement.executeQuery();
			String doneTransaction = "0";
			while(rs.next()) {
				doneTransaction = rs.getString(1) + "";
			}
			
			return doneTransaction;
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
	
	
	public String accountName(String accountNo) throws Exception {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
				
		try {
			String sql = "select acct_no, acct_nm from account " + 
						"where acct_no = ?";
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, accountNo);
			
			rs = preparedStatement.executeQuery();
			
			String accountName = "";
			while(rs.next()) {
				accountName = rs.getString(2);
			}
			
			return accountName;
		} catch (SQLException e) {
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
	
	
	
}
