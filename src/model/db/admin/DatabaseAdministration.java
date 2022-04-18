package model.db.admin;

import static model.db.SQLFactory.createTable;
import static model.db.SQLFactory.dropTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.db.Constants;
import model.db.Constants.Files;
import model.db.Constants.Tables;

public class DatabaseAdministration {

	private static final boolean CREATE_TABLES = true;
	private static final boolean DROP_TABLES = true;

	/**
	 * Create all tables pointed to by {@link Constants.Tables} enum.
	 */
	protected static void createTables() {
		try (Connection c = DriverManager.getConnection(Files.DATABASE.toString());
				Statement stmtCreateTables = c.createStatement();
				Statement stmtCheckTables = c.createStatement();) {
			for (Tables t : Tables.values()) {
				System.out.println("Schema for table " + t.tableName() + ":");
				System.out.println(createTable(t));
				try (ResultSet rs = stmtCheckTables.executeQuery("SELECT * FROM sqlite_schema WHERE type='table' AND name='" + t.tableName() + "'");) {
					if (rs.next()) {
						System.out.println("OPERATION: Table " + t.tableName() + " already exists.");
					} else {
						stmtCreateTables.executeUpdate(createTable(t));
						System.out.println("OPERATION: Table " + t.tableName() + " created.");
					}
				}
				System.out.println();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected static void dropTables() {
		try (Connection c = DriverManager.getConnection(Files.DATABASE.toString())) {
			for (Tables t : Tables.values()) {

				try (Statement stmtDropTables = c.createStatement();) {
					stmtDropTables.executeUpdate(dropTable(t));
					System.out.println("OPERATION: Table " + t.tableName() + " dropped.");
				} catch (SQLException e) {
					System.out.println("OPERATION: Could not drop " + t.tableName());
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
