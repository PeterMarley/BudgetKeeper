package model.db.admin;

import static model.db.admin.SQLGenerator.dropTablesSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import model.db.admin.Enums.Files;
import model.db.admin.Enums.Tables;

public class DropTables {

	private static final boolean DROP_TABLES = true;

	public static void main(String[] args) {
		if (DROP_TABLES) {
			dropTables();
		}
	}

	private static void dropTables() {
		try (Connection c = DriverManager.getConnection(Files.DATABASE.toString())) {
			for (Tables t : Tables.values()) {

				try (Statement stmtDropTables = c.createStatement();) {
					stmtDropTables.executeUpdate(dropTablesSQL(t));
					System.out.println("Table " + t.tableName() + " dropped.");
				} catch (SQLException e) {
					System.out.println("Could not drop " + t.tableName());
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
