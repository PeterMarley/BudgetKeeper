package model.db;

import model.db.Constants.Files;
import model.db.Constants.Tables;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;

import static model.domain.Utility.nullCheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

import controller.Controller;

/**
 * This class provides methods needed to interact with the BudgetKeeper programs database ({@link}) <br>
 * <hr>
 * <h1>SCHEMA</h1>
 * 
 * <pre>
CREATE TABLE transactions (
        transactionID INTEGER NOT NULL,
        monthID INTEGER NOT NULL,
        name TEXT,
        paid INTEGER,
        income INTEGER,
        date TEXT,
        type TEXT,
        value REAL,
        PRIMARY KEY(transactionID),
        FOREIGN KEY(monthID) REFERENCES months (monthID)
);
CREATE TABLE months (
        monthID INTEGER NOT NULL,
        date TEXT,
        PRIMARY KEY(monthID)
);
 * </pre>
 * <hr>
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 */
public class DatabaseAccessObject {

	/**
	 * Default return sentinel value for queries.
	 */
	public static final int SENTINEL_RETURN = -1;
	public static final int START_SEED = 1;

	/**
	 * 0 "name"<br>
	 * 1 "paid"<br>
	 * 2 "date"<br>
	 * 3 "income"<br>
	 * 4 "type"<br>
	 * 5 "value"<br>
	 */
	private static final String[] COLUMNS_TRANSACTIONS = new String[] { "name", "paid", "date", "income", "type", "value", "transactionID" };

	private int currentSeed;

	public DatabaseAccessObject() {
		setSeed(selectSeed());
	}

	//**********************************\
	//									|
	//	API								|
	//									|
	//**********************************/

	/**
	 * Retrieves all {@link model.domain.Month Month} data from the {@code months} table in database, and adds all relevant
	 * {@link model.domain.Transaction Transactions} to each {@code Month} from {@code transactions} table.
	 * 
	 * @return list of all Months (including their transactions).
	 */
	public List<Month> loadData() {
		return selectMonths();
	}

	/**
	 * Save a Month to database.
	 * <hr>
	 * <b>1. </b>The database is queried for this monthsID, if the Month is not found, a new Month is created and written to database.
	 * 
	 * @param toSave
	 * @return
	 */
	public boolean saveData(Month toSave) {
		System.out.println("*********************************");
		System.out.println("saving data");
		System.out.println("*********************************");

		int currentSeed = Controller.getSeed(false);

		// Discern if this a new Month
		int monthID = 0;
		boolean newMonth = true;
		try {
			monthID = selectMonthID(toSave);
			newMonth = false;
		} catch (SQLException e1) {
			// this is a new Month. newMonth = true.
		}

		try {

			if (newMonth) {	// add month if new month and get monthID
				monthID = insertMonth(toSave);
			}

			HashMap<Transaction, Integer> tMapFromDB = queryTransactionsMap(monthID);
			Set<Integer> transactionIDsFromDB = new HashSet<Integer>();
			Set<Transaction> transactionsFromDB = new HashSet<Transaction>();
			tMapFromDB.forEach((t, i) -> {
				transactionIDsFromDB.add(i);
				transactionsFromDB.add(t);
			});
			List<Transaction> tFromObj = new LinkedList<Transaction>(toSave.getTransactions());

			List<Transaction> toAdd = new LinkedList<Transaction>();
			HashMap<Integer, Transaction> toUpdate = new HashMap<Integer, Transaction>();
			HashMap<Transaction, Integer> toDelete = new HashMap<Transaction, Integer>();
			List<Transaction> filtered = new LinkedList<Transaction>();

			// Sorting transactions into [to be added], [to be updated], and [to be deleted]
			List<Transaction> removeFromtObj = new LinkedList<Transaction>();
			for (int i = 0; i < tFromObj.size(); i++) {

				Transaction t = tFromObj.get(i);

				int originalTID = t.getTransactionID();
				boolean isUnsaved = t.isUnsaved();

				// sort Transactions into collections for processing
				if (isUnsaved && originalTID != Transaction.NEW_ID && !t.isDelete()) {			// db has tid && isUnsaved
					toUpdate.put(originalTID, t);
				} else if (originalTID == Transaction.NEW_ID) { 								// new transaction
					t.setTransactionID(Controller.getSeed(true));
					toAdd.add(t);
				} else if (!transactionIDsFromDB.contains(originalTID) && isUnsaved || t.isDelete()) { // old logic needs readded i think
					toDelete.put(t, originalTID);
				}
				removeFromtObj.add(t);

			}
			transactionsFromDB.removeAll(removeFromtObj);

			// Database Operations
			try (Connection con = getConnection()) {

				// create new transactions
				for (Transaction tToAdd : toAdd) {
					tToAdd.setUnsaved(false);
					createTransaction(con, monthID, tToAdd);
				}

				// update edited transactions
				for (Integer TID : toUpdate.keySet()) {
					Transaction t = toUpdate.get(TID);
					updateTransaction(con, monthID, t, TID);
					t.setUnsaved(false);
				}

				// delete removed transaction
//				tMapFromDB.forEach((t, i) -> {
//					if (!tFromObj.contains(t))
//						toDelete.put(t, i);
//				});
				transactionsFromDB.forEach(t -> toDelete.put(t, t.getTransactionID()));
				for (Transaction t : toDelete.keySet()) {
					deleteTransaction(con, toDelete.get(t));
				}
			}
			if (currentSeed != Controller.getSeed(false)) {
				updateSeed();
			}

		} catch (

		SQLException e) {
			e.printStackTrace();
		}

		System.out.println("*********************************");
		System.out.println("finished saving data");
		System.out.println("*********************************");
		return false;

	}

	//**********************************\
	//									|
	//	CREATE (INSERT)					|
	//									|
	//**********************************/

	private void createTransaction(Connection c, int monthID, Transaction t) {
		final String sql = "INSERT INTO transactions " +
				"(transactionID, monthID, name, paid, income, date, type, value) " +
				"VALUES " +
				"(?,?,?,?,?,?,?,?);";

		try (PreparedStatement stmtCreateTrans = c.prepareStatement(sql)) {
			stmtCreateTrans.setInt(1, t.getTransactionID());
			stmtCreateTrans.setInt(2, monthID);
			stmtCreateTrans.setString(3, t.getName());
			stmtCreateTrans.setInt(4, (t.isPaid()) ? 1 : 0);
			stmtCreateTrans.setInt(5, (t.isIncome()) ? 1 : 0);
			stmtCreateTrans.setString(6, t.getDate().format(Constants.FORMAT_YYYYMMDD));
			stmtCreateTrans.setString(7, t.getType().name());
			stmtCreateTrans.setDouble(8, t.getAbsoluteValue());
			stmtCreateTrans.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private int insertMonth(Month toSave) throws SQLException {
		final String sql = "INSERT INTO months (date) VALUES (?);";
		int keyVal = SENTINEL_RETURN;
		try (Connection c = getConnection();
				PreparedStatement stmtAddMonth = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmtAddMonth.setString(1, toSave.getDate().format(Constants.FORMAT_YYYYMM));
			stmtAddMonth.executeUpdate();
			try (ResultSet key = stmtAddMonth.getGeneratedKeys()) {
				if (key.next()) {
					keyVal = key.getInt(1);
				}
			}
		}
		return keyVal;
	}

	//**********************************\
	//									|
	//	READ (SELECT)					|
	//									|
	//**********************************/

	private int selectSeed() {
		int seed = START_SEED; // default seed if no data
		try (Connection c = getConnection();
				PreparedStatement stmtGetSeed = c.prepareStatement("SELECT * FROM ids;");
				ResultSet rs = stmtGetSeed.executeQuery()) {
			if (rs.next()) {
				seed = rs.getInt(1);
			}
			System.out.println(seed);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return seed;
	}

	/**
	 * Retrieves all {@code Month} objects from the {@code months} and {@code transaction} tables.
	 * 
	 * @return a {@code List} of {@link model.domain.Month Month} objects.
	 */
	protected List<Month> selectMonths() {

		final String sql = "SELECT * FROM months;";

		List<Month> resultsList = new LinkedList<Month>();

		// get connection to database and create read months query
		try (Connection c = getConnection();
				Statement stmtGetAllMonths = c.createStatement()) {

			// execute query
			try (ResultSet rs = stmtGetAllMonths.executeQuery(sql)) {
				// iterate through ResultSet constructing months and adding them
				while (rs.next()) {
					Month month = parseMonth(rs);
					month.addTransactions(selectTransactions(rs.getInt("monthID")));
					resultsList.add(month);
				}
			}

		} catch (SQLException e) {
		}

		// return results
		return resultsList;
	}

	/**
	 * Retrieves all {@code Transaction} data from the {@code transactions} table in database, for months with {@code monthID} column equal to
	 * parameter {@code monthID}.
	 * 
	 * @param monthID the monthID to search for.
	 * @return a {@code List} of {@link model.domain.Transaction Transaction} objects.
	 */
	protected SortedSet<Transaction> selectTransactions(int monthID) {

		final String sql = "SELECT * FROM transactions WHERE monthID=?;";

		SortedSet<Transaction> trans = new TreeSet<Transaction>();
		try (Connection c = getConnection();
				PreparedStatement stmtGetTransactionsForMonth = c.prepareStatement(sql)) {
			stmtGetTransactionsForMonth.setInt(1, monthID);
			try (ResultSet rs = stmtGetTransactionsForMonth.executeQuery()) {
				while (rs.next()) {
					Transaction t = parseTransaction(rs);
					trans.add(t);
				}

			} catch (SQLException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trans;
	}

	private int selectMonthID(Month toSave) throws SQLException {

		final String sql = "SELECT monthID FROM months WHERE date=?;";

		Integer monthID = null;

		try (Connection c = getConnection();
				PreparedStatement state = c.prepareStatement(sql)) {
			state.setString(1, toSave.getDate().format(Constants.FORMAT_YYYYMM));
			try (ResultSet rs = state.executeQuery()) {
				monthID = rs.getInt("monthID");
				return monthID;
			}
		}
	}

	private HashMap<Transaction, Integer> queryTransactionsMap(int monthID) {

		final String sql = "SELECT * FROM transactions WHERE monthID=?;";

		HashMap<Transaction, Integer> map = new HashMap<Transaction, Integer>();
		try (Connection c = getConnection();
				PreparedStatement statement = c.prepareStatement(sql)) {
			statement.setInt(1, monthID);
			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					map.put(parseTransaction(rs), rs.getInt("transactionID"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	//**********************************\
	//									|
	//	UPDATE							|
	//									|
	//**********************************/

	private void updateSeed() {
		try (Connection con = getConnection();
				Statement stmtDelete = con.createStatement();
				PreparedStatement stmtUpdate = con.prepareStatement("INSERT INTO ids (seed) VALUES (?)")) {
			stmtDelete.executeUpdate("DELETE FROM ids;");
			stmtUpdate.setInt(1, getSeed(true));
			stmtUpdate.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateTransaction(Connection c, int monthID, Transaction t, Integer oldTID) {

		final String sql = "UPDATE transactions SET name=?, transactionID=?, date=?, type=?, value=?, income=?, paid=? WHERE monthID=? AND transactionID=?;";

		try (PreparedStatement stmtUpdtTrans = c.prepareStatement(sql)) {
			stmtUpdtTrans.setString(1, t.getName());
			stmtUpdtTrans.setInt(2, t.getTransactionID());
			stmtUpdtTrans.setString(3, t.getDate().format(Constants.FORMAT_YYYYMMDD));
			stmtUpdtTrans.setString(4, t.getType().name());
			stmtUpdtTrans.setDouble(5, t.getAbsoluteValue());
			stmtUpdtTrans.setInt(6, t.isIncome() ? 1 : 0);
			stmtUpdtTrans.setInt(7, t.isPaid() ? 1 : 0);

			stmtUpdtTrans.setInt(8, monthID);
			stmtUpdtTrans.setInt(9, oldTID);

			stmtUpdtTrans.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//**********************************\
	//									|
	//	DELETE							|
	//									|
	//**********************************/

	private void deleteTransaction(Connection c, Integer tID) {

		final String sql = "DELETE FROM transactions WHERE transactionID=?;";

		try (PreparedStatement stmtDelTrans = c.prepareStatement(sql)) {
			stmtDelTrans.setInt(1, tID);
			stmtDelTrans.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void deleteMonth(Connection c, Integer mID) {
		final String sql = "DELETE FROM months WHERE monthID=?;";

		try (PreparedStatement semtDelMonth = c.prepareStatement(sql)) {
			semtDelMonth.setInt(1, mID);
			semtDelMonth.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteMonth(Month m) {
		try {
			int mID = selectMonthID(m);
			try (Connection c = getConnection()) {
				deleteMonth(c, mID);
			}
		} catch (SQLException e) {
			System.err.println("Failed to delete Month from database:");
			e.printStackTrace();
		}
	}

	public void deleteAll() {
		try (Connection c = getConnection();
				Statement stmtDeleteAll = c.createStatement()) {
			stmtDeleteAll.executeUpdate("DELETE FROM transactions;");
			stmtDeleteAll.executeUpdate("DELETE FROM months;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//**********************************\
	//									|
	//	Getters							|
	//									|
	//**********************************/

	public int getSeed(boolean increment) {
		if (increment) {
			currentSeed++;
		}
		return currentSeed;
	}

	//**********************************\
	//									|
	//	Setters							|
	//									|
	//**********************************/

	public void setSeed(int newSeed) {
		currentSeed = newSeed;
	}

	//**********************************\
	//									|
	//	Parse Objects					|
	//									|
	//**********************************/

	/**
	 * Generate a transaction objecy from the currently selected row in a results set.
	 * 
	 * @param rs a ResultSet
	 * @return a Transaction object, or null if generation failed.
	 */
	private static Transaction parseTransaction(ResultSet rs) {
		Transaction t = null;
		try {
			t = new Transaction(rs.getString(COLUMNS_TRANSACTIONS[0]),
					(rs.getInt(COLUMNS_TRANSACTIONS[1]) == 1) ? true : false,
					LocalDate.parse(rs.getString(COLUMNS_TRANSACTIONS[2]), Constants.FORMAT_YYYYMMDD),
					(rs.getInt(COLUMNS_TRANSACTIONS[3]) == 1) ? true : false,
					Type.valueOf(rs.getString(COLUMNS_TRANSACTIONS[4])),
					rs.getDouble(COLUMNS_TRANSACTIONS[5]),
					rs.getInt(COLUMNS_TRANSACTIONS[6]));
		} catch (SQLException | IllegalArgumentException | InputMismatchException e) {
			System.err.println("Transaction generation from ResultSet failed!");
			e.printStackTrace();
		}
		return t;
	}

	private static Month parseMonth(ResultSet rs) throws SQLException {
		return new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
	}

	//**********************************\
	//									|
	//	Utility							|
	//									|
	//**********************************/

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

//private HashMap<Transaction, Integer> mapTransactionIDs(int monthID, List<Transaction> list) {
//HashMap<Transaction, Integer> transactionIDs = new HashMap<Transaction, Integer>();
//final String SQL = "SELECT * FROM transactions WHERE monthID=?";
//try (Connection c = getConnection();
//		PreparedStatement stmtGetTransactionForMonth = c.prepareStatement(SQL)) {
//	stmtGetTransactionForMonth.setInt(1, monthID);
//	try (ResultSet result = stmtGetTransactionForMonth.executeQuery()) {
//		while (result.next()) {
//			Transaction generated = generateTransaction(result);
//			if (generated != null && list.contains(generated)) {
//				transactionIDs.put(generated, result.getInt("transactionID"));
//			}
//		}
//	}
//} catch (SQLException e) {
//	e.printStackTrace();
//}
//
//return transactionIDs;
//}

//public void updateMonth(Month m, int monthID, HashMap<Transaction, Integer> mapOftIDs) {
//
//	String updtMonth = "UPDATE months SET date=? WHERE monthID=?;";
//	String updtTransaction = "UPDATE transactions SET income=?, date=?, type=?, value=? WHERE monthID=? AND transactionID=?;";
//	try (Connection c = getConnection();
//			PreparedStatement stmtUpdtMonth = c.prepareStatement(updtMonth);
//			PreparedStatement stmtUpdtTransaction = c.prepareStatement(updtTransaction)) {
//		stmtUpdtMonth.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
//		stmtUpdtMonth.setInt(2, monthID);
//		stmtUpdtMonth.executeUpdate();
//		HashMap<Transaction, Integer> db = mapTransactionIDs(monthID, new ArrayList<Transaction>(mapOftIDs.keySet()));
//		for (Map.Entry<Transaction, Integer> entry : mapOftIDs.entrySet()) {
//			Transaction t = entry.getKey();
//			Integer tID = entry.getValue();
//			if (!db.containsKey(t)) {
//				stmtUpdtTransaction.setInt(1, t.isIncome() ? 1 : 0);
//				stmtUpdtTransaction.setString(2, t.getDate().format(Constants.FORMAT_YYYYMMDD));
//				stmtUpdtTransaction.setString(3, t.getType().name());
//				stmtUpdtTransaction.setDouble(4, t.getAbsoluteValue());
//				stmtUpdtTransaction.setInt(5, monthID);
//				stmtUpdtTransaction.setInt(6, tID);
//				stmtUpdtTransaction.executeUpdate();
//			}
//		}
//
//		/*
//		 * 	/**
//		* <pre>
//		* CREATE TABLE transactions (
//		* 	transactionID INTEGER NOT NULL,
//		* 	monthID INTEGER NOT NULL,
//		* 	income INTEGER,
//		* 	date TEXT,
//		* 	type TEXT,
//		* 	value REAL,
//		* 	PRIMARY KEY(transactionID),
//		* 	FOREIGN KEY(monthID) REFERENCES months (monthID)
//		* );
//		*
//		* CREATE TABLE months (
//		* 	monthID INTEGER NOT NULL,
//		* 	date TEXT,
//		* 	PRIMARY KEY(monthID)
//		* );
//		* </pre>
//		*/
//
//	} catch (SQLException e) {
//		e.printStackTrace();
//	}
//}

///**
//* Retrieve all {@code Month}s from the database, and return as a {@code HashMap<Integer,Month>}, with keys being the monthID and values being the
//* {@code Month}s themselves.
//* 
//* @return A HashMap of K:V (Month:monthID)
//*/
//public HashMap<Month, Integer> queryMonths() {
//	HashMap<Month, Integer> resultsMap = new HashMap<Month, Integer>();
//
//	// get connection to database and create read months query
//	try (Connection c = getConnection();
//			Statement stmtGetAllMonths = c.createStatement()) {
//
//		// execute query
//		try (ResultSet rs = stmtGetAllMonths.executeQuery(SQLFactory.pullMonths())) {
//			// iterate through ResultSet constructing months and adding them
//			while (rs.next()) {
//				int monthID = rs.getInt("monthID");
//				Month month = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
//				//month.addTransactions(pullTransactionsForMonth(monthID));
//				resultsMap.put(month, monthID);
//			}
//		}
//
//	} catch (SQLException e) {
//		e.printStackTrace();
//	}
//	// return results
//	return resultsMap;
//
//}

//public List<Month> pullMonthsForYear(int year) {
//// create empty results list
//List<Month> results = new LinkedList<Month>();
//
//// get connection to database and create read months query
//try (Connection c = getConnection();
//		Statement stmtGetAllMonths = c.createStatement()) {
//	// execute query
//	try (ResultSet rs = stmtGetAllMonths.executeQuery(SQLFactory.pullMonthsForYear(year))) {
//		// iterate through ResultSet constructing months and adding them
//		while (rs.next()) {
//			Month m = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
//			m.addTransactions(pullTransactionsForMonth(rs.getInt("monthID")));
//			results.add(m);
//		}
//	}
//
//} catch (SQLException e) {
//	e.printStackTrace();
//}
//// return results
//return results;
//}

///**
//* Query the database to get the {@code monthID} column value for that month.
//* 
//* @param m
//* @return the monthID for the month specified in parameter {@code m}.
//*/
//public int pullMonthID(Month m) {
//	int monthID = SENTINEL_RETURN;
//	try (Connection c = getConnection();
//			PreparedStatement stmtGetMonthID = c.prepareStatement(SQLFactory.getMonthID())) {
//		stmtGetMonthID.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
//		try (ResultSet rs = stmtGetMonthID.executeQuery()) {
//			if (rs.next()) {
//				monthID = rs.getInt("monthID");
//			}
//		}
//	} catch (SQLException e) {
//		e.printStackTrace();
//	}
//	return monthID;
//}

//	/**
//	 * Returns a HashMap. Keys are Transaction objects and values are an Integer[2] containing the transactionID at index 0, and monthID at index 1.
//	 * 
//	 * @return a HashMap<Transaction,Integer[]>Keys are Transaction objects and values are an Integer[2] containing the transactionID at index 0, and
//	 *         monthID at index 1.
//	 */
//	public HashMap<Transaction, Integer[]> queryTransactions() {
//		HashMap<Transaction, Integer[]> resultsMap = new HashMap<Transaction, Integer[]>();
//		String SQL = "SELECT * FROM transactions;";
//		try (Connection c = getConnection();
//				Statement query = c.createStatement();
//				ResultSet results = query.executeQuery(SQL)) {
//			while (results.next()) {
//				Transaction t = generateTransaction(results);
//				int tID = results.getInt("transactionID");
//				int mID = results.getInt("monthID");
//				resultsMap.put(t, new Integer[] { tID, mID });
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return resultsMap;
//	}

//	public HashMap<Integer, Month> queryMonthsForYear(int year) {
//		HashMap<Integer, Month> resultsMap = new HashMap<Integer, Month>();
//
//		// get connection to database and create read months query
//		try (Connection c = getConnection();
//				Statement stmtGetMonthsForYear = c.createStatement()) {
//			try (ResultSet rs = stmtGetMonthsForYear.executeQuery(SQLFactory.pullMonthsForYear(year))) {
//				// iterate through ResultSet constructing months and adding them
//				while (rs.next()) {
//					int monthID = rs.getInt(1);
//					Month month = new Month(LocalDate.parse(rs.getString(2), Constants.FORMAT_YYYYMM));
//					month.addTransactions(pullTransactionsForMonth(monthID));
//					resultsMap.put(monthID, month);
//				}
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		// return results
//		return resultsMap;
//	}

//	public void updateTransaction(int transactionID, Transaction t) {
//		String s = "UPDATE transactions SET income=?, date=?, type=?, value=? WHERE transactionID=?";
//		try (Connection c = getConnection();
//				PreparedStatement stmtUpdateTransaction = c.prepareStatement(s)) {
//			stmtUpdateTransaction.setBoolean(1, t.isIncome());
//			stmtUpdateTransaction.setString(2, t.getDate().format(Constants.FORMAT_YYYYMMDD));
//			stmtUpdateTransaction.setString(3, t.getType().name());
//			stmtUpdateTransaction.setDouble(4, t.getAbsoluteValue());
//			stmtUpdateTransaction.setInt(5, transactionID);
//			stmtUpdateTransaction.executeUpdate();
//		} catch (SQLException e) {
//			System.err.println("SQL Connection failed while attempting to update transaction (transactionID=\"" + transactionID + "\")");
//			e.printStackTrace();
//		}
//	}

//	/**
//	 * Removes all {@code transaction} table rows that have {@code monthID} column equal to parameter {@code monthID}.
//	 * 
//	 * @param monthID
//	 */
//	public void removeTransactionsForMonth(int monthID) {
//		try (Connection c = getConnection();
//				Statement removeTransForMonth = c.createStatement()) {
//			removeTransForMonth.executeUpdate(SQLFactory.removeTransactionsForMonth(monthID));
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void removeTransaction(Transaction t) {
//		//SELECT * FROM transactions WHERE monthID=? AND income=? AND date=? AND type=? AND value=?
//	}

//------------------------------\
//	Misc						|
//------------------------------/

/**
 * Get all the years that have Months in the database
 * 
 * @return years
 */
//	public List<Integer> getYears() {
//		List<Integer> years = new LinkedList<Integer>();
//		try (Connection c = getConnection()) {
//			List<Month> months = pullMonths();
//			for (Month month : months) {
//				int year = month.getDate().getYear();
//				if (!years.contains(year)) {
//					years.add(year);
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		Collections.sort(years);
//		return years;
//	}

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
//	public boolean removeMonth(Month m) {
//		boolean removed = false;
//		int monthID = SENTINEL_RETURN;
//
//		System.out.println("Attempting to remove Month " + m.toString() + " to database.");
//
//		// create connection and create a statement
//		try (Connection c = getConnection();
//				PreparedStatement stmtSearch = c.prepareStatement(SQLFactory.searchMonth("date"))) {
//			stmtSearch.setString(1, m.getDate().format(Constants.FORMAT_YYYYMM));
//			// search for month
//			try (ResultSet rs = stmtSearch.executeQuery()) {
//				if (rs.next()) {
//					monthID = rs.getInt("monthID");
//					boolean dup = false;
//					// ensure there are no duplicates of month in database
//					while (rs.next()) {
//						dup = true;
//					}
//
//					// if there are no duplicates, then remove month with primary key equal to primaryKey int.
//					if (!dup) {
//						try (PreparedStatement stmtRemoveMonth = c.prepareStatement(SQLFactory.removeMonth())) {
//							stmtRemoveMonth.setInt(1, monthID);
//							stmtRemoveMonth.executeUpdate();
//							removed = true;
//							System.out.println("Month " + m.toString() + " successfully removed!");
//							removeTransactionsForMonth(monthID);
//						}
//					} else {
//						// duplicates found, remove aborted
//						System.out.println("Duplicates found for month " + m.toString() + ", remove operation aborted!");
//					}
//				} else {
//					System.out.println("No month found for removal.");
//				}
//			}
//			// capture its primary key
//			// DELETE FROM using primary key
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return removed;
//	}

/**
 * Retrieves all {@code Month} data from the {@code months} table in database.
 * 
 * @return a {@code List} of {@link model.domain.Month Month} objects.
 */
//	public List<Month> pullMonths() {
//		// create empty results list
//		List<Month> results = new LinkedList<Month>();
//
//		// get connection to database and create read months query
//		try (Connection c = getConnection();
//				Statement stmtGetAllMonths = c.createStatement()) {
//
//			// execute query
//			try (ResultSet rs = stmtGetAllMonths.executeQuery(SQLFactory.pullMonths())) {
//				// iterate through ResultSet constructing months and adding them
//				while (rs.next()) {
//					Month m = new Month(LocalDate.parse(rs.getString("date"), Constants.FORMAT_YYYYMM));
//					m.addTransactions(pullTransactionsForMonth(rs.getInt("monthID")));
//					results.add(m);
//				}
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		// return results
//		return results;
//	}
