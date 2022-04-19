package model.db;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;

/**
 * Global constants for BudgetKeeper program.
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class Constants {

	/**
	 * Contains a blueprint for all necessary tables in database for BudgetKeeper program.
	 *
	 */
	public enum Tables {
		/**
		 * <pre>
		 * CREATE TABLE transactions (
		 * 	transactionID INTEGER NOT NULL,
		 * 	monthID INTEGER NOT NULL,
		 *  name TEXT,
		 *  paid INTEGER,
		 * 	income INTEGER,
		 * 	date TEXT,
		 * 	type TEXT,
		 * 	value REAL,
		 * 	PRIMARY KEY(transactionID),
		 * 	FOREIGN KEY(monthID) REFERENCES months (monthID)
		 * );
		 * </pre>
		 * 
		 */
		TRANSACTION("transactions", "transactionID", new String[] { "monthID", "months" }, new String[] { "transactionID INTEGER NOT NULL", "monthID INTEGER NOT NULL",
				"name TEXT", "paid INTEGER", "income INTEGER", "date TEXT",
				"type TEXT",
				"value REAL" }),
		/**
		 * <pre>
		 * CREATE TABLE months (
		 * 	monthID INTEGER NOT NULL,
		 * 	date TEXT,
		 * 	PRIMARY KEY(monthID)
		 * );
		 * </pre>
		 */
		MONTH("months", "monthID", null, new String[] { "monthID INTEGER NOT NULL", "date TEXT" });

		private String tableName;
		private String primaryKey;
		private String[] foreignKeys;
		private String[] columns;

		private Tables(String tableName, String primaryKey, String[] foreignKey, String[] columns) {
			this.tableName = tableName;
			this.primaryKey = primaryKey;
			this.foreignKeys = foreignKey;
			this.columns = columns;
		}

		public String tableName() {
			return this.tableName;
		}

		public String primaryKey() {
			return this.primaryKey;
		}

		public String[] foreignKeys() {
			return this.foreignKeys;
		}

		public String[] columns() {
			return this.columns;
		}
	}

	/**
	 * Contains locations for files used in the BudgetKeeper program.
	 * 
	 * @author Peter Marley
	 * @StudentNumber 13404067
	 * @Email pmarley03@qub.ac.uk
	 * @GitHub https://github.com/PeterMarley
	 *
	 */
	public enum Files {
		/**
		 * The location and filename of this programs database
		 */
		DATABASE("jdbc:sqlite:database.db");

		private String location;

		private Files(String location) {
			this.location = location;
		}

		public String toString() {
			return this.location;
		}
	}

	public static final String DB_LOCATION = "jdbc:sqlite:database.db";
	public static final String DATE_MONTH_YEAR_FORMAT = "yyyy/MM";
	public static final String DATE_DAY_MONTH_YEAR_FORMAT = "yyyy/MM/dd";

	public static final DateTimeFormatter FORMAT_YYYYMM = new DateTimeFormatterBuilder()
			.appendPattern(Constants.DATE_MONTH_YEAR_FORMAT)
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
			.toFormatter();

	public static final DateTimeFormatter FORMAT_YYYYMMDD = new DateTimeFormatterBuilder()
			.appendPattern(Constants.DATE_DAY_MONTH_YEAR_FORMAT)
			.toFormatter();

}
