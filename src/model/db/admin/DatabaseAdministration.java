package model.db.admin;

import static model.domain.Utility.nullCheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.db.Constants;
import model.db.Constants.Files;
import model.db.Constants.Tables;
import model.db.DatabaseAccessObject;

public class DatabaseAdministration {

	private static final boolean CREATE_TABLES = true;
	private static final boolean DROP_TABLES = true;

	private static final DatabaseAccessObject DAO = new DatabaseAccessObject();
	private static final TableAdminOperations TAO = new TableAdminOperations();

	/**
	 * Create all tables pointed to by {@link Constants.Tables} enum.
	 */
	protected static void createTables() {
		try (Connection c = DriverManager.getConnection(Files.DATABASE.toString());
				Statement stmtCreateTables = c.createStatement();
				Statement stmtCheckTables = c.createStatement();) {
			for (Tables t : Tables.values()) {
				System.out.println("Schema for table " + t.tableName() + ":");
				System.out.println(TAO.createTable(t));
				try (ResultSet rs = stmtCheckTables.executeQuery("SELECT * FROM sqlite_schema WHERE type='table' AND name='" + t.tableName() + "'");) {
					if (rs.next()) {
						System.out.println("OPERATION: Table " + t.tableName() + " already exists.");
					} else {
						stmtCreateTables.executeUpdate(TAO.createTable(t));
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
					stmtDropTables.executeUpdate(TAO.dropTable(t));
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

	private static class TableAdminOperations {

		/**
		 * Generate DROP TABLE SQL statements for a {@link Tables} enum.
		 * 
		 * @param table
		 * @return an SQL code String that will drop a table specified by {@code table} parameter.
		 */
		protected synchronized static String dropTable(Tables table) throws IllegalArgumentException {
			nullCheck(table);
			StringBuilder b = new StringBuilder();
			b.append("DROP TABLE ");
			b.append(table.tableName());
			b.append(";");

			return b.toString();

		}

		/**
		 * Generate CREATE TABLE statements for a {@link Tables} enum.
		 * 
		 * @param table
		 * @return an SQL code String that will create a table specified by {@code table} parameter. Formatted pleasingly for .schema viewing.
		 */
		protected synchronized static String createTable(Tables table) throws IllegalArgumentException {
			nullCheck(table);
			StringBuilder b = new StringBuilder();
			b.append("CREATE TABLE ");
			b.append(table.tableName());
			b.append(" (%n");
			for (int i = 0; i < table.columns().length; i++) {
				b.append("\t");
				b.append(table.columns()[i]);
				b.append(",%n");
			}
			b.append("\tPRIMARY KEY(");
			b.append(table.primaryKey());
			b.append(")");
			if (table.foreignKeys() != null) {
				b.append(",%n");
				b.append("\tFOREIGN KEY(");
				b.append(table.foreignKeys()[0]);
				b.append(") REFERENCES ");
				b.append(table.foreignKeys()[1]);
				b.append(" (");
				b.append(table.foreignKeys()[0]);
				b.append(")%n");
			} else {
				b.append("%n");
			}
			b.append(");");
			return String.format(b.toString());
		}
	}
}
