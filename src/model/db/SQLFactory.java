package model.db;

import static model.domain.Utility.nullCheck;

import model.db.Constants.Tables;

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
	public static String SQLDropTable(Tables table) throws IllegalArgumentException {
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
	public static String SQLCreateTable(Tables table) throws IllegalArgumentException {
		nullCheck(table);
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE ");
		b.append(table.tableName());
		b.append(" (%n");
		for (int i = 0; i < table.columnSchema().length; i++) {
			b.append("\t");
			b.append(table.columnSchema()[i]);
			b.append(",%n");
		}
		if (table.primaryKey() != null) {
			b.append("\tPRIMARY KEY(");
			b.append(table.primaryKey());
			b.append(")");
		}
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

	/**
	 * <code>{@value}</code>
	 */
	public static final String INSERT_MONTH = "INSERT INTO months (date) VALUES (?);";

	/**
	 * <code>{@value}</code>
	 */
	public static final String INSERT_TRANSACTION = "INSERT INTO transactions (transactionID, monthID, name, paid, income, date, type, value) VALUES (?,?,?,?,?,?,?,?);";

	//**********************************\
	//									|
	//	READ							|
	//									|
	//**********************************/

	/**
	 * <code>{@value}</code>
	 */
	public static final String READ_MONTHS_WHERE_DATE = "SELECT monthID FROM months WHERE date=?;";

	/**
	 * <code>{@value}</code>
	 */
	public static final String READ_MONTHS = "SELECT * FROM months;";

	/**
	 * <code>{@value}</code>
	 */
	public static final String READ_TRANSACTIONS_WHERE_MONTHID = "SELECT * FROM transactions WHERE monthID=?;";
	//**********************************\
	//									|
	//	CREATE							|
	//									|
	//**********************************/

	//**********************************\
	//									|
	//	UPDATE							|
	//									|
	//**********************************/

	//**********************************\
	//									|
	//	DELETE							|
	//									|
	//**********************************/

}
