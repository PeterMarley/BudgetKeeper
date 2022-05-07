package model.db.admin;

import static model.domain.Utility.nullCheck;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.SortedSet;

import model.db.Constants;
import model.db.Constants.Files;
import model.db.Constants.Tables;
import model.domain.Month;
import model.domain.Transaction;
import model.db.DatabaseAccessObject;
import model.db.SQLFactory;

public class DatabaseAdministration extends DatabaseAccessObject {

	/**
	 * Adds a Collection of {@code Transaction} objects to the {@code transaction} table in database, with column {@code monthID} set to parameter
	 * {@code monthID}. The {@code Transaction}s are only added if they are not already part of the data for the {@code Month} pointed to by
	 * {@code monthID}.
	 * 
	 * @param transaction
	 * @param monthID
	 */
	public void addTransactions(Collection<Transaction> transactions, int monthID) {
		try (Connection c = getConnection()) {
			SortedSet<Transaction> transactionsForMonth = queryTransactions(monthID);
			for (Transaction t : transactions) {
				if (!transactionsForMonth.contains(t)) {
					addTransaction(t, monthID);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes a transaction to the database IF the database does not already contain an {@link model.domain.Transaction#equals(Object) equal} transaction
	 * for this month.
	 * 
	 * @param t
	 * @param monthID
	 * @return
	 */
	public int addTransaction(Transaction t, int monthID) {
		int transKey = SENTINEL_RETURN;
		try (Connection c = getConnection();
			PreparedStatement stmtGetTransactions = c.prepareStatement(SQLFactory.READ_TRANSACTIONS_WHERE_MONTHID)) {
			stmtGetTransactions.setInt(1, monthID);
			SortedSet<Transaction> transactions = queryTransactions(monthID);
			if (transactions.contains(t)) {
				System.out.println("This transaction already exists in database for this month");
			} else {
				try (PreparedStatement stmtAddTrans = c.prepareStatement(SQLFactory.INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {
					//"INSERT INTO transactions (monthID, name, paid, income, date, type, value) VALUES (?,?,?,?,?);";

					stmtAddTrans.setInt(1, t.getTransactionID());
					stmtAddTrans.setInt(2, monthID);
					stmtAddTrans.setString(3, t.getName());
					stmtAddTrans.setInt(4, t.isPaid() ? 1 : 0);
					stmtAddTrans.setInt(5, t.isIncome() ? 1 : 0);
					stmtAddTrans.setString(6, t.getDate().format(Constants.FORMAT_YYYYMMDD));
					stmtAddTrans.setString(7, t.getType().name());
					stmtAddTrans.setDouble(8, t.getAbsoluteValue());
					transKey = stmtAddTrans.executeUpdate();
					System.out.println("Transaction Successfully added.");
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transKey;
	}

	/**
	 * Adds a month to database {@code months} table, if it is not already present (decided by {@link model.domain.Transaction#equals(Object)
	 * equals}).<br>
	 * <br>
	 * 
	 * If a {@link model.domain.Month Month} is added, all it's {@link model.domain.Transaction transactions} are also added to the {@code transactions}
	 * table.
	 * 
	 * @param m
	 * @return the primary key of the new month in database, or {@value #SENTINEL_RETURN} if the month was not added
	 */
	public int addMonth(Month m) {
		int generatedPrimaryKey = SENTINEL_RETURN;
		//TODO add transactions
		System.out.println("Attempting to add Month " + m.toString() + " to database.");

		// make connection to database, and create 2 prepared statements
		try (Connection c = super.getConnection();
			PreparedStatement stmtSearch = c.prepareStatement(SQLFactory.READ_MONTHS_WHERE_DATE);
			PreparedStatement stmtAdd = c.prepareStatement(SQLFactory.INSERT_MONTH, Statement.RETURN_GENERATED_KEYS)) {

			// search for month, to see if it already exists
			stmtSearch.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
			try (ResultSet rsSearch = stmtSearch.executeQuery()) {
				boolean notInDB = true;
				while (rsSearch.next()) {
					notInDB = false;// results set should have no rows if this month is not in database
				}

				// if no results, then add month
				if (notInDB) {
					stmtAdd.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
					stmtAdd.executeUpdate();
					try (ResultSet keys = stmtAdd.getGeneratedKeys();) { // capture primary key of new month
						generatedPrimaryKey = keys.getInt(1);
						if (generatedPrimaryKey != SENTINEL_RETURN) { // if adding the month was successful add transactions for the month
							addTransactions(m.getTransactions(), generatedPrimaryKey);
							System.out.println("Month " + m.toString() + " successfully added!");
						}
					}
				} else {
					System.out.println("Month " + m.getDate().format(Constants.FORMAT_YYYYMM) + " already exists!");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return generatedPrimaryKey;
	}

	/**
	 * Create all tables pointed to by {@link Constants.Tables} enum.
	 */
	protected void createTables() {
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
			e.printStackTrace();
		}

	}

	/**
	 * Drop all tables specified in {@link model.db.Constants.Tables Tables} enum.
	 */
	protected void dropTables() {
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
			e.printStackTrace();
		}
	}

	/**
	 * Generate DROP TABLE SQL statements for a {@link Tables} enum.
	 * 
	 * @param table
	 * @return an SQL code String that will drop a table specified by {@code table} parameter.
	 */
	protected synchronized String dropTable(Tables table) throws IllegalArgumentException {
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
	protected synchronized String createTable(Tables table) throws IllegalArgumentException {
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
