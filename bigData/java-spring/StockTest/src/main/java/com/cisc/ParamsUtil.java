package com.cisc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ParamsUtil {

	public static void main(String[] args) throws Exception {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement statement = conn.prepareStatement("select t.tradingcode from webtrade_tmp t");
		ResultSet rs =  statement.executeQuery();
		while(rs.next()) {
			System.out.println(rs.getString(1));
		}
	}

	public static List<String> getParams() {
		List<String> list = new ArrayList<>();
		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement statement = conn.prepareStatement("select t.tradingcode from webtrade_tmp t");
			ResultSet rs =  statement.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
