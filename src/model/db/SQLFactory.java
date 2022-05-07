package model.db;

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

	//**********************************\
	//									|
	//	CREATE							|
	//									|
	//**********************************/

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
	//	UPDATE							|
	//									|
	//**********************************/

	//**********************************\
	//									|
	//	DELETE							|
	//									|
	//**********************************/

}
