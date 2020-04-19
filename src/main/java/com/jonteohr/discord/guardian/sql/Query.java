package com.jonteohr.discord.guardian.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.jonteohr.discord.guardian.property.PropertyHandler;

public class Query {
	
	private PropertyHandler prop = new PropertyHandler();
	
	/**
	 * Executes a query to the database.
	 * @param query {@link java.lang.String String} SQL query
	 * @return {@code true} if success
	 * @see #queryGet(String)
	 * @throws Exception
	 */
	public boolean queryExec(String query) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(
				"jdbc:mysql://" + prop.loadProperty("db.host") + ":3306/" + prop.loadProperty("db.name") + "?serverTimezone=UTC",
				prop.loadProperty("db.user"),
				prop.loadProperty("db.pass")
			);
			
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			
			con.close();
			
			return true;
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Retrieves a ResultSet from the database.
	 * @param query {@link java.lang.String String} SQL query
	 * @return {@link java.sql.ResultSet ResultSet} with results from query
	 * @see #queryExec(String)
	 * @throws Exception
	 */
	public ResultSet queryGet(String query) {
		ResultSet res;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(
				"jdbc:mysql://" + prop.loadProperty("db.host") + ":3306/" + prop.loadProperty("db.name") + "?serverTimezone=UTC",
				prop.loadProperty("db.user"),
				prop.loadProperty("db.pass")
			);
			
			Statement stmt = con.createStatement();
			res = stmt.executeQuery(query);
			
			return res;
		} catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
