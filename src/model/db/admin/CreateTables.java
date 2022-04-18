package model.db.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.db.admin.Enums.Files;
import model.db.admin.Enums.Tables;

import static model.db.admin.SQLGenerator.*;

public class CreateTables {

	private static final boolean CREATE_TABLES = true;

	public static void main(String[] args) {
		System.out.println("Tasks:");
		if (CREATE_TABLES) {
			System.out.println("Creating tables: Start.");
			createTables();
			System.out.println("Creating tables: End.");
		}
	}

	//--------------------------\
	//	Actions					|
	//--------------------------

	private static void createTables() {
		try (Connection c = DriverManager.getConnection(Files.DATABASE.toString());
				Statement stmtCreateTables = c.createStatement();
				Statement stmtCheckTables = c.createStatement();) {
			for (Tables t : Tables.values()) {
				System.out.println("Schema for table " + t.tableName() + ":");
				System.out.println(createTableSQL(t));
				try (ResultSet rs = stmtCheckTables.executeQuery("SELECT * FROM sqlite_schema WHERE type='table' AND name='" + t.tableName() + "'");) {
					if (rs.next()) {
						System.out.println("OPERATION: Table " + t.tableName() + " already exists.");
					} else {
						stmtCreateTables.executeUpdate(createTableSQL(t));
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

}
