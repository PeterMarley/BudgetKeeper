package model.db;

import model.db.Constants.Files;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides methods needed to interact with the BudgetKeeper programs database ({@link})
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class DatabaseAccessObject {

	public static final int SENTINEL_RETURN = -1;

	/**
	 * <pre>
	 * CREATE TABLE transactions (
	 * 	transactionID INTEGER NOT NULL,
	 * 	monthID INTEGER NOT NULL,
	 * 	income INTEGER,
	 * 	date TEXT,
	 * 	type TEXT,
	 * 	value REAL,
	 * 	PRIMARY KEY(transactionID),
	 * 	FOREIGN KEY(monthID) REFERENCES months (monthID)
	 * );
	 *
	 * CREATE TABLE months (
	 * 	monthID INTEGER NOT NULL,
	 * 	date TEXT,
	 * 	PRIMARY KEY(monthID)
	 * );
	 * </pre>
	 */

	/**
	 * Unified source for database connections in DatabaseAccessObject
	 * 
	 * @return SQL Connection object
	 * @throws SQLException if connection establishment fails
	 */
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(Files.DATABASE.toString());
	}

	//------------------------------\
	//	Mapped Returns				|
	//------------------------------/

	public HashMap<Integer, Month> queryMonths() {
		HashMap<Integer, Month> resultsMap = new HashMap<Integer, Month>();

		// get connection to database and create read months query
		try (Connection c = getConnection();
				Statement stmtGetAllMonths = c.createStatement()) {

			// execute query
			try (ResultSet rs = stmtGetAllMonths.executeQuery(SQLFactory.pullMonths())) {
				// iterate through ResultSet constructing months and adding them
				while (rs.next()) {
					int monthID = rs.getInt("monthID");
					Month month = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
					month.addTransactions(pullTransactionsForMonth(monthID));
					resultsMap.put(monthID, month);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// return results
		return resultsMap;

	}

	public HashMap<Integer, Month> queryMonthsForYear(int year) {
		HashMap<Integer, Month> resultsMap = new HashMap<Integer, Month>();

		// get connection to database and create read months query
		try (Connection c = getConnection();
				Statement stmtGetMonthsForYear = c.createStatement()) {
			// execute query
			try (ResultSet rs = stmtGetMonthsForYear.executeQuery(SQLFactory.pullMonthsForYear(year))) {
				// iterate through ResultSet constructing months and adding them
				while (rs.next()) {
					int monthID = rs.getInt("monthID");
					Month month = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
					month.addTransactions(pullTransactionsForMonth(monthID));
					resultsMap.put(monthID, month);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// return results
		return resultsMap;

	}

	public void updateTransaction(int transactionID, Transaction t) {
		String s = "UPDATE transactions SET income=?, date=?, type=?, value=? WHERE transactionID=?";
		try (Connection c = getConnection();
				PreparedStatement stmtUpdateTransaction = c.prepareStatement(s)) {
			stmtUpdateTransaction.setBoolean(1, t.isIncome());
			stmtUpdateTransaction.setString(2, t.getDate().format(Constants.FORMAT_YYYYMMDD));
			stmtUpdateTransaction.setString(3, t.getType().name());
			stmtUpdateTransaction.setDouble(4, t.getAbsoluteValue());
			stmtUpdateTransaction.setInt(5, transactionID);
			stmtUpdateTransaction.executeUpdate();
		} catch (SQLException e) {
			System.err.println("SQL Connection failed while attempting to update transaction (transactionID=\"" + transactionID + "\")");
			e.printStackTrace();
		}
	}

	public HashMap<Integer, Transaction> pullTransactionIDs(int monthID, List<Transaction> list) {
		HashMap<Integer, Transaction> transactionIDs = new HashMap<Integer, Transaction>();
		try (Connection c = getConnection();
				PreparedStatement stmtGetTransactionForMonth = c.prepareStatement("SELECT * FROM transactions WHERE monthID=" + monthID)) {
			try (ResultSet rs = stmtGetTransactionForMonth.executeQuery()) {
				while (rs.next()) {
					transactionIDs.put(rs.getInt("transactionID"), generateTransaction(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return transactionIDs;
	}

	private Transaction generateTransaction(ResultSet rs) {
		Transaction toReturn = null;
		try {
			toReturn = new Transaction(rs.getString("name"),
					(rs.getInt("paid") == 0) ? false : true,
					LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMMDD),
					(rs.getInt("income") == 0) ? false : true,
					Type.valueOf(rs.getString("type")),
					rs.getDouble("value"));
		} catch (SQLException e) {
			System.err.println("Transaction generation from ResultSet failed!");
		}
		return toReturn;
	}

	//------------------------------\
	//	Months						|
	//------------------------------/

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
		try (Connection c = getConnection();
				PreparedStatement stmtSearch = c.prepareStatement(SQLFactory.searchMonth("date"));
				PreparedStatement stmtAdd = c.prepareStatement(SQLFactory.addMonth(), Statement.RETURN_GENERATED_KEYS)) {

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
	 * Removes a month from database {@code months} table, if it is already present (decided by querying database for {@code Months} with date - month and
	 * year - equal to {@code m}).<br>
	 * <br>
	 * 
	 * If a {@link model.domain.Month Month} is removed, all it's {@link model.domain.Transaction transactions} are also removed from the
	 * {@code transactions} table.
	 * 
	 * @param m
	 * @return successfully removed?
	 */
	public boolean removeMonth(Month m) {
		boolean removed = false;
		int monthID = SENTINEL_RETURN;

		System.out.println("Attempting to remove Month " + m.toString() + " to database.");

		// create connection and create a statement
		try (Connection c = getConnection();
				PreparedStatement stmtSearch = c.prepareStatement(SQLFactory.searchMonth("date"))) {
			stmtSearch.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
			// search for month
			try (ResultSet rs = stmtSearch.executeQuery()) {
				if (rs.next()) {
					monthID = rs.getInt("monthID");
					boolean dup = false;
					// ensure there are no duplicates of month in database
					while (rs.next()) {
						dup = true;
					}

					// if there are no duplicates, then remove month with primary key equal to primaryKey int.
					if (!dup) {
						try (PreparedStatement stmtRemoveMonth = c.prepareStatement(SQLFactory.removeMonth())) {
							stmtRemoveMonth.setInt(1, monthID);
							stmtRemoveMonth.executeUpdate();
							removed = true;
							System.out.println("Month " + m.toString() + " successfully removed!");
							removeTransactionsForMonth(monthID);
						}
					} else {
						// duplicates found, remove aborted
						System.out.println("Duplicates found for month " + m.toString() + ", remove operation aborted!");
					}
				} else {
					System.out.println("No month found for removal.");
				}
			}
			// capture its primary key
			// DELETE FROM using primary key
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return removed;
	}

	/**
	 * Retrieves all {@code Month} data from the {@code months} table in database.
	 * 
	 * @return a {@code List} of {@link model.domain.Month Month} objects.
	 */
	public List<Month> pullMonths() {
		// create empty results list
		List<Month> results = new LinkedList<Month>();

		// get connection to database and create read months query
		try (Connection c = getConnection();
				Statement stmtGetAllMonths = c.createStatement()) {

			// execute query
			try (ResultSet rs = stmtGetAllMonths.executeQuery(SQLFactory.pullMonths())) {
				// iterate through ResultSet constructing months and adding them
				while (rs.next()) {
					Month m = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
					m.addTransactions(pullTransactionsForMonth(rs.getInt("monthID")));
					results.add(m);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// return results
		return results;
	}

	public List<Month> pullMonthsForYear(int year) {
		// create empty results list
		List<Month> results = new LinkedList<Month>();

		// get connection to database and create read months query
		try (Connection c = getConnection();
				Statement stmtGetAllMonths = c.createStatement()) {

			// execute query
			try (ResultSet rs = stmtGetAllMonths.executeQuery(SQLFactory.pullMonthsForYear(year))) {
				// iterate through ResultSet constructing months and adding them
				while (rs.next()) {
					Month m = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
					m.addTransactions(pullTransactionsForMonth(rs.getInt("monthID")));
					results.add(m);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// return results
		return results;
	}

	/**
	 * Query the database to get the {@code monthID} column value for that month.
	 * 
	 * @param m
	 * @return the monthID for the month specified in parameter {@code m}.
	 */
	public int pullMonthID(Month m) {
		int monthID = SENTINEL_RETURN;
		try (Connection c = getConnection();
				PreparedStatement stmtGetMonthID = c.prepareStatement(SQLFactory.getMonthID())) {
			stmtGetMonthID.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
			try (ResultSet rs = stmtGetMonthID.executeQuery()) {
				if (rs.next()) {
					monthID = rs.getInt("monthID");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return monthID;
	}

	//	public void updateMonth(int monthID) {
	//		removeMonth(m);
	//		addMonth(m);
	//	}

	//------------------------------\
	//	Transactions				|
	//------------------------------/

	/**
	 * Retrieves all {@code Transaction} data from the {@code transactions} table in database, for months with {@code monthID} column equal to parameter
	 * {@code monthID}.
	 * 
	 * @param monthID the monthID to search for.
	 * @return a {@code List} of {@link model.domain.Transaction Transaction} objects.
	 */
	public List<Transaction> pullTransactionsForMonth(int monthID) {
		List<Transaction> trans = new LinkedList<Transaction>();
		try (Connection c = getConnection();
				PreparedStatement stmtGetTransactionsForMonth = c.prepareStatement(SQLFactory.searchTransactions("monthID"))) {
			stmtGetTransactionsForMonth.setInt(1, monthID);
			try (ResultSet rs = stmtGetTransactionsForMonth.executeQuery()) {
				while (rs.next()) {
					Transaction t = new Transaction(
							rs.getString("name"),
							(rs.getInt("paid") == 1) ? true : false,
							LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMMDD),
							(rs.getInt("income") == 1) ? true : false,
							Type.valueOf(rs.getString("type")),
							rs.getDouble("value"));
					trans.add(t);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trans;
	}

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
			List<Transaction> transactionsForMonth = pullTransactionsForMonth(monthID);
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
				PreparedStatement stmtGetTransactions = c.prepareStatement(SQLFactory.searchTransactions("monthID"))) {
			stmtGetTransactions.setInt(1, monthID);
			List<Transaction> transactions = pullTransactionsForMonth(monthID);
			if (transactions.contains(t)) {
				System.out.println("This transaction already exists in database for this month");
			} else {
				try (PreparedStatement stmtAddTrans = c.prepareStatement(SQLFactory.addTransaction(), Statement.RETURN_GENERATED_KEYS)) {
					//"INSERT INTO transactions (monthID, name, paid, income, date, type, value) VALUES (?,?,?,?,?);";

					stmtAddTrans.setInt(1, monthID);
					stmtAddTrans.setString(2, t.getName());
					stmtAddTrans.setInt(3, (t.isPaid()) ? 1 : 0);
					stmtAddTrans.setInt(4, ((t.isIncome()) ? 1 : 0));
					stmtAddTrans.setString(5, t.getDate().format(Constants.FORMAT_YYYYMMDD));
					stmtAddTrans.setString(6, t.getType().name());
					stmtAddTrans.setDouble(7, t.getAbsoluteValue());
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
	 * Removes all {@code transaction} table rows that have {@code monthID} column equal to parameter {@code monthID}.
	 * 
	 * @param monthID
	 */
	public void removeTransactionsForMonth(int monthID) {
		try (Connection c = getConnection();
				Statement removeTransForMonth = c.createStatement()) {
			removeTransForMonth.executeUpdate(SQLFactory.removeTransactionsForMonth(monthID));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeTransaction(Transaction t) {
		//SELECT * FROM transactions WHERE monthID=? AND income=? AND date=? AND type=? AND value=?
	}

	//------------------------------\
	//	Misc						|
	//------------------------------/

	/**
	 * Get all the years that have Months in the database
	 * 
	 * @return years
	 */
	public List<Integer> getYears() {
		List<Integer> years = new LinkedList<Integer>();
		try (Connection c = getConnection()) {
			List<Month> months = pullMonths();
			for (Month month : months) {
				int year = month.getDate().getYear();
				if (!years.contains(year)) {
					years.add(year);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Collections.sort(years);
		return years;
	}

}
