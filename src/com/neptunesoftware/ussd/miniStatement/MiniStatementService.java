package com.neptunesoftware.ussd.miniStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.neptunesoftware.ussd.connection.DbConnection;

public class MiniStatementService {
	
	public MiniStatementResponse miniStatement(MiniStatementRequest miniRequest) {
		
		
		DbConnection connect = new DbConnection();
		Connection con = null;
		ResultSet result = null;
		PreparedStatement statement = null;
		
		List<MiniStatement> miniResult = new ArrayList<MiniStatement>();
		MiniStatementResponse miniResponse = new MiniStatementResponse();
		
		String sql ="SELECT to_char(sys_create_ts, 'DD/MM/YYYY') \"DATE\", dr_cr_ind, txn_amt,tran_ref_txt  "
				+ "FROM deposit_account_history " + 
				" WHERE acct_no = ? "
				+ "AND to_char(sys_create_ts, 'MM') = to_char(sysdate, 'MM') order by 1 desc";
		
		/*
		 * String sql =
		 * "SELECT tran_dt, dr_cr_ind, txn_amt, tran_ref_txt FROM deposit_account_history "
		 * + "WHERE acct_no = ? AND to_char(tran_dt, 'MON') = to_char(sysdate, 'MON')";
		 */
		
		try {
			
			con = connect.connector();
			statement = con.prepareStatement(sql);
			statement.setString(1, miniRequest.getAccountNumber());
			result = statement.executeQuery();

			miniResponse.setStatusCode(400);
			miniResponse.setSuccessMessage("Record not found");
			
		
			while (result.next()) {
				MiniStatement miniData = new MiniStatement();
				
				miniData.setTransactionDate(result.getString(1));
				miniData.setTransactionType(result.getString(2));
				miniData.setTransactionAmount(result.getString(3));
				miniData.setTransactionRefNo(result.getString(4));

				miniResult.add(miniData);

				miniResponse.setStatusCode(200);
				miniResponse.setSuccessMessage("Success");

			}
			miniResponse.setMiniResponse(miniResult);
			
			return miniResponse;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
		

	}

}
