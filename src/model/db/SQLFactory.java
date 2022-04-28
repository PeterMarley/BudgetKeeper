package model.db;

import static model.domain.Utility.nullCheck;

import model.db.Constants.Tables;
import model.domain.Month;
import model.domain.Transaction;

/**
 * This factory class is used to generate SQL code Strings for use in the BudgetKeeper programs {@link model.db.DatabaseAccessObject DAO} Database is
 * found at {@link model.db.Constants.Files#DATABASE this enum}.
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class SQLFactory {

	/**
	 * Generate DROP TABLE SQL statements for a {@link Tables} enum.
	 * 
	 * @param table
	 * @return an SQL code String that will drop a table specified by {@code table} parameter.
	 */
	public static String dropTable(Tables table) throws IllegalArgumentException {
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
	 * @return an SQL code String that will create a table specified by {@code table} parameter.
	 */
	public static String createTable(Tables table) throws IllegalArgumentException {
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

	private static final String ADD_MONTH_SQL = "INSERT INTO months (date) VALUES (?);";
	private static final String REMOVE_MONTH_SQL = "DELETE FROM months WHERE monthID=?;";
	private static final String SEARCH_MONTH = "SELECT monthID FROM months WHERE %s=?;";
	private static final String PULL_MONTHS = "SELECT * FROM months;";
	private static final String PULL_MONTHS_FOR_YEAR = "SELECT * FROM months WHERE date LIKE '%d/%%';";
	private static final String SEARCH_TRANSACTIONS = "SELECT * FROM transactions WHERE %s=?;";
	private static final String ADD_TRANSACTION = "INSERT INTO transactions (monthID, name, paid, income, date, type, value) VALUES (?,?,?,?,?,?,?);";
	private static final String REMOVE_TRANSACTIONS_FOR_MONTH = "DELETE FROM transactions WHERE monthID=%d;";
	private static final String GET_MONTH_ID = "SELECT monthID FROM months WHERE date=?;";
	private static final String REMOVE_TRANSACTION = "DELETE FROM transactions WHERE monthID=? AND name=%s AND paid=%d AND income=%d AND date='%s' AND type='%s' AND value=%.2f;";
	private static final String GET_YEARS = "SELECT * FROM months ORDER BY date;";

	/**
	 * Generate a PreparedStatement String for an INSERT INTO statement for inserting a {@link model.domain.Month Month} object into database
	 * {@code months} table.
	 * 
	 * @return an SQL code String (for use in a PreparedStatement) that will add a {@code Month} into the database. This SQL code contains '?'
	 *         character(s)
	 *         to avoid SQL injection techniques.<br>
	 *         <br>
	 *         {@value #ADD_MONTH_SQL}
	 */
	public static String addMonth() {
		return ADD_MONTH_SQL;
	}

	/**
	 * Generate SQL code String for deleting a row (representing a {@link model.domain.Month Month} object), but the {@code monthID}.
	 * 
	 * @return an SQL code String (for use in a PreparedStatement) that will remove a {@code Month} from the database. This SQL code contains '?'
	 *         character(s) to avoid SQL injection techniques.<br>
	 *         <br>
	 *         {@value #REMOVE_MONTH_SQL}
	 */
	public static String removeMonth() {
		return REMOVE_MONTH_SQL;
	}

	/**
	 * Generate SQL code String for searching {@code months} table specific {@code column} value.
	 * 
	 * @param columnName the column name
	 * @return an SQL code String (for use in a PreparedStatement) that will remove a {@code Month} from the database, searching by {@code column}. This
	 *         SQL code contains '?' character(s) to avoid SQL injection techniques.
	 *         <br>
	 *         <br>
	 * 
	 *         {@value #SEARCH_MONTH} placeholder is replaced by {@code columnName} value.
	 */
	public static String searchMonth(String columnName) {
		return String.format(SEARCH_MONTH, columnName);
	}

	/**
	 * Generate SQL code String for searching for and retrieving all {@code months} table data.
	 * 
	 * @return an SQL code String (for use in a Statement) that will retrieve all {@code Month}'s from the database.<br>
	 *         <br>
	 *         {@value #PULL_MONTHS}
	 */
	public static String pullMonths() {
		return PULL_MONTHS;
	}

	/**
	 * Generate SQL code String for searching for and retrieving {@code months} table data for year {@code year}.
	 * 
	 * @return an SQL code String (for use in a Statement) that will retrieve all {@code Month}'s for year {@code year} from the database.<br>
	 *         <br>
	 *         {@value #PULL_MONTHS_FOR_YEAR} placeholder is replaced by {@code year} value.
	 */
	public static String pullMonthsForYear(int year) {
		return String.format(PULL_MONTHS_FOR_YEAR, year);
	}

	/**
	 * Generate SQL code String for searching {@code transactions} table specific {@code column} value.
	 * 
	 * @param columnName the column name
	 * @return an SQL code String (for use in a PreparedStatement) that will remove a {@link model.domain.Month Month} from the database, searching by
	 *         {@code column}. This
	 *         SQL code contains '?' character(s) to avoid SQL injection techniques.<br>
	 *         <br>
	 * 
	 *         {@value #SEARCH_TRANSACTIONS} placeholder is replaced by {@code columnName} value.
	 */
	public static String searchTransactions(String columnName) {
		return String.format(SEARCH_TRANSACTIONS, columnName);
	}

	/**
	 * Generate SQL code String for adding a {@link model.domain.Transaction Transaction} to {@code transactions} table.
	 * 
	 * @return an SQL code String (for use in a Statement) that will add a {@link model.domain.Transaction Transaction} to the database. This
	 *         SQL code contains '?' character(s) to avoid SQL injection techniques.<br>
	 *         <br>
	 * 
	 *         {@value #ADD_TRANSACTION}.
	 */
	public static String addTransaction() {
		return ADD_TRANSACTION;
	}

	/**
	 * Generate SQL code String for removing all {@link model.domain.Transaction Transaction}s from {@code transactions} table for all months with primary
	 * key equal to {@code monthID}.
	 * 
	 * @return an SQL code String (for use in a Statement) that will add a {@link model.domain.Transaction Transaction} to the database. This
	 *         SQL code contains '?' character(s) to avoid SQL injection techniques.<br>
	 *         <br>
	 * 
	 *         {@value #REMOVE_TRANSACTIONS_FOR_MONTH}. placeholder is replaced by {@code monthID} value.
	 */
	public static String removeTransactionsForMonth(int monthID) {
		return String.format(REMOVE_TRANSACTIONS_FOR_MONTH, monthID);
	}

	/**
	 * Generate SQL code String getting the {@code monthID} for a given {@link model.domain.Month Month} object's data.
	 * 
	 * @return an SQL code String (for use in a PreparedStatement) that retrieve the {@code monthID} column value for that month. This
	 *         SQL code contains '?' character(s) to avoid SQL injection techniques.<br>
	 *         <br>
	 * 
	 *         {@value #GET_MONTH_ID}. placeholder is replaced by {@code monthID} value.
	 */
	public static String getMonthID() {
		return GET_MONTH_ID;
	}

	//	public static String removeTransaction(Transaction t) {
	//		return String.format(REMOVE_TRANSACTION,
	//				t.getName(),
	//				(t.isPaid()) ? 1 : 0,
	//				(t.isIncome()) ? 1 : 0,
	//				t.getDate().format(Constants.FORMAT_YYYYMM),
	//				t.getType().toString(),
	//				t.getValue());
	//	}

}
