package model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import model.db.Constants.Files;

public class IDGenerator {
	/**
	 * Unified source for database connections in DatabaseAccessObject
	 * 
	 * @return SQL Connection object
	 * @throws SQLException if connection establishment fails
	 */
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(Files.DATABASE.toString());
	}
	
	
}

